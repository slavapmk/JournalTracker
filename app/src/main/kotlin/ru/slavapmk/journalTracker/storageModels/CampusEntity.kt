package ru.slavapmk.journalTracker.storageModels

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "campuses_table")
data class CampusEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "codename") val codename: String,
    @ColumnInfo(name = "name") val name: String,
)
