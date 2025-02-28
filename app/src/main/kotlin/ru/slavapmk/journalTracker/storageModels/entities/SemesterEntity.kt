package ru.slavapmk.journalTracker.storageModels.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "semesters_table")
data class SemesterEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "start_date_day") val startDay: Int,
    @ColumnInfo(name = "start_date_month") val startMonth: Int,
    @ColumnInfo(name = "start_date_year") val startYear: Int,
    @ColumnInfo(name = "end_date_day") val endDay: Int,
    @ColumnInfo(name = "end_date_month") val endMonth: Int,
    @ColumnInfo(name = "end_date_year") val endYear: Int,
)
