package ru.slavapmk.journalTracker.storageModels.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.slavapmk.journalTracker.storageModels.dao.TimesDao
import ru.slavapmk.journalTracker.storageModels.entities.TimeEntity

class TimeRepository(
    private val timesDao: TimesDao
) {
    suspend fun insertTime(timeEntity: TimeEntity) = withContext(Dispatchers.IO) {
        timesDao.insertTime(timeEntity)
    }

    suspend fun deleteTime(timeId: Int) = withContext(Dispatchers.IO) {
        timesDao.deleteTime(timeId)
    }

    suspend fun deleteTime(
        startHour: Int, startMinute: Int,
        endHour: Int, endMinute: Int
    ) = withContext(Dispatchers.IO) {
        timesDao.deleteTime(startHour, startMinute, endHour, endMinute)
    }

    suspend fun getTimes(): List<TimeEntity> = withContext(Dispatchers.IO) {
        timesDao.getTimes()
    }
}
