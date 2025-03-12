package ru.slavapmk.journalTracker.viewModels

import android.content.Context
import android.content.Intent
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.HorizontalAlignment
import ru.slavapmk.journalTracker.R
import ru.slavapmk.journalTracker.dataModels.StudentAttendanceLesson
import ru.slavapmk.journalTracker.dataModels.toEdit
import ru.slavapmk.journalTracker.excelExporter.BorderData
import ru.slavapmk.journalTracker.excelExporter.CellData
import ru.slavapmk.journalTracker.excelExporter.ExcelExporter
import ru.slavapmk.journalTracker.excelExporter.RenderData
import ru.slavapmk.journalTracker.storageModels.StorageDependencies
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.LocalDate
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar


class ExportDayViewModel : ViewModel() {
    private val weekdayNamesId: List<Int> by lazy {
        listOf(
            R.string.day_monday,
            R.string.day_tuesday,
            R.string.day_wednesday,
            R.string.day_thurday,
            R.string.day_friday,
            R.string.day_saturday,
            R.string.day_sunday
        )
    }

    val savedLiveStatus by lazy {
        MutableLiveData<Unit>()
    }
    val sharedLiveStatus by lazy {
        MutableLiveData<Intent?>()
    }
    val openLiveStatus by lazy {
        MutableLiveData<Intent?>()
    }
    val statusCallback by lazy {
        MutableLiveData<String?>()
    }

