package ru.slavapmk.journalTracker.attendanceSynchronize

import android.content.Context
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.HorizontalAlignment
import ru.slavapmk.journalTracker.R
import ru.slavapmk.journalTracker.dataModels.StudentAttendanceLesson
import ru.slavapmk.journalTracker.dataModels.selectWeek.Semester
import ru.slavapmk.journalTracker.dataModels.selectWeek.Week
import ru.slavapmk.journalTracker.dataModels.toEdit
import ru.slavapmk.journalTracker.storageModels.StorageDependencies
import ru.slavapmk.journalTracker.storageModels.entities.LessonInfoEntity
import ru.slavapmk.journalTracker.storageModels.entities.SemesterEntity
import ru.slavapmk.journalTracker.storageModels.entities.StudentAttendanceEntity
import ru.slavapmk.journalTracker.storageModels.entities.StudentEntity
import ru.slavapmk.journalTracker.utils.generateWeeks
import ru.slavapmk.journalTracker.viewModels.SimpleDate
import ru.slavapmk.journalTracker.viewModels.compareTo
import java.time.LocalDate

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

class AttendanceExporter(
    private val statusCallback: MutableLiveData<String?>
) {
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


    private suspend fun getWeeks(semesterId: Int): List<Week>? {
        val semesters = withContext(Dispatchers.IO) {
            StorageDependencies.semesterRepository.getSemesters()
        }
        if (semesters.isEmpty()) {
            return null
        }
        val semester: SemesterEntity = semesters.find { it.id == semesterId } ?: return null
        return generateWeeks(
            Semester(
                semester.startDay,
                semester.startMonth,
                semester.startYear,
                semester.endDay,
                semester.endMonth,
                semester.endYear,
            )
        )
    }

    private fun genDates(from: SimpleDate, to: SimpleDate): MutableList<SimpleDate> {
        val result: MutableList<SimpleDate> = mutableListOf()

        var current = from.toLocalDate()
        val end = to.toLocalDate()

        while (!current.isAfter(end)) {
            result.add(SimpleDate(current.dayOfMonth, current.monthValue, current.year))
            current = current.plusDays(1)
        }

        return result
    }

    private suspend fun getWeek(semesterId: Int, date: SimpleDate): Pair<SimpleDate, SimpleDate>? {
        val semesters = withContext(Dispatchers.IO) {
            StorageDependencies.semesterRepository.getSemesters()
        }
        if (semesters.isEmpty()) {
            return null
        }
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

    suspend fun parseSemester(
        context: Context, semesterId: Int, groupName: String
    ) = withContext(Dispatchers.Default) {
        val weeks: List<Week> = getWeeks(semesterId) ?: return@withContext ExcelExporter(
            listOf(),
            creator = "Journal Exporter",
            title = "Attendance Journal"
        )
        val sheetNames = MutableList(weeks.size) { i ->
            context.getString(
                R.string.exporter_week_id,
                i + 1
            )
        }
        sheetNames.add(
            context.getString(
                R.string.exporter_overview
            )
        )
        val exporter = ExcelExporter(
            sheetNames,
            creator = "Journal Exporter",
            title = "Attendance Journal"
        )
        val genDays = weeks.map { week ->
            genDates(
                SimpleDate(week.startDay, week.startMonth, week.startYear),
                SimpleDate(week.endDay, week.endMonth, week.endYear)
            )
        }

        val allAttendances = mutableMapOf<Int, StudentAttendance>()

        for ((i, week) in genDays.withIndex()) {
            statusCallback.postValue(
                context.getString(
                    R.string.export_collecting_week,
                    i + 1
                )
            )
            val (attendances, insertDataList) = renderDateList(
                context, week,
                groupName
            )
            for ((key, value) in attendances.entries) {
                allAttendances.compute(key) { _, oldValue ->
                    if (oldValue == null) {
                        value
                    } else {
                        oldValue + value
                    }
                }
            }
            for (insertData in insertDataList) {
                exporter.insertData(sheetNames[i], insertData)
            }
        }

        for (renderData in renderOverview(
            context,
            allAttendances,
            groupName
        )) {
            exporter.insertData(
                sheetNames.last(),
                renderData
            )
        }

        exporter.resizeWorkbook()

        statusCallback.postValue(
            context.getString(
                R.string.export_processing_file
            )
        )
        return@withContext exporter
    }

    suspend fun parseWeek(
        context: Context, semesterId: Int, date: SimpleDate, groupName: String
    ) = withContext(Dispatchers.Default) {
        statusCallback.postValue(
            context.getString(
                R.string.export_collecting_data
            )
        )
        val dates: Pair<SimpleDate, SimpleDate> = getWeek(
            semesterId, date
        ) ?: return@withContext ExcelExporter(
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

        val (_, insertDataList) = renderDateList(
            context,
            genDates(dates.first, dates.second),
            groupName
        )
        for (insertData in insertDataList) {
            exporter.insertData(sheetNames[0], insertData)
        }

        exporter.resizeWorkbook()

        statusCallback.postValue(
            context.getString(
                R.string.export_processing_file
            )
        )
        return@withContext exporter
    }

    suspend fun parseDate(
        context: Context, date: SimpleDate, groupName: String
    ) = withContext(Dispatchers.Default) {
        statusCallback.postValue(
            context.getString(
                R.string.export_collecting_data
            )
        )
        val weekdayName = context.getString(
            weekdayNamesId[
                LocalDate.of(date.year, date.month, date.day).dayOfWeek.value - 1
            ]
        )
        val sheetNames = listOf(
            context.getString(
                R.string.exporter_date,
                date.day, date.month, date.year, weekdayName
            )
        )
        val exporter = ExcelExporter(
            sheetNames,
            creator = "Journal Exporter",
            title = "Attendance Journal"
        )

        val (_, insertDataList) = renderDateList(
            context,
            listOf(date),
            groupName
        )
        for (insertData in insertDataList) {
            exporter.insertData(sheetNames[0], insertData)
        }

        exporter.resizeWorkbook()

        statusCallback.postValue(
            context.getString(
                R.string.export_processing_file
            )
        )
        return@withContext exporter
    }

    private suspend fun renderOverview(
        context: Context,
        attendances: Map<Int, StudentAttendance>,
        group: String
    ): List<RenderData> {
        val result = mutableListOf<RenderData>()

        val (renderStudents, students) = renderOrGetStudentNames(context)
        result.add(renderStudents.apply {
            offsetRow = 1
        })

        result.add(
            renderSummary(
                context,
                attendances,
                students
            ).apply {
                offsetColumn = 3
                offsetRow = 1
            }
        )

        result.add(
            RenderData(
                listOf(
                    CellData(
                        0, 0,
                        context.getString(R.string.exporter_group, group),
                        endColumn = 4
                    )
                ),
                listOf(
                    BorderData(
                        0, 0,
                        4, 0,
                        BorderStyle.THICK,
                    )
                ),
                freezeRow = 4,
                freezeColumn = 2
            )
        )

        return result
    }

    private suspend fun renderDateList(
        context: Context,
        dates: List<SimpleDate>,
        group: String
    ): Pair<Map<Int, StudentAttendance>, List<RenderData>> {
        val result = mutableListOf<RenderData>()

        val (renderStudents, students) = renderOrGetStudentNames(context)
        result.add(renderStudents.apply {
            offsetRow = 1
        })

        val attendances: MutableList<Map<Int, StudentAttendance>> = mutableListOf()

        var offset = 3
        for (date in dates) {
            val (count, sumAttendances, renderLessons) = renderDate(
                context, date, students
            )
            attendances.add(sumAttendances)
            result.add(renderLessons.apply {
                offsetColumn = offset
                offsetRow = 1
            })
            offset += count
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
                students
            ).apply {
                offsetColumn = offset
                offsetRow = 1
            }
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
                freezeRow = 4,
                freezeColumn = 2
            )
        )

        return summedAttendance to result
    }

    private var renderedStudents: Pair<RenderData, List<StudentEntity>>? = null
    private suspend fun renderOrGetStudentNames(context: Context): Pair<RenderData, List<StudentEntity>> {
        if (renderedStudents == null) {
            renderedStudents = renderStudentNames(context)
        }
        return renderedStudents!!
    }

    private suspend fun renderStudentNames(context: Context): Pair<RenderData, List<StudentEntity>> {
        val resultCells = mutableListOf<CellData>()
        val resultBorders = mutableListOf<BorderData>()

        // Student DB id title
        resultCells.add(
            CellData(
                0,
                0,
                context.getString(R.string.exporter_id),
                endRow = 3,
                rotation = 90
            )
        )

        // Student number title
        resultCells.add(
            CellData(
                1,
                0,
                context.getString(R.string.exporter_num),
                endRow = 3
            )
        )

        // Student name title
        resultCells.add(
            CellData(
                2,
                0,
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
                    2,
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
                    1,
                    4 + index,
                    index + 1,
                    alignment = HorizontalAlignment.RIGHT
                )
            }
        )

        // Student numbers
        resultCells.addAll(
            studentEntityList.mapIndexed { index, studentEntity ->
                CellData(
                    0,
                    4 + index,
                    "#${studentEntity.id}",
                    alignment = HorizontalAlignment.RIGHT
                )
            }
        )

        resultBorders.apply {
            add(
                BorderData(
                    0, 0,
                    2, 3,
                    BorderStyle.THICK
                )
            )
            add(
                BorderData(
                    1, 0,
                    1, 3,
                    BorderStyle.THIN
                )
            )
            add(
                BorderData(
                    0, 4,
                    2, 3 + studentEntityList.size,
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

    private suspend fun renderDate(
        context: Context, date: SimpleDate, allStudents: List<StudentEntity>
    ): Triple<Int, Map<Int, StudentAttendance>, RenderData> {
        val resultCells = mutableListOf<CellData>()
        val resultBorders = mutableListOf<BorderData>()

        val lessonListWithAttendance = withContext(Dispatchers.IO) {
            StorageDependencies.lessonInfoRepository.getLessonsByDate(
                date.day,
                date.month,
                date.year
            ).map {
                it to getFullAttendances(it, allStudents)
            }
        }

        if (lessonListWithAttendance.isEmpty()) {
            return Triple(0, mapOf(), RenderData(resultCells, resultBorders))
        }

        val studentsSum = mutableMapOf<Int, StudentAttendance>()

        val weekdayName = context.getString(
            weekdayNamesId[
                LocalDate.of(date.year, date.month, date.day).dayOfWeek.value - 1
            ]
        )
        resultCells.add(
            CellData(
                0,
                0,
                context.getString(
                    R.string.exporter_date,
                    date.day, date.month, date.year,
                    weekdayName
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
            resultCells.add(
                CellData(
                    lessonIndex,
                    3,
                    "#${lesson.id}",
                    rotation = 90
                )
            )
            for (student in students) {
                val studentIndex = allStudents.indexOfFirst {
                    it.id == student.studentId
                }
                val toEdit = student.attendance.toEdit()
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
                val color = toEdit?.color?.let {
                    val color = context.getColor(it)
                    SimpleColor(color.red, color.green, color.blue)
                }
                resultCells.add(
                    CellData(
                        lessonIndex,
                        studentIndex + 4,
                        skipped,
                        backgroundColor = color
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
                    lessonListWithAttendance.size - 1, 3,
                    BorderStyle.THICK,
                    BorderStyle.THIN,
                )
            )
            add(
                BorderData(
                    0, 4,
                    lessonListWithAttendance.size - 1, 3 + allStudents.size,
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
                resultBorders
            )
        )
    }

    private suspend fun getFullAttendances(
        it: LessonInfoEntity, allStudents: List<StudentEntity>
    ): MutableList<StudentAttendanceEntity> {
        val lessonAttendance = StorageDependencies.studentsAttendanceRepository.getLessonAttendance(
            it.id
        ).associateBy { it.studentId }
        val toInit = mutableListOf<StudentAttendanceEntity>()
        val allAttendance = mutableListOf<StudentAttendanceEntity>()
        for (allStudent in allStudents) {
            val attendance = lessonAttendance[allStudent.id]
            if (attendance == null) {
                val element = StudentAttendanceEntity(
                    0,
                    allStudent.id,
                    it.id,
                    allStudent.default,
                    null
                )
                toInit.add(element)
                allAttendance.add(element)
            } else {
                allAttendance.add(attendance)
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            StorageDependencies.studentsAttendanceRepository.insertAttendances(toInit)
        }

        return allAttendance
    }

    private fun renderSummary(
        context: Context,
        summedAttendance: Map<Int, StudentAttendance>,
        students: List<StudentEntity>
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
                endRow = 3,
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
                endRow = 3,
                rotation = 90
            )
        )

        for ((index, entry) in summedAttendance.entries) {
            resultCells.add(
                CellData(
                    0, index + 4,
                    entry.respectful
                )
            )
            resultCells.add(
                CellData(
                    1, index + 4,
                    entry.disrespectful
                )
            )
        }

        resultBorders.apply {
            add(
                BorderData(
                    0, 1,
                    0, 3,
                    BorderStyle.THIN
                )
            )
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
                    1, 3,
                    BorderStyle.THICK
                )
            )
            add(
                BorderData(
                    0, 4,
                    1, 3 + students.size,
                    BorderStyle.THICK,
                    BorderStyle.THIN,
                )
            )
        }

        return RenderData(
            resultCells,
            resultBorders
        )
    }
}