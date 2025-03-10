package ru.slavapmk.journalTracker.viewModels

import android.os.Environment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.VerticalAlignment
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class ExportDayViewModel : ViewModel() {
    val savedLiveStatus by lazy {
        MutableLiveData<Unit>()
    }

    fun saveExcel() {
        viewModelScope.launch {
            val workbook = parse()
            withContext(Dispatchers.IO) {
                try {
                    val file = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        "Sample2.xlsx"
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

    private suspend fun parse() = withContext(Dispatchers.IO) {
        val workbook = XSSFWorkbook()
        val sheet: Sheet = workbook.createSheet("Лист1")

        parseBook(
            workbook,
            sheet,
            listOf(
                CellData(0, 0, "Hi"),
                CellData(1, 1, "Bye"),
                CellData(2, 0, 99, endColumn = 10)
            )
        )

        resizeColumn(
            workbook,
            sheet
        )

        return@withContext workbook
    }

    private fun parseBook(workbook: Workbook, sheet: Sheet, cellDataList: List<CellData>) {
        for (data in cellDataList) {
            val row =
                if (sheet.getRow(data.row) == null) sheet.createRow(data.row) else sheet.getRow(data.row)
            val cell = row.createCell(data.column)

            if (data.value is String) {
                cell.setCellValue(data.value)
            } else if (data.value is Int) {
                cell.setCellValue(data.value.toDouble())
            }

            if (data.endColumn > data.column) {
                sheet.addMergedRegion(
                    CellRangeAddress(
                        data.row,
                        data.endRow,
                        data.column,
                        data.endColumn
                    )
                )
            }

            val style = workbook.createCellStyle()
            style.alignment = data.alignment
            style.verticalAlignment = data.verticalAlignment
            cell.cellStyle = style
        }
    }

    private fun resizeColumn(workbook: Workbook, sheet: Sheet) {
        val columnsToResize = mutableMapOf<Int, Int>()

        for (rowIndex in 0..sheet.lastRowNum) {
            val row = sheet.getRow(rowIndex) ?: continue
            for (cell in row) {
                val cellValue = cell.toString()
                val columnIndex = cell.columnIndex
                columnsToResize[columnIndex] =
                    maxOf(columnsToResize.getOrDefault(columnIndex, 0), cellValue.length)
            }
        }

        for ((column, maxLength) in columnsToResize) {
            sheet.setColumnWidth(column, (maxLength + 2) * 256) // +2 для небольшого запаса
        }
    }
}

data class CellData(
    val column: Int,
    val row: Int,
    val value: Any,
    val endColumn: Int = column,
    val endRow: Int = row,
    val alignment: HorizontalAlignment = HorizontalAlignment.CENTER,
    val verticalAlignment: VerticalAlignment = VerticalAlignment.CENTER,
)