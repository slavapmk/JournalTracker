package ru.slavapmk.journalTracker.storageModels.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.slavapmk.journalTracker.storageModels.dao.CampusesDao
import ru.slavapmk.journalTracker.storageModels.dao.StudentsAttendanceDao
import ru.slavapmk.journalTracker.storageModels.dao.TimesDao
import ru.slavapmk.journalTracker.storageModels.entities.CampusEntity
import ru.slavapmk.journalTracker.storageModels.entities.StudentAttendanceEntity
import ru.slavapmk.journalTracker.storageModels.entities.TimeEntity

class StudentAttendanceRepository(
    private val studentsAttendanceDao: StudentsAttendanceDao
) {
    suspend fun insertAttendance(
        studentAttendanceEntity: StudentAttendanceEntity
    ) = withContext(Dispatchers.IO) {
        studentsAttendanceDao.insertStudentAttendance(studentAttendanceEntity)
    }

    suspend fun deleteAttendance(attendanceId: Int) = withContext(Dispatchers.IO) {
        studentsAttendanceDao.deleteAttendance(attendanceId)
    }
}
