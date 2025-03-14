package ru.slavapmk.journalTracker.viewModels

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.HorizontalAlignment
import ru.slavapmk.journalTracker.R
import ru.slavapmk.journalTracker.attendanceSynchronize.AttendanceExporter
import ru.slavapmk.journalTracker.dataModels.StudentAttendanceLesson
import ru.slavapmk.journalTracker.dataModels.selectWeek.Semester
import ru.slavapmk.journalTracker.dataModels.toEdit
import ru.slavapmk.journalTracker.attendanceSynchronize.BorderData
import ru.slavapmk.journalTracker.attendanceSynchronize.CellData
import ru.slavapmk.journalTracker.attendanceSynchronize.ExcelExporter
import ru.slavapmk.journalTracker.attendanceSynchronize.RenderData
import ru.slavapmk.journalTracker.storageModels.StorageDependencies
import ru.slavapmk.journalTracker.storageModels.entities.SemesterEntity
import ru.slavapmk.journalTracker.storageModels.entities.StudentEntity
import ru.slavapmk.journalTracker.ui.SharedKeys
import ru.slavapmk.journalTracker.utils.generateWeeks
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.LocalDate
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar

class ExportWeekViewModel : ViewModel() {
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

    val savedLiveStatus by lazy {
        MutableLiveData<Unit>()
    }
    val sharedLiveStatus by lazy {
        MutableLiveData<Intent?>()
    }
    val openLiveStatus by lazy {
        MutableLiveData<Intent?>()
    }
    val statusCallback by lazy {
        MutableLiveData<String?>()
    }

    lateinit var shared: SharedPreferences

    fun saveExcel(context: Context) {
        viewModelScope.launch {
            val file = GregorianCalendar.getInstance().apply { time = Date() }.let { calendar ->
                File(
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
            }
            renderToFile(context, file)
            savedLiveStatus.postValue(Unit)
        }
    }

    fun shareExcel(context: Context) {
        viewModelScope.launch {
            val file = GregorianCalendar.getInstance().apply { time = Date() }.let { calendar ->
                File(
                    context.cacheDir,
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
            }
            if (
                renderToFile(context, file)
            ) {
                val uri = FileProvider.getUriForFile(
                    context, "${context.packageName}.provider", file
                )
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/vnd.ms-excel"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                sharedLiveStatus.postValue(
                    Intent.createChooser(
                        intent, context.getString(R.string.share_via)
                    )
                )
            } else {
                sharedLiveStatus.postValue(null)
            }
        }
    }

    fun openExcel(context: Context) {
        viewModelScope.launch {
            val file = GregorianCalendar.getInstance().apply { time = Date() }.let { calendar ->
                File(
                    context.cacheDir,
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
            }
            if (
                renderToFile(context, file)
            ) {
                val uri = FileProvider.getUriForFile(
                    context, "${context.packageName}.provider", file
                )
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "application/vnd.ms-excel")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                openLiveStatus.postValue(
                    Intent.createChooser(
                        intent, context.getString(R.string.share_via)
                    )
                )
            } else {
                openLiveStatus.postValue(null)
            }
        }
    }

    private suspend fun renderToFile(context: Context, file: File): Boolean {
        val attendanceExporter = AttendanceExporter(statusCallback)
        val workbook = attendanceExporter.parseWeek(
            context,
            shared.getInt(SharedKeys.SEMESTER_ID, -1),
            date,
            shared.getString(SharedKeys.GROUP_NAME_KEY, "") ?: ""
        )
        return withContext(Dispatchers.IO) {
            try {
                val outputStream = FileOutputStream(file)
                workbook.export(outputStream)
                outputStream.close()
                true
            } catch (e: IOException) {
                e.printStackTrace()
                false
            }
        }
    }
}