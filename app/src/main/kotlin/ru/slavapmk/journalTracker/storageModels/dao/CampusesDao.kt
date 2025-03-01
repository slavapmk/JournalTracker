package ru.slavapmk.journalTracker.storageModels.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.slavapmk.journalTracker.storageModels.entities.CampusEntity

@Dao
interface CampusesDao {
    @Insert(entity = CampusEntity::class)
    fun insertCampus(campus: CampusEntity): Long

    @Query("DELETE FROM campuses_table WHERE id = :campusId")
    fun deleteCampus(campusId: Int)

    @Query("SELECT * FROM campuses_table")
    fun getCampuses(): List<CampusEntity>
}