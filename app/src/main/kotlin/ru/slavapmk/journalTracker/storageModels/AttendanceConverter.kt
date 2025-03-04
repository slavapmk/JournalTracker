package ru.slavapmk.journalTracker.storageModels

import androidx.room.TypeConverter

class AttendanceConverter {
    @TypeConverter
    fun fromAttendance(value: StudentAttendance?): String? {
        return value?.let {
            when (it) {
                StudentAttendance.VISIT -> VISIT
                StudentAttendance.NOT_VISIT -> NOT_VISIT
                StudentAttendance.SICK -> SICK
                StudentAttendance.SICK_WITH_CERTIFICATE -> SICK_WITH_CERTIFICATE
                StudentAttendance.RESPECTFUL_PASS -> RESPECTFUL_PASS
            }
        }
    }

    @TypeConverter
    fun toAttendance(raw: String?): StudentAttendance? {
        return raw?.let {
            when (it) {
                VISIT -> StudentAttendance.VISIT
                NOT_VISIT -> StudentAttendance.NOT_VISIT
                SICK -> StudentAttendance.SICK
                SICK_WITH_CERTIFICATE -> StudentAttendance.SICK_WITH_CERTIFICATE
                RESPECTFUL_PASS -> StudentAttendance.RESPECTFUL_PASS
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