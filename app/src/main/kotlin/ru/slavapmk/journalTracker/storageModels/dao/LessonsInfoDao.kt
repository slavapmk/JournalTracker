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

//    @Query("SELECT * FROM lessons_table")
//    fun getLessons(): List<LessonInfoEntity>

    @Query("SELECT * FROM lessons_table WHERE date_day = :day AND date_month = :month AND date_year = :year")
    fun getLessonsByDate(day: Int, month: Int, year: Int): List<LessonInfoEntity>
}