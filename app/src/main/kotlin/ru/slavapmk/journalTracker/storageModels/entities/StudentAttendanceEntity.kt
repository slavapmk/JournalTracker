package ru.slavapmk.journalTracker.storageModels.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ru.slavapmk.journalTracker.storageModels.StudentAttendance

@Entity(
    tableName = "student_attendance_table",
    foreignKeys = [
        ForeignKey(
            entity = StudentEntity::class,
            parentColumns = ["id"],
            childColumns = ["student_id"]
        ),
        ForeignKey(
            entity = LessonInfoEntity::class,
            parentColumns = ["id"],
            childColumns = ["lesson_id"]
        )
    ]
)
data class StudentAttendanceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "student_id") val studentId: Int,
    @ColumnInfo(name = "lesson_id") val lessonId: Int,
    val attendance: StudentAttendance
)
