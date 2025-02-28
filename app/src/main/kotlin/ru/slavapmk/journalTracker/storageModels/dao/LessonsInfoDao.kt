package ru.slavapmk.journalTracker.storageModels.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.slavapmk.journalTracker.storageModels.entities.CampusEntity
import ru.slavapmk.journalTracker.storageModels.entities.LessonInfoEntity
import ru.slavapmk.journalTracker.storageModels.entities.TimeEntity
import java.sql.Time

@Dao
interface LessonsInfoDao {
    @Insert(entity = LessonInfoEntity::class)
    fun insertLesson(lesson: LessonInfoEntity)

    @Insert(entity = LessonInfoEntity::class)
    fun insertLessons(lessons: List<LessonInfoEntity>)

    @Query("DELETE FROM lessons_table WHERE id = :lessonId")
    fun deleteLesson(lessonId: Int)
}