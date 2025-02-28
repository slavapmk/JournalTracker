package ru.slavapmk.journalTracker.storageModels.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.slavapmk.journalTracker.storageModels.entities.CampusEntity
import ru.slavapmk.journalTracker.storageModels.entities.SemesterEntity
import ru.slavapmk.journalTracker.storageModels.entities.TimeEntity
import java.sql.Time

@Dao
interface SemestersDao {
    @Insert(entity = SemesterEntity::class)
    fun insertSemester(semester: SemesterEntity)

    @Query("DELETE FROM semesters_table WHERE id = :semesterId")
    fun deleteTime(semesterId: Int)

    @Query("SELECT * FROM semesters_table")
    fun getSemesters(): List<SemesterEntity>
}