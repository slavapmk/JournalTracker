package ru.slavapmk.journalTracker.excelExporter

import android.os.Environment
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ExcelWriter {
    private val workbook: Workbook by lazy { XSSFWorkbook() }

    fun writeExcelFile(): Workbook {
        // Создаем новую книгу Excel
        val sheet: Sheet = workbook.createSheet("Лист1")

        // Добавляем заголовки
        val headerRow: Row = sheet.createRow(0)
        headerRow.createCell(0).setCellValue("Имя")
        headerRow.createCell(1).setCellValue("Возраст")
        headerRow.createCell(2).setCellValue("Город")

        // Добавляем данные
        val data = arrayOf(
            arrayOf<Any>("Алексей", 25, "Москва"),
            arrayOf<Any>("Ольга", 30, "Санкт-Петербург"),
            arrayOf<Any>("Иван", 22, "Екатеринбург")
        )

        var rowNum = 1
        for (rowData in data) {
            val row: Row = sheet.createRow(rowNum++)
            for (i in rowData.indices) {
                val cell: Cell = row.createCell(i)
                if (rowData[i] is String) {
                    cell.setCellValue(rowData[i] as String)
                } else if (rowData[i] is Int) {
                    cell.setCellValue((rowData[i] as Int).toDouble())
                }
            }
        }

        return workbook
    }

    fun saveBook() {
        try {
            val file = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "Sample.xlsx"
            )
            val outputStream = FileOutputStream(file)
            workbook.write(outputStream)
            workbook.close()
            outputStream.close()
            println("Файл успешно создан: " + file.absolutePath)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}