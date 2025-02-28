package ru.slavapmk.journalTracker.storageModels.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.slavapmk.journalTracker.storageModels.dao.CampusesDao
import ru.slavapmk.journalTracker.storageModels.dao.LessonsInfoDao
import ru.slavapmk.journalTracker.storageModels.dao.TimesDao
import ru.slavapmk.journalTracker.storageModels.entities.CampusEntity
import ru.slavapmk.journalTracker.storageModels.entities.LessonInfoEntity
import ru.slavapmk.journalTracker.storageModels.entities.TimeEntity

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
}
