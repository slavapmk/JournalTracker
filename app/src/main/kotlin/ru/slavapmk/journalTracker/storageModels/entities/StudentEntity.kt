package ru.slavapmk.journalTracker.storageModels.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.slavapmk.journalTracker.storageModels.StudentAttendance

@Entity(tableName = "students_table")
data class StudentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String,
    val default: StudentAttendance?
)
