package ru.slavapmk.journalTracker.storageModels.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.slavapmk.journalTracker.storageModels.dao.LessonsInfoDao
import ru.slavapmk.journalTracker.storageModels.entities.LessonInfoEntity

class LessonInfoRepository(
    private val lessonsInfoDao: LessonsInfoDao
) {
    suspend fun insertLesson(lessonInfoEntity: LessonInfoEntity) = withContext(Dispatchers.IO) {
        lessonsInfoDao.insertLesson(lessonInfoEntity)
    }

    suspend fun insertLessons(
        lessonInfoEntities: List<LessonInfoEntity>
    ) = withContext(Dispatchers.IO) {
        lessonsInfoDao.insertLessons(lessonInfoEntities)
    }

    suspend fun deleteLessons(lessonId: Int) = withContext(Dispatchers.IO) {
        lessonsInfoDao.deleteLesson(lessonId)
    }

//    suspend fun getLessons(): List<LessonInfoEntity> = withContext(Dispatchers.IO) {
//        lessonsInfoDao.getLessons()
//    }

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

    suspend fun updateByDateTime(
        dateDay: Int,
        dateMonth: Int,
        dateYear: Int,
        semesterId: Int,
        name: String,
        type: String,
        timeId: Int,
        teacher: String,
        cabinet: Int,
        campusId: Int
    ) = withContext(Dispatchers.IO) {
        lessonsInfoDao.updateByDateTime(
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
}
