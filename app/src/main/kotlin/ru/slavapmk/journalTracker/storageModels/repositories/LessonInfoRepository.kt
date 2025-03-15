package ru.slavapmk.journalTracker.storageModels.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.slavapmk.journalTracker.storageModels.LessonEntityType
import ru.slavapmk.journalTracker.storageModels.dao.LessonsInfoDao
import ru.slavapmk.journalTracker.storageModels.entities.InsertLesson
import ru.slavapmk.journalTracker.storageModels.entities.LessonInfoEntity

class LessonInfoRepository(
    private val lessonsInfoDao: LessonsInfoDao
) {
    suspend fun insertLessons(
        lessonInfoEntities: List<InsertLesson>
    ) = withContext(Dispatchers.IO) {
        lessonsInfoDao.insertLessons(lessonInfoEntities)
    }

    suspend fun getLessonsByDate(
        day: Int, month: Int, year: Int
    ): List<LessonInfoEntity> = withContext(Dispatchers.IO) {
        lessonsInfoDao.getLessonsByDate(day, month, year)
    }

    suspend fun getLessonById(
        id: Int
    ): LessonInfoEntity = withContext(Dispatchers.IO) {
        lessonsInfoDao.getLessonById(id)
    }

    suspend fun deleteLessonsByDateTime(
        day: Int, month: Int, year: Int, timeId: Int
    ) = withContext(Dispatchers.IO) {
        lessonsInfoDao.deleteLessonsByDateTime(day, month, year, timeId)
    }

    suspend fun deleteLessonsByDateTimeName(
        day: Int, month: Int, year: Int, timeId: Int, name: String
    ) = withContext(Dispatchers.IO) {
        lessonsInfoDao.deleteLessonsByDateTimeName(day, month, year, timeId, name)
    }

    suspend fun updateByDateTime(
        oldTime: Int,
        dateDay: Int,
        dateMonth: Int,
        dateYear: Int,
        semesterId: Int,
        name: String,
        type: LessonEntityType,
        timeId: Int,
        teacher: String,
        cabinet: Int,
        campusId: Int
    ) = withContext(Dispatchers.IO) {
        lessonsInfoDao.updateByDateTime(
            oldTime,
            dateDay,
            dateMonth,
            dateYear,
            semesterId,
            name,
            type,
            timeId,
            teacher,
            cabinet,
            campusId
        )
    }

    suspend fun getLessonNames(): List<String> = withContext(Dispatchers.IO) {
        lessonsInfoDao.getLessonNames()
    }

    suspend fun getTeacherNames(): List<String> = withContext(Dispatchers.IO) {
        lessonsInfoDao.getTeacherNames()
    }

    suspend fun getAllCabinets(): List<Int> = withContext(Dispatchers.IO) {
        lessonsInfoDao.getAllCabinets()
    }

    suspend fun getCabinets(campusId: Int): List<Int> = withContext(Dispatchers.IO) {
        lessonsInfoDao.getCabinets(campusId)
    }
}
