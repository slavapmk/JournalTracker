package ru.slavapmk.journalTracker.storageModels.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.slavapmk.journalTracker.storageModels.entities.TimeEntity

@Dao
interface TimesDao {
    @Insert(entity = TimeEntity::class)
    fun insertTime(time: TimeEntity)

    @Query("DELETE FROM times_table WHERE id = :timeId")
    fun deleteTime(timeId: Int)

    @Query("SELECT * FROM times_table")
    fun getTimes(): List<TimeEntity>
}