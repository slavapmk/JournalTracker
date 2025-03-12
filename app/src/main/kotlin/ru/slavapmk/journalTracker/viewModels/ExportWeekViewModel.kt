package ru.slavapmk.journalTracker.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.HorizontalAlignment
import ru.slavapmk.journalTracker.R
import ru.slavapmk.journalTracker.dataModels.StudentAttendanceLesson
import ru.slavapmk.journalTracker.dataModels.toEdit
import ru.slavapmk.journalTracker.excelExporter.BorderData
import ru.slavapmk.journalTracker.excelExporter.CellData
import ru.slavapmk.journalTracker.excelExporter.ExcelExporter
import ru.slavapmk.journalTracker.excelExporter.RenderData
import ru.slavapmk.journalTracker.storageModels.StorageDependencies
import ru.slavapmk.journalTracker.storageModels.entities.StudentEntity

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

    private suspend fun parse(
        context: Context, dates: Pair<SimpleDate, SimpleDate>, group: String
    ) = withContext(Dispatchers.IO) {
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

        val insertDataList = generateWeek(context, genDates(dates.first, dates.second), group)
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
                offset
            )
        )

        return result
    }

    private fun renderSummary(
        context: Context,
        summedAttendance: Map<Int, StudentAttendance>,
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
                endColumn = 1 + 1
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
                    0, index + 2,
                    entry.respectful
                )
            )
            resultCells.add(
                CellData(
                    1, index + 2,
                    entry.disrespectful
                )
            )
        }

        return RenderData(
            resultCells,
            resultBorders,
            offsetColumn = offset
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

        val studentsSum = mutableMapOf<Int, StudentAttendance>()

        // Lessons with attendance
        for ((lessonIndex, listPair) in lessonListWithAttendance.withIndex()) {
            val (lesson, students) = listPair
            resultCells.add(
                CellData(
                    lessonIndex,
                    0,
                    context.getString(
                        lesson.type.toEdit().shortNameRes
                    )
                )
            )
            resultCells.add(
                CellData(
                    lessonIndex,
                    1,
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
                        studentIndex + 2,
                        skipped
                    )
                )
            }
        }

        return Triple(
            lessonListWithAttendance.size,
            studentsSum,
            RenderData(
                resultCells,
                resultBorders,
                offsetColumn = offset
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

        return RenderData(
            resultCells,
            resultBorders
        ) to studentEntityList
    }
}