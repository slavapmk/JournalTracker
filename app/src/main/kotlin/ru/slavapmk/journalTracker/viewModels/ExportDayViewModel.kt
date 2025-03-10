package ru.slavapmk.journalTracker.viewModels

import android.content.Context
import android.os.Environment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.VerticalAlignment
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import ru.slavapmk.journalTracker.R
import ru.slavapmk.journalTracker.storageModels.StorageDependencies
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar


class ExportDayViewModel : ViewModel() {
    val savedLiveStatus by lazy {
        MutableLiveData<Unit>()
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
                            calendar[Calendar.MINUTE]
                        )
                    )
                    val outputStream = FileOutputStream(file)
                    workbook.write(outputStream)
                    workbook.close()
                    outputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            savedLiveStatus.postValue(Unit)
        }
    }

    private suspend fun parse(context: Context) = withContext(Dispatchers.IO) {
        val workbook = XSSFWorkbook()
        val sheet: Sheet = workbook.createSheet("Лист1")
        workbook.properties.coreProperties.creator = "Journal Exporter"
        workbook.properties.coreProperties.title = "Attendance Journal"

        val cellDataList = fillDay(context)
        parseBook(
            workbook,
            sheet,
            cellDataList
        )

        resizeColumn(
            sheet
        )

        return@withContext workbook
    }

    private suspend fun fillDay(context: Context): RenderData {
        val resultCells = mutableListOf<CellData>()
        val resultBorders = mutableListOf<BorderData>()

        resultCells.add(
            CellData(
                0,
                1,
                context.getString(R.string.exporter_num),
                endRow = 3
            )
        )
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
        resultBorders.add(
            BorderData(
                0, 1,
                1, 4 + studentEntityList.size - 1,
                BorderStyle.THICK
            )
        )

        return RenderData(resultCells, resultBorders)
    }

    private fun parseBook(
        workbook: Workbook,
        sheet: Sheet,
        renderData: RenderData
    ) {
        for (data in renderData.cells) {
            val row = sheet.getRow(data.row) ?: sheet.createRow(data.row)
            val cell = row.createCell(data.column)

            when (data.value) {
                is String -> cell.setCellValue(data.value)
                is Int -> cell.setCellValue(data.value.toDouble())
            }

            if (
                (maxOf(data.row, data.endRow) - minOf(data.row, data.endRow) + 1) *
                (maxOf(data.column, data.endColumn) - minOf(data.column, data.endColumn) + 1)
                > 1
            ) {
                sheet.addMergedRegion(
                    CellRangeAddress(
                        minOf(data.row, data.endRow),
                        maxOf(data.row, data.endRow),
                        minOf(data.column, data.endColumn),
                        maxOf(data.column, data.endColumn),
                    )
                )
            }

            val style = workbook.createCellStyle()
            style.alignment = data.alignment
            style.rotation = data.rotation
            style.verticalAlignment = data.verticalAlignment
            cell.cellStyle = style
        }

        for (border in renderData.borders) {
            for (colIndex in border.startColumn..border.endColumn) {
                for (rowIndex in border.startRow..border.endRow) {
                    val row = sheet.getRow(rowIndex) ?: sheet.createRow(rowIndex)
                    val cell = row.getCell(colIndex) ?: row.createCell(colIndex)
                    val style = if (cell.cellStyle.index == 0.toShort()) {
                        workbook.createCellStyle()
                    } else {
                        cell.cellStyle
                    }.apply {
                        val isTop = rowIndex == border.startRow
                        val isBottom = rowIndex == border.endRow
                        val isLeft = colIndex == border.startColumn
                        val isRight = colIndex == border.endColumn

                        borderTop = if (isTop) border.outer else border.inner
                        borderBottom = if (isBottom) border.outer else border.inner
                        borderLeft = if (isLeft) border.outer else border.inner
                        borderRight = if (isRight) border.outer else border.inner
                    }
                    cell.cellStyle = style
                }
            }
        }
    }


    private fun resizeColumn(sheet: Sheet) {
        val columnsToResize = mutableMapOf<Int, Int>()

        for (rowIndex in 0..sheet.lastRowNum) {
            val row = sheet.getRow(rowIndex) ?: continue
            for (cell in row) {
                val cellValue = cell.toString()
                val columnIndex = cell.columnIndex
                columnsToResize[columnIndex] = maxOf(
                    columnsToResize.getOrDefault(columnIndex, 0),
                    if (cell.cellStyle.rotation == 90.toShort()) {
                        1
                    } else {
                        cellValue.length
                    }
                )
            }
        }

        for ((column, maxLength) in columnsToResize) {
            sheet.setColumnWidth(column, (maxLength + 2) * 256) // +2 для небольшого запаса
        }
    }
}

data class RenderData(
    val cells: List<CellData>,
    val borders: List<BorderData>
)

data class CellData(
    val column: Int,
    val row: Int,
    val value: Any,
    val endColumn: Int = column,
    val endRow: Int = row,
    val alignment: HorizontalAlignment = HorizontalAlignment.CENTER,
    val verticalAlignment: VerticalAlignment = VerticalAlignment.CENTER,
    val rotation: Short = 0
)

data class BorderData(
    val startColumn: Int,
    val startRow: Int,
    val endColumn: Int,
    val endRow: Int,
    val outer: BorderStyle,
    val inner: BorderStyle = BorderStyle.NONE
)