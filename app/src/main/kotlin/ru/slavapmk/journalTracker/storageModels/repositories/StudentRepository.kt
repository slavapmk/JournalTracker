package ru.slavapmk.journalTracker.storageModels.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.slavapmk.journalTracker.storageModels.dao.CampusesDao
import ru.slavapmk.journalTracker.storageModels.dao.StudentsDao
import ru.slavapmk.journalTracker.storageModels.dao.TimesDao
import ru.slavapmk.journalTracker.storageModels.entities.CampusEntity
import ru.slavapmk.journalTracker.storageModels.entities.StudentEntity
import ru.slavapmk.journalTracker.storageModels.entities.TimeEntity

class StudentRepository(
    private val studentsDao: StudentsDao
) {
    suspend fun insertStudent(studentEntity: StudentEntity) = withContext(Dispatchers.IO) {
        studentsDao.insertStudent(studentEntity)
    }

    suspend fun deleteStudent(studentId: Int) = withContext(Dispatchers.IO) {
        studentsDao.deleteStudent(studentId)
    }
}
