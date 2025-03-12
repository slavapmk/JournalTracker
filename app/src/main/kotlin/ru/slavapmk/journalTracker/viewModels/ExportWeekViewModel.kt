package ru.slavapmk.journalTracker.viewModels

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Environment
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
import ru.slavapmk.journalTracker.dataModels.selectWeek.Semester
import ru.slavapmk.journalTracker.dataModels.toEdit
import ru.slavapmk.journalTracker.excelExporter.BorderData
import ru.slavapmk.journalTracker.excelExporter.CellData
import ru.slavapmk.journalTracker.excelExporter.ExcelExporter
import ru.slavapmk.journalTracker.excelExporter.RenderData
import ru.slavapmk.journalTracker.storageModels.StorageDependencies
import ru.slavapmk.journalTracker.storageModels.entities.SemesterEntity
import ru.slavapmk.journalTracker.storageModels.entities.StudentEntity
import ru.slavapmk.journalTracker.ui.SharedKeys
import ru.slavapmk.journalTracker.utils.generateWeeks
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar

data class StudentAttendance(
    val respectful: Int = 0,
    val disrespectful: Int = 0
) {
    operator fun plus(other: StudentAttendance): StudentAttendance {
        return StudentAttendance(
            respectful + other.respectful,
            disrespectful + other.disrespectful
        )
    }
}

class ExportWeekViewModel : ViewModel() {
    val savedLiveStatus by lazy {
        MutableLiveData<Unit>()
    }
    val sharedLiveStatus by lazy {
        MutableLiveData<Intent?>()
    }
    val openLiveStatus by lazy {
        MutableLiveData<Intent?>()
    }

    lateinit var shared: SharedPreferences

    private var _date: SimpleDate?
        get() = if (
            shared.contains(SharedKeys.SELECTED_DAY) &&
            shared.contains(SharedKeys.SELECTED_MONTH) &&
            shared.contains(SharedKeys.SELECTED_YEAR)
        ) {
            SimpleDate(
                shared.getInt(SharedKeys.SELECTED_DAY, -1),
                shared.getInt(SharedKeys.SELECTED_MONTH, -1),
                shared.getInt(SharedKeys.SELECTED_YEAR, -1),
            )
        } else {
            null
        }
        set(value) {
            if (value == null) {
                shared.edit()?.apply {
                    remove(SharedKeys.SELECTED_DAY)
                    remove(SharedKeys.SELECTED_MONTH)
                    remove(SharedKeys.SELECTED_YEAR)
                    apply()
                }
            } else {
                shared.edit()?.apply {
                    putInt(SharedKeys.SELECTED_DAY, value.day)
                    putInt(SharedKeys.SELECTED_MONTH, value.month)
                    putInt(SharedKeys.SELECTED_YEAR, value.year)
                    apply()
                }
            }
        }

    val date: SimpleDate
        get() =
            if (_date == null) {
                val now = nowDate()
                _date = now
                now
            } else {
                _date!!
            }

    private fun nowDate(): SimpleDate {
        val calendar: Calendar = GregorianCalendar.getInstance().apply {
            time = Date()
        }
        return SimpleDate(
            calendar[Calendar.DAY_OF_MONTH],
            calendar[Calendar.MONTH] + 1,
            calendar[Calendar.YEAR]
        )
    }

    private suspend fun getWeek(): Pair<SimpleDate, SimpleDate>? {
        val semesters = StorageDependencies.semesterRepository.getSemesters()
        if (semesters.isEmpty()) {
            return null
        }
        val semesterId = shared.getInt(SharedKeys.SEMESTER_ID, -1)
        val semester: SemesterEntity = semesters.find { it.id == semesterId } ?: return null
        val weeks = generateWeeks(
            Semester(
                semester.startDay,
                semester.startMonth,
                semester.startYear,
                semester.endDay,
                semester.endMonth,
                semester.endYear,
            )
        )
        val week = weeks.find { week ->
            val startDate = SimpleDate(
                week.startDay,
                week.startMonth,
                week.startYear
            )
            val endDate = SimpleDate(
                week.endDay,
                week.endMonth,
                week.endYear
            )
            return@find startDate <= date && date <= endDate
        }
        return week?.let {
            SimpleDate(
                it.startDay,
                it.startMonth,
                it.startYear
            ) to SimpleDate(
                it.endDay,
                it.endMonth,
                it.endYear
            )
        }
    }

