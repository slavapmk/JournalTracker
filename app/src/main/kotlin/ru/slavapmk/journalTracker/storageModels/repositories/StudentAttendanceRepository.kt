package ru.slavapmk.journalTracker.storageModels.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.slavapmk.journalTracker.storageModels.StudentEntityAttendance
import ru.slavapmk.journalTracker.storageModels.dao.StudentsAttendanceDao
import ru.slavapmk.journalTracker.storageModels.entities.StudentAttendanceEntity

class StudentAttendanceRepository(
    private val studentsAttendanceDao: StudentsAttendanceDao
) {
    suspend fun insertAttendance(
        studentAttendanceEntity: StudentAttendanceEntity
    ) = withContext(Dispatchers.IO) {
        studentsAttendanceDao.insertStudentAttendance(studentAttendanceEntity)
    }

    suspend fun insertAttendances(
        studentAttendanceEntity: List<StudentAttendanceEntity>
    ) = withContext(Dispatchers.IO) {
        studentsAttendanceDao.insertStudentAttendances(studentAttendanceEntity)
    }

    suspend fun deleteAttendance(attendanceId: Int) = withContext(Dispatchers.IO) {
        studentsAttendanceDao.deleteAttendance(attendanceId)
    }

    suspend fun getLessonAttendance(
        lessonId: Int
    ): List<StudentAttendanceEntity> = withContext(Dispatchers.IO) {
        studentsAttendanceDao.getLessonAttendance(lessonId)
    }

    suspend fun updateAttendance(
        studentAttendanceEntity: StudentAttendanceEntity
    ) = withContext(Dispatchers.IO) {
        studentsAttendanceDao.updateAttendance(studentAttendanceEntity)
    }

    suspend fun getStudentAttendanceWithNames(
        lessonId: Int
    ) = withContext(Dispatchers.IO) {
        studentsAttendanceDao.getStudentAttendanceWithNames(lessonId)
    }

    suspend fun insertOrUpdate(
        studentId: Int, lessonId: Int, attendance: StudentEntityAttendance?
    ) = withContext(Dispatchers.IO) {
        studentsAttendanceDao.updateByStudentIdAndLessonId(studentId, lessonId, attendance)
        studentsAttendanceDao.insertIfNotExist(studentId, lessonId, attendance)
    }
}
