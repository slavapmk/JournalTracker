package ru.slavapmk.journalTracker.storageModels.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.slavapmk.journalTracker.storageModels.dao.CampusesDao
import ru.slavapmk.journalTracker.storageModels.entities.CampusEntity

class CampusRepository(
    private val campusesDao: CampusesDao
) {
    suspend fun insertCampus(campusEntity: CampusEntity) = withContext(Dispatchers.IO) {
        campusesDao.insertCampus(campusEntity)
    }

    suspend fun deleteCampus(campusId: Int) = withContext(Dispatchers.IO) {
        campusesDao.deleteCampus(campusId)
    }

    suspend fun getCampuses(): List<CampusEntity> = withContext(Dispatchers.IO) {
        campusesDao.getCampuses()
    }
}
