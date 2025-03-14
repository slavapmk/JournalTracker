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
import ru.slavapmk.journalTracker.R
import ru.slavapmk.journalTracker.attendanceSynchronize.AttendanceExporter
import ru.slavapmk.journalTracker.ui.SharedKeys
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar

class ExportSummaryViewModel : ViewModel() {
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
        val workbook = attendanceExporter.parseSemester(
            context,
            shared.getInt(SharedKeys.SEMESTER_ID, -1),
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