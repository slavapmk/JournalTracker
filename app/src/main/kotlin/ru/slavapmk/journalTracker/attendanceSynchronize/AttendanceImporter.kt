package ru.slavapmk.journalTracker.attendanceSynchronize

import android.content.Context
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import ru.slavapmk.journalTracker.R
import ru.slavapmk.journalTracker.dataModels.StudentAttendanceLesson
import ru.slavapmk.journalTracker.dataModels.toEntity
import ru.slavapmk.journalTracker.storageModels.StorageDependencies
import ru.slavapmk.journalTracker.utils.LocaleHelper
import java.io.File
import java.io.FileInputStream

class AttendanceImporter(
    private val file: File,
    private val context: Context,
    private val callback: MutableLiveData<String>? = null
) {
    private val attendanceLessonLookup by lazy {
        val result = mutableMapOf<String, StudentAttendanceLesson>()
        for (entry in StudentAttendanceLesson.entries) {
            for (allVariation in LocaleHelper.getAllVariations(context, entry.displayNameRes)) {
                result[allVariation] = entry
            }
            for (additionalVariant in entry.additionalVariants) {
                result[additionalVariant] = entry
            }
        }
        result
    }

    suspend fun import() {
        val workbook: Workbook = withContext(Dispatchers.IO) {
            FileInputStream(file).use {
                WorkbookFactory.create(file)
            }
        }

        for ((i, sheet) in workbook.sheetIterator().withIndex()) {
            callback?.postValue(
                context.getString(
                    R.string.importing_sheet, i + 1, workbook.numberOfSheets
                )
            )
            for (readAttendance in processSheet(sheet)) {
//                println(readAttendance)
                StorageDependencies.studentsAttendanceRepository.insertOrUpdate(
                    readAttendance.user,
                    readAttendance.lesson,
                    readAttendance.type.toEntity()
                )
            }
        }
    }

    private fun processSheet(sheet: Sheet): Sequence<ReadAttendance> {
        val maxColumns = sheet.getMaxColumns()

        // Index -> Student ID
        val studentsIds = sequence {
            for (i in 0 until sheet.physicalNumberOfRows) {
                val row = sheet.getRow(i)
                yield(
                    i to row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                )
            }
        }.mapNotNull { (row, cell) ->
            try {
                val stringCellValue = cell.stringCellValue
                if (stringCellValue.startsWith("#")) {
                    try {
                        row to stringCellValue.removePrefix("#").toInt()
                    } catch (nfe: NumberFormatException) {
                        null
                    }
                } else {
                    null
                }
            } catch (e: RuntimeException) {
                null
            }
        }.associate { it }

        return sequence {
            for (col in 1..maxColumns) {
                val attendances = sequence {
                    for (i in 0 until sheet.physicalNumberOfRows) {
                        val row = sheet.getRow(i)
                        yield(
                            i to row.getCell(col, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
                        )
                    }
                }
                yieldAll(
                    extractAttendance(
                        studentsIds, attendances
                    )
                )
            }
        }
    }

    private fun extractAttendance(
        // Row -> Student ID
        studentIndexes: Map<Int, Int>,
        // [Row -> Cell]
        row: Sequence<Pair<Int, Cell>>
    ) = sequence {
        val (attendances, another) = row.mapNotNull { (row, cell) ->
            try {
                row to cell.stringCellValue
            } catch (e: RuntimeException) {
                null
            }
        }.partition { (row, _) ->
            studentIndexes.contains(row)
        }
        val lessonIdCellPair = another.find { (_, cell) ->
            cell.startsWith("#")
        }
        if (lessonIdCellPair == null) {
            return@sequence
        }
        val (_, lessonIdCell) = lessonIdCellPair
        for ((attendanceRow, attendanceCell) in attendances) {
            try {
                val studentAttendanceLesson = attendanceLessonLookup[
                    attendanceCell.removePrefix("'")
                ]
                studentAttendanceLesson?.let { attendance ->
                    studentIndexes[attendanceRow]?.let { studentId ->
                        yield(
                            ReadAttendance(
                                studentId,
                                lessonIdCell.removePrefix("#").toInt(),
                                attendance
                            )
                        )
                    }
                }
            } catch (e: RuntimeException) {
                continue
            }
        }
    }
}

fun Sheet.getMaxColumns(): Int {
    var max = 0
    for (row in this) {
        if (row.lastCellNum > max) {
            max = row.lastCellNum.toInt()
        }
    }
    return max
}

data class ReadAttendance(
    val user: Int,
    val lesson: Int,
    val type: StudentAttendanceLesson
)