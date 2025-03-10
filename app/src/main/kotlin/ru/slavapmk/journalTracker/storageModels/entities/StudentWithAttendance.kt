package ru.slavapmk.journalTracker.storageModels.entities

import androidx.room.Embedded

data class StudentWithAttendance(
    @Embedded(prefix = "student_") val student: StudentEntity,
    @Embedded(prefix = "attendance_") val attendance: StudentAttendanceEntity
)

