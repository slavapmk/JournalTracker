package ru.slavapmk.journalTracker.storageModels.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "lessons_table",
    foreignKeys = [
        ForeignKey(
            entity = SemesterEntity::class,
            parentColumns = ["id"],
            childColumns = ["semester_id"]
        ),
        ForeignKey(
            entity = TimeEntity::class,
            parentColumns = ["id"],
            childColumns = ["time_id"]
        ),
        ForeignKey(
            entity = CampusEntity::class,
            parentColumns = ["id"],
            childColumns = ["campus_id"]
        )
    ]
)
data class LessonInfoEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "semester_id") val semesterId: Int,
    val name: String,
    val type: String,
    @ColumnInfo(name = "date_day") val dateDay: Int,
    @ColumnInfo(name = "date_month") val dateMonth: Int,
    @ColumnInfo(name = "date_year") val dateYear: Int,
    @ColumnInfo(name = "time_id") val timeId: Int,
    @ColumnInfo(name = "teacher") val teacher: String,
    @ColumnInfo(name = "cabinet") val cabinet: Int,
    @ColumnInfo(name = "campus_id") val campusId: Int,
)