package ru.slavapmk.journalTracker.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.HorizontalAlignment
import ru.slavapmk.journalTracker.R
import ru.slavapmk.journalTracker.excelExporter.BorderData
import ru.slavapmk.journalTracker.excelExporter.CellData
import ru.slavapmk.journalTracker.excelExporter.RenderData
import ru.slavapmk.journalTracker.storageModels.StorageDependencies
import ru.slavapmk.journalTracker.storageModels.entities.StudentEntity

data class LessonAttendance(
    val respectful: Int = 0,
    val disrespectful: Int = 0
) {
    operator fun plus(other: LessonAttendance): LessonAttendance {
        return LessonAttendance(
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

        val attendances: MutableList<Map<Int, LessonAttendance>> = mutableListOf()

        var offset: Int = 2
        for (date in dates) {
            val (count, sumAttendances, renderLessons) = renderLessons(
                context, date, students, offset
            )
            offset += count
            attendances.add(sumAttendances)
            result.add(renderLessons)
        }

        val summedAttendance: Map<Int, LessonAttendance> = attendances
            .fold(mutableMapOf()) { acc, map ->
                map.forEach { (key, value) ->
                    acc[key] = acc.getOrDefault(key, LessonAttendance()) + value
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
        summedAttendance: Map<Int, LessonAttendance>,
        offset: Int
    ): RenderData {
        TODO("Not yet implemented")
    }

    private fun renderLessons(
        context: Context, date: SimpleDate, students: List<StudentEntity>,
        offset: Int
    ): Triple<Int, Map<Int, LessonAttendance>, RenderData> {
        TODO("Not yet implemented")
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