    fun saveExcel(context: Context, date: SimpleDate, group: String) {
        viewModelScope.launch {
            val workbook = parse(context, date, group)
            withContext(Dispatchers.IO) {
                val calendar: Calendar = GregorianCalendar.getInstance().apply { time = Date() }

                try {
                    val file = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        context.getString(
                            R.string.export_filename_excel,
                            calendar[Calendar.YEAR],
                            calendar[Calendar.MONTH] + 1,
                            calendar[Calendar.DAY_OF_MONTH],
                            calendar[Calendar.HOUR_OF_DAY],
                            calendar[Calendar.MINUTE],
                            calendar[Calendar.SECOND]
                        )
                    )
                    val outputStream = FileOutputStream(file)
                    workbook.export(outputStream)
                    outputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            savedLiveStatus.postValue(Unit)
        }
    }

    fun shareExcel(context: Context, date: SimpleDate, group: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val workbook = parse(context, date, group)
                val calendar: Calendar = GregorianCalendar.getInstance().apply { time = Date() }

                try {
                    val file = File(
                        context.cacheDir,
                        context.getString(
                            R.string.export_filename_excel,
                            calendar[Calendar.YEAR],
                            calendar[Calendar.MONTH] + 1,
                            calendar[Calendar.DAY_OF_MONTH],
                            calendar[Calendar.HOUR_OF_DAY],
                            calendar[Calendar.MINUTE],
                            calendar[Calendar.SECOND]
                        )
                    )
                    val outputStream = FileOutputStream(file)
                    workbook.export(outputStream)
                    outputStream.close()

                    val uri =
                        FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "application/vnd.ms-excel"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    sharedLiveStatus.postValue(
                        Intent.createChooser(
                            intent, context.getString(R.string.share_via)
                        )
                    )
                } catch (e: IOException) {
                    e.printStackTrace()
                    sharedLiveStatus.postValue(null)
                }
            }
        }
    }

    fun openExcel(context: Context, date: SimpleDate, group: String) {
        viewModelScope.launch {
            val workbook = parse(context, date, group)
            withContext(Dispatchers.IO) {
                val calendar: Calendar = GregorianCalendar.getInstance().apply { time = Date() }

                try {
                    val file = File(
                        context.cacheDir,
                        context.getString(
                            R.string.export_filename_excel,
                            calendar[Calendar.YEAR],
                            calendar[Calendar.MONTH] + 1,
                            calendar[Calendar.DAY_OF_MONTH],
                            calendar[Calendar.HOUR_OF_DAY],
                            calendar[Calendar.MINUTE],
                            calendar[Calendar.SECOND]
                        )
                    )
                    val outputStream = FileOutputStream(file)
                    workbook.export(outputStream)
                    outputStream.close()

                    val uri =
                        FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(uri, "application/vnd.ms-excel")
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    openLiveStatus.postValue(
                        Intent.createChooser(
                            intent, context.getString(R.string.share_via)
                        )
                    )
                } catch (e: IOException) {
                    e.printStackTrace()
                    openLiveStatus.postValue(null)
                }
            }
        }
    }

    private suspend fun parse(
        context: Context, date: SimpleDate, group: String
    ) = withContext(Dispatchers.IO) {
        statusCallback.postValue(
            context.getString(
                R.string.export_collecting_data
            )
        )
        val sheetNames = listOf(
            context.getString(
                R.string.exporter_date,
                date.day, date.month, date.year,
                context.getString(
                    weekdayNamesId[
                        LocalDate.of(date.year, date.month, date.day).dayOfWeek.value - 1
                    ]
                )
            )
        )
        val exporter = ExcelExporter(
            sheetNames,
            creator = "Journal Exporter",
            title = "Attendance Journal"
        )

        val dayData = generateDay(context, date, group)

        exporter.insertData(sheetNames[0], dayData)
        exporter.resizeWorkbook()

        statusCallback.postValue(
            context.getString(
                R.string.export_processing_file
            )
        )
        return@withContext exporter
    }

    private suspend fun generateDay(
        context: Context,
        date: SimpleDate,
        group: String
    ): RenderData {
        val resultCells = mutableListOf<CellData>()
        val resultBorders = mutableListOf<BorderData>()

        // Student number title
        resultCells.add(
            CellData(
                0,
                1,
                context.getString(R.string.exporter_num),
                endRow = 3
            )
        )

        // Student name title
        resultCells.add(
            CellData(
                1,
                1,
                context.getString(R.string.exporter_name),
                endRow = 3
            )
        )

        val studentEntityList = withContext(Dispatchers.IO) {
            StorageDependencies.studentRepository.getStudents()
        }

        // Student names
        resultCells.addAll(
            studentEntityList.mapIndexed { index, studentEntity ->
                CellData(
                    1,
                    4 + index,
                    studentEntity.name,
                    alignment = HorizontalAlignment.LEFT
                )
            }
        )

        // Student numbers
        resultCells.addAll(
            List(studentEntityList.size) { index ->
                CellData(
                    0,
                    4 + index,
                    index + 1,
                    alignment = HorizontalAlignment.RIGHT
                )
            }
        )

        val lessonListWithAttendance = withContext(Dispatchers.IO) {
            StorageDependencies.lessonInfoRepository.getLessonsByDate(
                date.day,
                date.month,
                date.year
            ).map {
                it to StorageDependencies.studentsAttendanceRepository.getStudentAttendanceWithNames(
                    it.id
                )
            }
        }

        // Date title
        resultCells.add(
            CellData(
                2, 1,
                context.getString(
                    R.string.exporter_date,
                    date.day, date.month, date.year,
                    context.getString(
                        weekdayNamesId[
                            LocalDate.of(date.year, date.month, date.day).dayOfWeek.value - 1
                        ]
                    )
                ),
                endColumn = 2 + lessonListWithAttendance.size - 1
            )
        )

        val studentsSumDisrespect = mutableMapOf<Int, Int>()
        val studentsSumRespect = mutableMapOf<Int, Int>()

        // Lessons with attendance
        for ((lessonIndex, listPair) in lessonListWithAttendance.withIndex()) {
            val (lesson, students) = listPair
            resultCells.add(
                CellData(
                    2 + lessonIndex,
                    2,
                    context.getString(
                        lesson.type.toEdit().shortNameRes
                    )
                )
            )
            resultCells.add(
                CellData(
                    2 + lessonIndex,
                    3,
                    lesson.name,
                    rotation = 90
                )
            )
            for ((studentIndex, student) in students.withIndex()) {
                val toEdit = student.attendance.attendance.toEdit()
                when (toEdit) {
                    StudentAttendanceLesson.NULL -> {}
                    StudentAttendanceLesson.VISIT -> {}
                    StudentAttendanceLesson.NOT_VISIT ->
                        studentsSumDisrespect.compute(studentIndex) { _, oldValue ->
                            (oldValue ?: 0) + 2
                        }

                    StudentAttendanceLesson.SICK ->
                        studentsSumDisrespect.compute(studentIndex) { _, oldValue ->
                            (oldValue ?: 0) + 2
                        }

                    StudentAttendanceLesson.SICK_WITH_CERTIFICATE ->
                        studentsSumRespect.compute(studentIndex) { _, oldValue ->
                            (oldValue ?: 0) + 2
                        }

                    StudentAttendanceLesson.RESPECTFUL_PASS ->
                        studentsSumRespect.compute(studentIndex) { _, oldValue ->
                            (oldValue ?: 0) + 2
                        }

                    null -> {}
                }
                val skipped = toEdit?.displayNameRes?.let { context.getString(it) } ?: ""
                resultCells.add(
                    CellData(
                        2 + lessonIndex,
                        4 + studentIndex,
                        skipped
                    )
                )
            }
        }

        // Skipped title
        resultCells.add(
            CellData(
                2 + lessonListWithAttendance.size, 1,
                context.getString(
                    R.string.exporter_hour_skipped
                ),
                endColumn = 2 + lessonListWithAttendance.size + 1
            )
        )

        // Skipped respectful title
        resultCells.add(
            CellData(
                2 + lessonListWithAttendance.size, 2,
                context.getString(
                    R.string.exporter_hour_skipped_respectful
                ),
                endRow = 3,
                rotation = 90
            )
        )

        // Skipped disrespectful title
        resultCells.add(
            CellData(
                2 + lessonListWithAttendance.size + 1, 2,
                context.getString(
                    R.string.exporter_hour_skipped_disrespectful
                ),
                endRow = 3,
                rotation = 90
            )
        )

        // Skipped hours
        resultCells.addAll(
            studentsSumDisrespect.map {
                CellData(
                    2 + lessonListWithAttendance.size + 1,
                    4 + it.key,
                    it.value
                )
            }
        )
        resultCells.addAll(
            studentsSumRespect.map {
                CellData(
                    2 + lessonListWithAttendance.size,
                    4 + it.key,
                    it.value
                )
            }
        )

        // Group name
        resultCells.add(
            CellData(
                0, 0,
                context.getString(R.string.exporter_group, group),
                endColumn = 2 + lessonListWithAttendance.size + 1
            )
        )

        // Students rows
        resultBorders.addAll(
            List(studentEntityList.size) { i ->
                BorderData(
                    0, 4 + i,
                    2 + lessonListWithAttendance.size + 1, 4 + i,
                    BorderStyle.THIN,
                    BorderStyle.THIN
                )
            }
        )

        // Students
        resultBorders.add(
            BorderData(
                0, 4,
                1, 4 + studentEntityList.size - 1,
                BorderStyle.THICK,
                BorderStyle.THIN
            )
        )

        // Lesson types
        resultBorders.add(
            BorderData(
                2, 2,
                2 + lessonListWithAttendance.size - 1, 2,
                BorderStyle.THICK,
                BorderStyle.THIN
            )
        )

        // Hours skipped
        resultBorders.add(
            BorderData(
                2, 2,
                2 + lessonListWithAttendance.size - 1, 2,
                BorderStyle.THICK,
                BorderStyle.THIN
            )
        )

        // Hours skipped
        resultBorders.add(
            BorderData(
                2 + lessonListWithAttendance.size, 1,
                3 + lessonListWithAttendance.size, 1,
                BorderStyle.THICK
            )
        )

        // Only date border
        resultBorders.add(
            BorderData(
                2, 1,
                2 + lessonListWithAttendance.size - 1,
                1,
                BorderStyle.THICK
            )
        )

        // Attendance border
        resultBorders.add(
            BorderData(
                2 + lessonListWithAttendance.size, 1,
                2 + lessonListWithAttendance.size + 1,
                3 + studentEntityList.size,
                BorderStyle.THICK
            )
        )

        // Header border
        resultBorders.add(
            BorderData(
                0, 1,
                2 + lessonListWithAttendance.size + 1,
                3,
                BorderStyle.THICK
            )
        )

        // Group name border
        resultBorders.add(
            BorderData(
                0, 0,
                2 + lessonListWithAttendance.size + 1,
                0,
                BorderStyle.THICK
            )
        )

        // Date lessons borders
        resultBorders.add(
            BorderData(
                2, 1,
                2 + lessonListWithAttendance.size - 1,
                3 + studentEntityList.size,
                BorderStyle.THICK
            )
        )

        return RenderData(
            resultCells, resultBorders,
            1, 3
        )
    }
}
