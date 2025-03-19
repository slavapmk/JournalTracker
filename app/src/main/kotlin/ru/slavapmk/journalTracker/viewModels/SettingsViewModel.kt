package ru.slavapmk.journalTracker.viewModels

import android.content.Context
import android.content.SharedPreferences
import android.os.Environment
import androidx.core.content.edit
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import ru.slavapmk.journalTracker.attendanceSynchronize.AttendanceImporter
import ru.slavapmk.journalTracker.dataModels.settings.AttendanceFormats
import ru.slavapmk.journalTracker.dataModels.settings.WeeksFormats
import ru.slavapmk.journalTracker.ui.SharedKeys
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class SettingsViewModel : ViewModel() {
    lateinit var sharedPreferences: SharedPreferences

    var weekFormat: WeeksFormats?
        get() = when (sharedPreferences.getString(
            SharedKeys.WEEK_FORMAT_KEY,
            SharedKeys.EVEN_UNEVEN_VALUE_KEY
        )) {
            SharedKeys.EVEN_UNEVEN_VALUE_KEY -> WeeksFormats.EVEN_UNEVEN
            SharedKeys.UP_DOWN_VALUE_KEY -> WeeksFormats.UP_DOWN
            SharedKeys.DOWN_UP_VALUE_KEY -> WeeksFormats.DOWN_UP
            else -> null
        }
        set(value) {
            sharedPreferences.edit {
                putString(
                    SharedKeys.WEEK_FORMAT_KEY,
                    when (value) {
                        WeeksFormats.EVEN_UNEVEN -> SharedKeys.EVEN_UNEVEN_VALUE_KEY
                        WeeksFormats.UP_DOWN -> SharedKeys.UP_DOWN_VALUE_KEY
                        WeeksFormats.DOWN_UP -> SharedKeys.DOWN_UP_VALUE_KEY
                        null -> {
                            remove(SharedKeys.WEEK_FORMAT_KEY)
                            return
                        }
                    }
                )
            }
        }

    var attendanceFormat: AttendanceFormats?
        get() = when (
            sharedPreferences.getString(
                SharedKeys.ATTENDANCE_FORMAT_KEY,
                SharedKeys.PLUS_MINUS_VALUE_KEY
            )
        ) {
            SharedKeys.PLUS_MINUS_VALUE_KEY -> AttendanceFormats.PLUS_MINUS
            SharedKeys.SKIP_HOURS_VALUE_KEY -> AttendanceFormats.SKIP_HOURS
            else -> null
        }
        set(value) {
            sharedPreferences.edit {
                putString(
                    SharedKeys.ATTENDANCE_FORMAT_KEY, when (value) {
                        AttendanceFormats.PLUS_MINUS -> SharedKeys.PLUS_MINUS_VALUE_KEY
                        AttendanceFormats.SKIP_HOURS -> SharedKeys.SKIP_HOURS_VALUE_KEY
                        null -> {
                            remove(SharedKeys.ATTENDANCE_FORMAT_KEY)
                            return
                        }
                    }
                )
            }
        }

    var weekTypes: Int
        get() = sharedPreferences.getInt(SharedKeys.WEEK_TYPES_KEY, 1)
        set(value) {
            sharedPreferences.edit {
                putInt(SharedKeys.WEEK_TYPES_KEY, value)
            }
        }

    var groupName: String
        get() = sharedPreferences.getString(SharedKeys.GROUP_NAME_KEY, "").toString()
        set(value) {
            sharedPreferences.edit {
                putString(SharedKeys.GROUP_NAME_KEY, value)
            }
        }

    val importStatusCallback by lazy { MutableLiveData<String>() }
    val importDone by lazy { MutableLiveData<Unit>() }

    fun importExcel(file: File, context: Context) {
        viewModelScope.launch {
            AttendanceImporter(
                file, context, importStatusCallback
            ).import()
            importDone.postValue(Unit)
        }
    }

    val exportDone by lazy { MutableLiveData<ExportResult>() }

    fun saveBackup(
        cacheDir: File, dbFile: File, outputFileName: String, sharedPreferences: SharedPreferences
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val backupPath = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    outputFileName
                )

                val sharedPrefsFile = File(cacheDir, "shared.json")
                saveSharedPreferencesToJson(sharedPreferences, sharedPrefsFile)

                try {
                    ZipOutputStream(FileOutputStream(backupPath)).use { zipOut ->
                        addFileToZip(dbFile, zipOut, "database.db")
                        addFileToZip(sharedPrefsFile, zipOut, "shared.json")
                    }
                    exportDone.postValue(
                        ExportResult.SuccessResult(backupPath.absolutePath)
                    )
                } catch (e: IOException) {
                    exportDone.postValue(
                        ExportResult.ErrorResult(e)
                    )
                }
            }
        }
    }

    private fun saveSharedPreferencesToJson(
        sharedPreferences: SharedPreferences,
        outputFile: File
    ) {
        val json = JSONObject()

        for (entry in sharedPreferences.all.entries) {
            when (val value = entry.value) {
                is String -> json.put(entry.key, value)
                is Int -> json.put(entry.key, value)
                is Boolean -> json.put(entry.key, value)
                is Float -> json.put(entry.key, value)
                is Long -> json.put(entry.key, value)
                else -> {}
            }
        }

        outputFile.writer().use { it.write(json.toString(4)) }
    }

    private fun addFileToZip(file: File, zipOut: ZipOutputStream, zipEntryName: String) {
        FileInputStream(file).use { fis ->
            zipOut.putNextEntry(ZipEntry(zipEntryName))
            fis.copyTo(zipOut)
            zipOut.closeEntry()
        }
    }
}

sealed interface ExportResult {
    data class SuccessResult(val path: String) : ExportResult
    data class ErrorResult(val error: IOException) : ExportResult
}