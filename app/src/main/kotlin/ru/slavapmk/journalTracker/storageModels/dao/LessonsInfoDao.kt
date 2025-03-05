package ru.slavapmk.journalTracker.storageModels.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.slavapmk.journalTracker.storageModels.entities.InsertLesson
import ru.slavapmk.journalTracker.storageModels.entities.LessonInfoEntity

@Dao
interface LessonsInfoDao {
    @Insert(entity = LessonInfoEntity::class)
    fun insertLesson(lesson: LessonInfoEntity)

    @Insert(entity = LessonInfoEntity::class)
    fun insertLessons(lessons: List<InsertLesson>)

    @Query("DELETE FROM lessons_table WHERE id = :lessonId")
    fun deleteLesson(lessonId: Int)

//    @Query("SELECT * FROM lessons_table")
//    fun getLessons(): List<LessonInfoEntity>

    @Query("SELECT * FROM lessons_table WHERE date_day = :day AND date_month = :month AND date_year = :year")
    fun getLessonsByDate(day: Int, month: Int, year: Int): List<LessonInfoEntity>

    @Query("SELECT * FROM lessons_table WHERE id = :id LIMIT 1")
    fun getLessonById(id: Int): LessonInfoEntity

    @Query("DELETE FROM lessons_table WHERE date_day = :day AND date_month = :month AND date_year = :year AND time_id = :timeId")
    fun deleteLessonsByDateTime(day: Int, month: Int, year: Int, timeId: Int)

    @Query(
        """
            UPDATE lessons_table
            SET semester_id = :semesterId, name = :name, type = :type,
                time_id = :timeId, teacher = :teacher, cabinet = :cabinet, 
                campus_id = :campusId
            WHERE date_day = :dateDay AND date_month = :dateMonth AND date_year = :dateYear
        """
    )
    fun updateByDateTime(
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
    )

    @Query("DELETE FROM lessons_table WHERE date_day = :day AND date_month = :month AND date_year = :year AND time_id = :timeId AND name = :name")
    fun deleteLessonsByDateTimeName(day: Int, month: Int, year: Int, timeId: Int, name: String)
}