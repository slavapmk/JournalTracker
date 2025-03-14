package ru.slavapmk.journalTracker.attendanceSynchronize

import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.VerticalAlignment
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.ss.util.CellRangeAddressList
import org.apache.poi.xssf.usermodel.XSSFColor
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileOutputStream
import kotlin.math.roundToInt


class ExcelExporter(
    sheetNames: List<String> = listOf("Sheet 1"),
    creator: String,
    title: String
) {
    private val workbook = XSSFWorkbook().apply {
        properties.coreProperties.creator = creator
        properties.coreProperties.title = title
    }
    private val sheets: Map<String, Sheet> = sheetNames.associateWith {
        workbook.createSheet(it)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun resizeWorkbook() {
        for (sheet in sheets.values) {
            resizeSheet(sheet)
        }
    }

    @Suppress("MemberVisibilityCanBePrivate", "Unused")
    fun resizeSheet(sheet: String) {
        sheets[sheet]?.let { resizeSheet(it) }
    }

    private fun findRegion(
        regions: List<CellRangeAddress>,
        column: Int,
        row: Int
    ): CellRangeAddress? {
        for (region in regions) {
            if (row in region.firstRow..region.lastRow && column in region.firstColumn..region.lastColumn) {
                return region
            }
        }
        return null
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun resizeSheet(sheet: Sheet) {
        val columnsToResize = mutableMapOf<Int, Double>()

        for (rowIndex in 0..sheet.lastRowNum) {
            val row = sheet.getRow(rowIndex) ?: continue
            for (columnIndex in 0..row.physicalNumberOfCells) {
                val cell: Cell? = row.getCell(columnIndex)
                val cellValue = cell?.toString() ?: ""
                val findRegion = findRegion(sheet.mergedRegions, columnIndex, rowIndex)
                val mergeCell = findRegion?.let {
                    sheet.getRow(it.firstRow).getCell(it.firstColumn)
                }
                val mergeWidthCols = findRegion?.let {
                    maxOf(it.lastColumn, it.firstColumn) - minOf(it.firstColumn, it.lastColumn) + 1
                }
                val width: Double = if (cell?.cellStyle?.rotation == 90.toShort()) {
                    5.0
                } else {
                    if (mergeWidthCols == null || mergeCell == null) {
                        cellValue.length.toDouble()
                    } else {
                        if (mergeCell.cellStyle.rotation == 90.toShort()) {
                            5.0
                        } else {
                            mergeCell.toString().length.toDouble() / mergeWidthCols
                        }
                    }
                }
                columnsToResize[columnIndex] = maxOf(
                    columnsToResize.getOrDefault(columnIndex, 5.0),
                    width
                )
            }
        }

        for ((column, maxLength) in columnsToResize) {
            sheet.setColumnWidth(
                column,
                ((maxLength/* + 2*/) * 256).roundToInt()
            ) // +2 для небольшого запаса
        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun insertData(
        sheet: String,
        renderData: RenderData
    ) {
        insertData(
            sheets[sheet] ?: return, renderData
        )
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun insertData(
        sheet: Sheet,
        renderData: RenderData
    ) {
        for (cellData in renderData.cells) {
            val cellRow = cellData.row + renderData.offsetRow
            val cellColumn = cellData.column + renderData.offsetColumn

            val row = sheet.getRow(cellRow) ?: sheet.createRow(cellRow)
            val cell = row.createCell(cellColumn)

            when (cellData.value) {
                is String -> cell.setCellValue(cellData.value)
                is Int -> cell.setCellValue(cellData.value.toDouble())
            }

            val endRow = cellData.endRow + renderData.offsetRow
            val endColumn = cellData.endColumn + renderData.offsetColumn
            if (
                (maxOf(cellRow, endRow) - minOf(cellRow, endRow) + 1) *
                (maxOf(cellColumn, endColumn) - minOf(cellColumn, endColumn) + 1)
                > 1
            ) {
                try {
                    sheet.addMergedRegion(
                        CellRangeAddress(
                            minOf(cellRow, endRow),
                            maxOf(cellRow, endRow),
                            minOf(cellColumn, endColumn),
                            maxOf(cellColumn, endColumn),
                        )
                    )
                } catch (ise: IllegalStateException) {
                    throw IllegalStateException(cellData.toString(), ise)
                }
            }

            val style = workbook.createCellStyle()
            cellData.backgroundColor?.let {
                style.setFillForegroundColor(
                    XSSFColor(it.byteArray)
                )
                style.setFillPattern(FillPatternType.SOLID_FOREGROUND)
            }
            cellData.textColor?.let {
                val createFont = workbook.createFont()
                createFont.setColor(XSSFColor(it.byteArray))
                style.setFont(createFont)
            }
            style.alignment = cellData.alignment
            style.rotation = cellData.rotation
            style.verticalAlignment = cellData.verticalAlignment
            cell.cellStyle = style
        }

        for (border in renderData.borders) {
            val startColumn = border.startColumn + renderData.offsetColumn
            val endColumn = border.endColumn + renderData.offsetColumn
            val startRow = border.startRow + renderData.offsetRow
            val endRow = border.endRow + renderData.offsetRow
            for (colIndex in startColumn..endColumn) {
                for (rowIndex in startRow..endRow) {
                    val row = sheet.getRow(rowIndex) ?: sheet.createRow(rowIndex)
                    val cell = row.getCell(colIndex) ?: row.createCell(colIndex)
                    val style = if (cell.cellStyle.index == 0.toShort()) {
                        workbook.createCellStyle()
                    } else {
                        cell.cellStyle
                    }.apply {
                        val isTop = rowIndex == startRow
                        val isBottom = rowIndex == endRow
                        val isLeft = colIndex == startColumn
                        val isRight = colIndex == endColumn

                        (if (isTop) border.outer else border.inner)?.also { borderTop = it }
                        (if (isBottom) border.outer else border.inner)?.also { borderBottom = it }
                        (if (isLeft) border.outer else border.inner)?.also { borderLeft = it }
                        (if (isRight) border.outer else border.inner)?.also { borderRight = it }
                    }
                    cell.cellStyle = style
                }
            }
        }

        sheet.createFreezePane(
            if (renderData.freezeColumn == null) {
                0
            } else {
                renderData.freezeColumn!! + renderData.offsetColumn + 1
            },
            if (renderData.freezeRow == null) {
                0
            } else {
                renderData.freezeRow!! + renderData.offsetRow + 1
            }
        )

        for (validation in renderData.validations) {
            val validationHelper = sheet.dataValidationHelper
            val constraint = validationHelper.createExplicitListConstraint(
                validation.variants.toTypedArray()
            )
            val createdValidation = validationHelper.createValidation(
                constraint,
                CellRangeAddressList(
                    renderData.offsetRow + validation.startRow,
                    renderData.offsetRow + validation.endRow,
                    renderData.offsetColumn + validation.startColumn,
                    renderData.offsetColumn + validation.endColumn
                )
            )
            createdValidation.showErrorBox = true
            createdValidation.suppressDropDownArrow = true
            createdValidation.emptyCellAllowed = true
            sheet.addValidationData(createdValidation)
        }
    }

    fun export(fileOutputStream: FileOutputStream) {
        workbook.write(fileOutputStream)
        workbook.close()
    }
}

data class SimpleColor(
    val red: Int,
    val green: Int,
    val blue: Int
) {
    val byteArray: ByteArray
        get() = byteArrayOf(
            red.toByte(), green.toByte(), blue.toByte()
        )
}

data class RenderValidation(
    val startColumn: Int,
    val startRow: Int,
    val endColumn: Int,
    val endRow: Int,
    val variants: List<String>
)

data class RenderData(
    val cells: List<CellData>,
    val borders: List<BorderData>,
    var freezeColumn: Int? = null,
    var freezeRow: Int? = null,
    var offsetColumn: Int = 0,
    var offsetRow: Int = 0,
    val validations: List<RenderValidation> = listOf()
)

data class CellData(
    val column: Int,
    val row: Int,
    val value: Any,
    val endColumn: Int = column,
    val endRow: Int = row,
    val alignment: HorizontalAlignment = HorizontalAlignment.CENTER,
    val verticalAlignment: VerticalAlignment = VerticalAlignment.CENTER,
    val rotation: Short = 0,
    val textColor: SimpleColor? = null,
    val backgroundColor: SimpleColor? = null
)

data class BorderData(
    val startColumn: Int,
    val startRow: Int,
    val endColumn: Int,
    val endRow: Int,
    val outer: BorderStyle,
    val inner: BorderStyle? = null
)