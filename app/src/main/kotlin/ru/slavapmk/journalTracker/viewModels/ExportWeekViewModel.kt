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
                students,
                summedAttendance,
                offset
            )
        )

        return result
    }

    private suspend fun renderSummary(
        context: Context,
        students: List<StudentEntity>,
        summedAttendance: Map<Int, StudentAttendance>,
        offset: Int
    ): RenderData {
        TODO("Not yet implemented")
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