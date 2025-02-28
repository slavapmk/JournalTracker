package ru.slavapmk.journalTracker.storageModels.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.slavapmk.journalTracker.storageModels.dao.SemestersDao
import ru.slavapmk.journalTracker.storageModels.entities.SemesterEntity

class SemesterRepository(
    private val semestersDao: SemestersDao
) {
    suspend fun insertSemester(semesterEntity: SemesterEntity): Int = withContext(Dispatchers.IO) {
        semestersDao.insertSemester(semesterEntity).toInt()
    }

    suspend fun deleteSemester(semesterId: Int) = withContext(Dispatchers.IO) {
        semestersDao.deleteTime(semesterId)
    }

    suspend fun getSemesters(): List<SemesterEntity> = withContext(Dispatchers.IO) {
        semestersDao.getSemesters()
    }
}
