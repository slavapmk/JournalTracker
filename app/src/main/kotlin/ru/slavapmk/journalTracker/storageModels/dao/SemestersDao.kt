package ru.slavapmk.journalTracker.storageModels.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.slavapmk.journalTracker.storageModels.entities.SemesterEntity

@Dao
interface SemestersDao {
    @Insert(entity = SemesterEntity::class)
    fun insertSemester(semester: SemesterEntity): Long

    @Query("DELETE FROM semesters_table WHERE id = :semesterId")
    fun deleteTime(semesterId: Int)

    @Query("SELECT * FROM semesters_table ORDER by start_date_year, start_date_month, start_date_day")
    fun getSemesters(): List<SemesterEntity>

    @Query("SELECT * FROM semesters_table WHERE id = :semesterId")
    fun getSemester(semesterId: Int): SemesterEntity
}