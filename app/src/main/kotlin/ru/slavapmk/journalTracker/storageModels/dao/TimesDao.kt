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

    @Query(
        """
            DELETE FROM times_table WHERE 
                start_hour = :startHour AND start_minute = :startMinute AND
                end_hour = :endHour AND end_minute = :endMinute
        """
    )
    fun deleteTime(
        startHour: Int, startMinute: Int,
        endHour: Int, endMinute: Int
    )

    @Query("SELECT * FROM times_table ORDER by start_hour, start_minute")
    fun getTimes(): List<TimeEntity>
}