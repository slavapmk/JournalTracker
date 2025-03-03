package ru.slavapmk.journalTracker.viewModels

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import ru.slavapmk.journalTracker.dataModels.settings.AttendanceFormats
import ru.slavapmk.journalTracker.dataModels.settings.WeeksFormats
import ru.slavapmk.journalTracker.ui.SharedKeys

class SettingsViewModel : ViewModel() {
    lateinit var sharedPreferences: SharedPreferences

    var weekFormat: WeeksFormats?
        get() = when (sharedPreferences.getString(SharedKeys.WEEK_FORMAT_KEY, SharedKeys.EVEN_UNEVEN_VALUE_KEY)) {
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
            sharedPreferences.getString(SharedKeys.ATTENDANCE_FORMAT_KEY, SharedKeys.PLUS_MINUS_VALUE_KEY)
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

    companion object {
    }
}