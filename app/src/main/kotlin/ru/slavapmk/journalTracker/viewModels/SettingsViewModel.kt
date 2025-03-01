package ru.slavapmk.journalTracker.viewModels

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import ru.slavapmk.journalTracker.dataModels.settings.AttendanceFormats
import ru.slavapmk.journalTracker.dataModels.settings.WeeksFormats

class SettingsViewModel : ViewModel() {
    lateinit var sharedPreferences: SharedPreferences

    var weekFormat: WeeksFormats?
        get() = when (sharedPreferences.getString(WEEK_FORMAT_KEY, EVEN_UNEVEN_VALUE_KEY)) {
            EVEN_UNEVEN_VALUE_KEY -> WeeksFormats.EVEN_UNEVEN
            UP_DOWN_VALUE_KEY -> WeeksFormats.UP_DOWN
            else -> null
        }
        set(value) {
            sharedPreferences.edit {
                putString(
                    WEEK_FORMAT_KEY,
                    when (value) {
                        WeeksFormats.EVEN_UNEVEN -> EVEN_UNEVEN_VALUE_KEY
                        WeeksFormats.UP_DOWN -> UP_DOWN_VALUE_KEY
                        null -> {
                            remove(WEEK_FORMAT_KEY)
                            return
                        }
                    }
                )
            }
        }

    var attendanceFormat: AttendanceFormats?
        get() = when (
            sharedPreferences.getString(ATTENDANCE_FORMAT_KEY, PLUS_MINUS_VALUE_KEY)
        ) {
            PLUS_MINUS_VALUE_KEY -> AttendanceFormats.PLUS_MINUS
            SKIP_HOURS_VALUE_KEY -> AttendanceFormats.SKIP_HOURS
            else -> null
        }
        set(value) {
            sharedPreferences.edit {
                putString(
                    ATTENDANCE_FORMAT_KEY, when (value) {
                        AttendanceFormats.PLUS_MINUS -> PLUS_MINUS_VALUE_KEY
                        AttendanceFormats.SKIP_HOURS -> SKIP_HOURS_VALUE_KEY
                        null -> {
                            remove(ATTENDANCE_FORMAT_KEY)
                            return
                        }
                    }
                )
            }
        }

    var weekTypes: Int
        get() = sharedPreferences.getInt(WEEK_TYPES_KEY, 1)
        set(value) {
            sharedPreferences.edit {
                putInt(WEEK_TYPES_KEY, value)
            }
        }

    var groupName: String
        get() = sharedPreferences.getString(GROUP_NAME_KEY, "").toString()
        set(value) {
            sharedPreferences.edit {
                putString(GROUP_NAME_KEY, value)
            }
        }

    companion object {
        private const val WEEK_TYPES_KEY = "WEEK_TYPES_KEY"
        private const val WEEK_FORMAT_KEY = "WEEK_FORMAT_KEY"
        private const val GROUP_NAME_KEY = "GROUP_NAME_KEY"
        private const val ATTENDANCE_FORMAT_KEY = "ATTENDANCE_FORMAT_KEY"
        private const val PLUS_MINUS_VALUE_KEY = "PLUS_MINUS"
        private const val SKIP_HOURS_VALUE_KEY = "SKIP_HOURS"
        private const val EVEN_UNEVEN_VALUE_KEY = "EVEN_UNEVEN"
        private const val UP_DOWN_VALUE_KEY = "UP_DOWN"
    }
}