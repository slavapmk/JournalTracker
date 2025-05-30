package ru.slavapmk.journalTracker.storageModels.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ru.slavapmk.journalTracker.storageModels.StudentEntityAttendance

@Entity(
    tableName = "student_attendance_table",
    foreignKeys = [
        ForeignKey(
            entity = StudentEntity::class,
            parentColumns = ["id"],
            childColumns = ["student_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = LessonInfoEntity::class,
            parentColumns = ["id"],
            childColumns = ["lesson_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class StudentAttendanceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "student_id", index = true) val studentId: Int,
    @ColumnInfo(name = "lesson_id", index = true) val lessonId: Int,
    val attendance: StudentEntityAttendance?,
    @ColumnInfo(name = "skip_description") val skipDescription: String?
)
