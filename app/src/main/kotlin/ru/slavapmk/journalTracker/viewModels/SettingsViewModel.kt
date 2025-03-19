package ru.slavapmk.journalTracker.viewModels

import android.content.Context
import android.content.SharedPreferences
import android.os.Environment
import android.util.Log
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
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
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
    val importExcelDone by lazy { MutableLiveData<Unit>() }

    fun importExcel(file: File, context: Context) {
        viewModelScope.launch {
            AttendanceImporter(
                file, context, importStatusCallback
            ).import()
            importExcelDone.postValue(Unit)
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
                Log.d(
                    "Backup",
                    "Creating SharedPreferences JSON dump at ${sharedPrefsFile.absolutePath}"
                )

                saveSharedPreferencesToJson(sharedPreferences, sharedPrefsFile)

                try {
                    ZipOutputStream(FileOutputStream(backupPath)).use { zipOut ->
                        Log.d("Backup", "Adding database file to archive: ${dbFile.absolutePath}")
                        addFileToZip(dbFile, zipOut, "database.db")

                        Log.d(
                            "Backup",
                            "Adding SharedPreferences JSON dump to archive: ${sharedPrefsFile.absolutePath}"
                        )
                        addFileToZip(sharedPrefsFile, zipOut, "shared.json")
                    }

                    Log.d("Backup", "Backup successfully created: ${backupPath.absolutePath}")
                    exportDone.postValue(ExportResult.SuccessResult(backupPath.absolutePath))
                } catch (e: IOException) {
                    Log.e("Backup", "Error while creating backup", e)
                    exportDone.postValue(ExportResult.ErrorResult(e))
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

    val restoreDone by lazy { MutableLiveData<ImportResult>() }

    fun restoreDbBackup(dbFile: File, zipInputStream: InputStream?) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    dbFile.outputStream().use { os ->
                        zipInputStream?.copyTo(os)
                    }
                }
                restoreDone.postValue(ImportResult.SuccessResult)
            } catch (e: IOException) {
                Log.e("Restore", "Error during restore only DB", e)
                restoreDone.postValue(ImportResult.ErrorResult(e))
            }
        }
    }

    fun restoreBackup(
        dbFile: File, zipInputStream: InputStream?, sharedPreferences: SharedPreferences,
        tempDir: File
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
//                    val tempDir = File(context.cacheDir, "backup_restore").apply { mkdirs() }
                    tempDir.mkdirs()

                    // Распаковываем ZIP
                    unzipFile(zipInputStream, tempDir)

                    // Восстанавливаем базу данных
                    val tempDbFile = File(tempDir, "database.db")

                    if (dbFile.exists()) {
                        tempDbFile.copyTo(dbFile, overwrite = true)
                        Log.d("Restore", "Database restored to ${dbFile.absolutePath}")
                    } else {
                        Log.e("Restore", "Database file not found in backup!")
                    }

                    // Восстанавливаем SharedPreferences
                    val sharedPrefsFile = File(tempDir, "shared.json")
                    if (sharedPrefsFile.exists()) {
                        restoreSharedPreferences(sharedPreferences, sharedPrefsFile)
                        Log.d("Restore", "SharedPreferences restored")
                    } else {
                        Log.e("Restore", "SharedPreferences file not found in backup!")
                    }

                    restoreDone.postValue(ImportResult.SuccessResult)
                } catch (e: IOException) {
                    Log.e("Restore", "Error during restore", e)
                    restoreDone.postValue(ImportResult.ErrorResult(e))
                }
            }
        }
    }

    private fun unzipFile(zipInputStream: InputStream?, outputDir: File) {
        if (zipInputStream == null) {
            Log.e("Unzip", "InputStream is null, cannot proceed with extraction")
            return
        }

        Log.d("Unzip", "Starting extraction to ${outputDir.absolutePath}")

        try {
            val bufferedStream = BufferedInputStream(zipInputStream)
            Log.d("Unzip", "Available bytes in stream: ${bufferedStream.available()}")

            ZipInputStream(bufferedStream).use { zipIn ->
                var entry: ZipEntry?

                while (zipIn.nextEntry.also { entry = it } != null) {
                    entry?.let {
                        val outFile = File(outputDir, it.name)

                        if (it.isDirectory) {
                            Log.d("Unzip", "Creating directory: ${outFile.absolutePath}")
                            outFile.mkdirs()
                        } else {
                            // Ensure parent directory exists
                            outFile.parentFile?.mkdirs()

                            Log.d(
                                "Unzip",
                                "Extracting file: ${outFile.absolutePath} (Size: ${it.size} bytes)"
                            )
                            try {
                                FileOutputStream(outFile).use { fos ->
                                    zipIn.copyTo(fos)
                                }
                                Log.d("Unzip", "Successfully extracted: ${outFile.name}")
                            } catch (e: IOException) {
                                Log.e("Unzip", "Error extracting file: ${outFile.name}", e)
                            }
                        }

                        zipIn.closeEntry()
                    }
                }
            }

            Log.d("Unzip", "Extraction completed successfully")
        } catch (e: IOException) {
            Log.e("Unzip", "Error during unzip operation", e)
        }
    }

    private fun restoreSharedPreferences(sharedPreferences: SharedPreferences, jsonFile: File) {
        val jsonString = jsonFile.readText()
        val jsonObject = JSONObject(jsonString)
        val editor = sharedPreferences.edit()

        jsonObject.keys().forEach { key ->
            when (val value = jsonObject.get(key)) {
                is String -> editor.putString(key, value)
                is Int -> editor.putInt(key, value)
                is Boolean -> editor.putBoolean(key, value)
                is Float -> editor.putFloat(key, value)
                is Long -> editor.putLong(key, value)
            }
        }
        editor.apply()
    }
}

sealed interface ExportResult {
    data class SuccessResult(val path: String) : ExportResult
    data class ErrorResult(val error: IOException) : ExportResult
}

sealed interface ImportResult {
    data object SuccessResult : ImportResult
    data class ErrorResult(val error: IOException) : ImportResult
}