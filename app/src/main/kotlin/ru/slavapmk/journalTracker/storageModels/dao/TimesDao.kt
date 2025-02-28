package ru.slavapmk.journalTracker.storageModels.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.slavapmk.journalTracker.storageModels.entities.CampusEntity
import ru.slavapmk.journalTracker.storageModels.entities.TimeEntity
import java.sql.Time

@Dao
interface TimesDao {
    @Insert(entity = TimeEntity::class)
    fun insertTime(campus: CampusEntity)

    @Query("DELETE FROM times_table WHERE id = :timeId")
    fun deleteTime(timeId: Int)
}