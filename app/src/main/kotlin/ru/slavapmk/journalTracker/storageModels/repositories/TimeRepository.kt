package ru.slavapmk.journalTracker.storageModels.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.slavapmk.journalTracker.storageModels.dao.CampusesDao
import ru.slavapmk.journalTracker.storageModels.dao.TimesDao
import ru.slavapmk.journalTracker.storageModels.entities.CampusEntity
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

    suspend fun getTimes(): List<TimeEntity> = withContext(Dispatchers.IO) {
        timesDao.getTimes()
    }
}
