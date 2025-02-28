package ru.slavapmk.journalTracker.storageModels

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "student_attendance_table",
    foreignKeys = [
        ForeignKey(
            entity = StudentEntity::class,
            parentColumns = ["id"],
            childColumns = ["student_id"]
        ),
        ForeignKey(
            entity = LessonInfo::class,
            parentColumns = ["id"],
            childColumns = ["lesson_id"]
        )
    ]
)
data class StudentAttendance(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "student_id") val studentId: Int,
    @ColumnInfo(name = "lesson_id") val lessonId: Int,
    val visited: Boolean
)
