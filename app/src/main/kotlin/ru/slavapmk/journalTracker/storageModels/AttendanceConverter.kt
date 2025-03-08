package ru.slavapmk.journalTracker.storageModels

import androidx.room.TypeConverter

class AttendanceConverter {
    @TypeConverter
    fun fromAttendance(value: StudentEntityAttendance?): String? {
        return value?.let {
            when (it) {
                StudentEntityAttendance.VISIT -> VISIT
                StudentEntityAttendance.NOT_VISIT -> NOT_VISIT
                StudentEntityAttendance.SICK -> SICK
                StudentEntityAttendance.SICK_WITH_CERTIFICATE -> SICK_WITH_CERTIFICATE
                StudentEntityAttendance.RESPECTFUL_PASS -> RESPECTFUL_PASS
            }
        }
    }

    @TypeConverter
    fun toAttendance(raw: String?): StudentEntityAttendance? {
        return raw?.let {
            when (it) {
                VISIT -> StudentEntityAttendance.VISIT
                NOT_VISIT -> StudentEntityAttendance.NOT_VISIT
                SICK -> StudentEntityAttendance.SICK
                SICK_WITH_CERTIFICATE -> StudentEntityAttendance.SICK_WITH_CERTIFICATE
                RESPECTFUL_PASS -> StudentEntityAttendance.RESPECTFUL_PASS
                else -> throw IllegalStateException()
            }
        }
    }

    companion object {
        private const val VISIT = "VISIT"
        private const val NOT_VISIT = "NOT_VISIT"
        private const val SICK = "SICK"
        private const val SICK_WITH_CERTIFICATE = "SICK_WITH_CERTIFICATE"
        private const val RESPECTFUL_PASS = "RESPECTFUL_PASS"
    }
}