    private fun genDates(from: SimpleDate, to: SimpleDate): MutableList<SimpleDate> {
        val result: MutableList<SimpleDate> = mutableListOf()
        for (year in from.year..to.year) {
            for (month in from.month..to.month) {
                for (day in from.day..to.day) {
                    result.add(
                        SimpleDate(day, month, year)
                    )
                }
            }
        }
        return result
    }

    fun saveExcel(context: Context) {
        viewModelScope.launch {
            val workbook = parse(context)
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

    private suspend fun parse(
        context: Context
    ) = withContext(Dispatchers.IO) {
        val dates: Pair<SimpleDate, SimpleDate> = getWeek() ?: return@withContext ExcelExporter(
            listOf(),
            creator = "Journal Exporter",
            title = "Attendance Journal"
        )
        val sheetNames = listOf(
            context.getString(
                R.string.exporter_week,
                dates.first.day, dates.first.month, dates.first.year,
                dates.second.day, dates.second.month, dates.second.year
            )
        )
        val exporter = ExcelExporter(
            sheetNames,
            creator = "Journal Exporter",
            title = "Attendance Journal"
        )

        val insertDataList = generateWeek(
            context, genDates(dates.first, dates.second),
            shared.getString(SharedKeys.GROUP_NAME_KEY, "") ?: ""
        )
        for (insertData in insertDataList) {
            exporter.insertData(sheetNames[0], insertData)
        }

        exporter.resizeWorkbook()

        return@withContext exporter
    }


    private suspend fun generateWeek(
        context: Context,
        dates: List<SimpleDate>,
        group: String
    ): List<RenderData> {
        val result = mutableListOf<RenderData>()

        val (renderStudents, students) = renderStudentNames(context)
        result.add(renderStudents)

        val attendances: MutableList<Map<Int, StudentAttendance>> = mutableListOf()

        var offset = 2
        for (date in dates) {
            val (count, sumAttendances, renderLessons) = renderLessons(
                context, date, students, offset
            )
            offset += count
            attendances.add(sumAttendances)
            result.add(renderLessons)
        }

        val summedAttendance: Map<Int, StudentAttendance> = attendances
            .fold(mutableMapOf()) { acc, map ->
                map.forEach { (key, value) ->
                    acc[key] = acc.getOrDefault(key, StudentAttendance()) + value
                }
                acc
            }

        result.add(
            renderSummary(
                context,
                summedAttendance,
                students,
                offset
            )
        )

        result.add(
            RenderData(
                listOf(
                    CellData(
                        0, 0,
                        context.getString(R.string.exporter_group, group),
                        endColumn = offset + 1
                    )
                ),
                listOf(
                    BorderData(
                        0, 0,
                        offset + 1, 0,
                        BorderStyle.THICK,
                    )
                ),
//                freezeRow = 3,
//                freezeColumn = 1
            )
        )

        return result
    }

    private fun renderSummary(
        context: Context,
        summedAttendance: Map<Int, StudentAttendance>,
        students: List<StudentEntity>,
        offset: Int
    ): RenderData {
        val resultCells = mutableListOf<CellData>()
        val resultBorders = mutableListOf<BorderData>()


        // Skipped title
        resultCells.add(
            CellData(
                0, 0,
                context.getString(
                    R.string.exporter_hour_skipped
                ),
                endColumn = 1
            )
        )

        // Skipped respectful title
        resultCells.add(
            CellData(
                0, 1,
                context.getString(
                    R.string.exporter_hour_skipped_respectful
                ),
                endRow = 2,
                rotation = 90
            )
        )

        // Skipped disrespectful title
        resultCells.add(
            CellData(
                1, 1,
                context.getString(
                    R.string.exporter_hour_skipped_disrespectful
                ),
                endRow = 2,
                rotation = 90
            )
        )

        for ((index, entry) in summedAttendance.entries) {
            resultCells.add(
                CellData(
                    0, index + 3,
                    entry.respectful
                )
            )
            resultCells.add(
                CellData(
                    1, index + 3,
                    entry.disrespectful
                )
            )
        }

        resultBorders.apply {
            add(
                BorderData(
                    0, 0,
                    1, 0,
                    BorderStyle.THICK
                )
            )
            add(
                BorderData(
                    0, 1,
                    1, 2,
                    BorderStyle.THICK
                )
            )
            add(
                BorderData(
                    0, 3,
                    1, 2 + students.size,
                    BorderStyle.THICK,
                    BorderStyle.THIN,
                )
            )
        }

        return RenderData(
            resultCells,
            resultBorders,
            offsetColumn = offset,
            offsetRow = 1
        )
    }

    private suspend fun renderLessons(
        context: Context, date: SimpleDate, allStudents: List<StudentEntity>,
        offset: Int
    ): Triple<Int, Map<Int, StudentAttendance>, RenderData> {
        val resultCells = mutableListOf<CellData>()
        val resultBorders = mutableListOf<BorderData>()

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

        if (lessonListWithAttendance.isEmpty()) {
            return Triple(0, mapOf(), RenderData(resultCells, resultBorders))
        }

        val studentsSum = mutableMapOf<Int, StudentAttendance>()

        resultCells.add(
            CellData(
                0,
                0,
                context.getString(
                    R.string.exporter_date,
                    date.day, date.month, date.year
                ),
                endColumn = lessonListWithAttendance.size - 1
            )
        )

        // Lessons with attendance
        for ((lessonIndex, listPair) in lessonListWithAttendance.withIndex()) {
            val (lesson, students) = listPair
            resultCells.add(
                CellData(
                    lessonIndex,
                    1,
                    context.getString(
                        lesson.type.toEdit().shortNameRes
                    )
                )
            )
            resultCells.add(
                CellData(
                    lessonIndex,
                    2,
                    lesson.name,
                    rotation = 90
                )
            )
            for (student in students) {
                val studentIndex = allStudents.indexOfFirst {
                    it.id == student.student.id
                }
                val toEdit = student.attendance.attendance.toEdit()
                when (toEdit) {
                    StudentAttendanceLesson.NULL -> studentsSum.compute(studentIndex) { _, oldValue ->
                        oldValue ?: StudentAttendance()
                    }

                    StudentAttendanceLesson.VISIT -> studentsSum.compute(studentIndex) { _, oldValue ->
                        oldValue ?: StudentAttendance()
                    }

                    StudentAttendanceLesson.NOT_VISIT ->
                        studentsSum.compute(studentIndex) { _, oldValue ->
                            (oldValue ?: StudentAttendance()) + StudentAttendance(0, 2)
                        }

                    StudentAttendanceLesson.SICK ->
                        studentsSum.compute(studentIndex) { _, oldValue ->
                            (oldValue ?: StudentAttendance()) + StudentAttendance(0, 2)
                        }

                    StudentAttendanceLesson.SICK_WITH_CERTIFICATE ->
                        studentsSum.compute(studentIndex) { _, oldValue ->
                            (oldValue ?: StudentAttendance()) + StudentAttendance(2, 0)
                        }

                    StudentAttendanceLesson.RESPECTFUL_PASS ->
                        studentsSum.compute(studentIndex) { _, oldValue ->
                            (oldValue ?: StudentAttendance()) + StudentAttendance(2, 0)
                        }

                    null -> studentsSum.compute(studentIndex) { _, oldValue ->
                        oldValue ?: StudentAttendance()
                    }
                }
                val skipped = toEdit?.displayNameRes?.let { context.getString(it) } ?: ""
                resultCells.add(
                    CellData(
                        lessonIndex,
                        studentIndex + 3,
                        skipped
                    )
                )
            }
        }

        resultBorders.apply {
            add(
                BorderData(
                    0, 0,
                    lessonListWithAttendance.size - 1, 0,
                    BorderStyle.THICK
                )
            )
            add(
                BorderData(
                    0, 1,
                    lessonListWithAttendance.size - 1, 1,
                    BorderStyle.THICK,
                    BorderStyle.THIN,
                )
            )
            add(
                BorderData(
                    0, 2,
                    lessonListWithAttendance.size - 1, 2,
                    BorderStyle.THICK,
                    BorderStyle.THIN,
                )
            )
            add(
                BorderData(
                    0, 3,
                    lessonListWithAttendance.size - 1, 2 + allStudents.size,
                    BorderStyle.THICK,
                    BorderStyle.THIN,
                )
            )
        }

        return Triple(
            lessonListWithAttendance.size,
            studentsSum,
            RenderData(
                resultCells,
                resultBorders,
                offsetColumn = offset,
                offsetRow = 1
            )
        )
    }

    private suspend fun renderStudentNames(context: Context): Pair<RenderData, List<StudentEntity>> {
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

        resultBorders.apply {
            add(
                BorderData(
                    0, 0,
                    1, 3,
                    BorderStyle.THICK
                )
            )
            add(
                BorderData(
                    0, 4,
                    1, 3 + studentEntityList.size,
                    BorderStyle.THICK,
                    BorderStyle.THIN,
                )
            )
        }

        return RenderData(
            resultCells,
            resultBorders
        ) to studentEntityList
    }
}