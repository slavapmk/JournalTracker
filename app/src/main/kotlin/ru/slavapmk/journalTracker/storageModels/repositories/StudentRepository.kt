package ru.slavapmk.journalTracker.storageModels.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.slavapmk.journalTracker.storageModels.dao.StudentsDao
import ru.slavapmk.journalTracker.storageModels.entities.StudentEntity

class StudentRepository(
    private val studentsDao: StudentsDao
) {
    suspend fun insertStudent(studentEntity: StudentEntity): Int = withContext(Dispatchers.IO) {
        studentsDao.insertStudent(studentEntity).toInt()
    }

    suspend fun deleteStudent(studentId: Int) = withContext(Dispatchers.IO) {
        studentsDao.deleteStudent(studentId)
    }

    suspend fun updateStudent(studentEntity: StudentEntity) = withContext(Dispatchers.IO) {
        studentsDao.updateStudent(studentEntity)
    }

    suspend fun getStudents(): List<StudentEntity> = withContext(Dispatchers.IO) {
        studentsDao.getStudents()
    }
}
