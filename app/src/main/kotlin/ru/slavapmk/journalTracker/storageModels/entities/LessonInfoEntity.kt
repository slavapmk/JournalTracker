package ru.slavapmk.journalTracker.storageModels.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import ru.slavapmk.journalTracker.storageModels.LessonEntityType

@Entity(
    tableName = "lessons_table",
    foreignKeys = [
        ForeignKey(
            entity = SemesterEntity::class,
            parentColumns = ["id"],
            childColumns = ["semester_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TimeEntity::class,
            parentColumns = ["id"],
            childColumns = ["time_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CampusEntity::class,
            parentColumns = ["id"],
            childColumns = ["campus_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class LessonInfoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "semester_id", index = true) val semesterId: Int,
    val name: String,
    val type: LessonEntityType,
    @ColumnInfo(name = "date_day") val dateDay: Int,
    @ColumnInfo(name = "date_month") val dateMonth: Int,
    @ColumnInfo(name = "date_year") val dateYear: Int,
    @ColumnInfo(name = "time_id", index = true) val timeId: Int,
    @ColumnInfo(name = "teacher") val teacher: String,
    @ColumnInfo(name = "cabinet") val cabinet: Int,
    @ColumnInfo(name = "campus_id", index = true) val campusId: Int,
)

data class InsertLesson(
    @ColumnInfo(name = "semester_id", index = true) val semesterId: Int,
    val name: String,
    val type: LessonEntityType,
    @ColumnInfo(name = "date_day") val dateDay: Int,
    @ColumnInfo(name = "date_month") val dateMonth: Int,
    @ColumnInfo(name = "date_year") val dateYear: Int,
    @ColumnInfo(name = "time_id", index = true) val timeId: Int,
    @ColumnInfo(name = "teacher") val teacher: String,
    @ColumnInfo(name = "cabinet") val cabinet: Int,
    @ColumnInfo(name = "campus_id", index = true) val campusId: Int
)