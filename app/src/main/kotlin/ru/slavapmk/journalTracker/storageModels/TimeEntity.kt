package ru.slavapmk.journalTracker.storageModels

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "times_table")
data class TimeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "start_hour") val startHour: Int,
    @ColumnInfo(name = "start_minute") val startMinute: Int,
    @ColumnInfo(name = "end_hour") val endHour: Int,
    @ColumnInfo(name = "end_minute") val endMinute: Int
)
