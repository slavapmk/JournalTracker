package ru.slavapmk.journalTracker.storageModels.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.slavapmk.journalTracker.storageModels.LessonEntityType
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

    @Query("""
        SELECT lessons_table.*
        FROM lessons_table
        JOIN times_table ON lessons_table.time_id = times_table.id
        WHERE date_day = :day AND date_month = :month AND date_year = :year
        ORDER BY times_table.start_hour, times_table.start_minute
    """)
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
            WHERE date_day = :dateDay AND date_month = :dateMonth AND
                  date_year = :dateYear AND time_id = :oldTime
        """
    )
    fun updateByDateTime(
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
    )

    @Query("DELETE FROM lessons_table WHERE date_day = :day AND date_month = :month AND date_year = :year AND time_id = :timeId AND name = :name")
    fun deleteLessonsByDateTimeName(day: Int, month: Int, year: Int, timeId: Int, name: String)

    @Query("SELECT DISTINCT name FROM lessons_table ORDER BY name LIMIT 200")
    fun getLessonNames(): List<String>

    @Query("SELECT DISTINCT teacher FROM lessons_table ORDER BY teacher LIMIT 200")
    fun getTeacherNames(): List<String>

    @Query("SELECT DISTINCT cabinet FROM lessons_table ORDER BY cabinet LIMIT 200")
    fun getAllCabinets(): List<Int>

    @Query("SELECT DISTINCT cabinet FROM lessons_table WHERE campus_id = :campusId ORDER BY cabinet LIMIT 200")
    fun getCabinets(campusId: Int): List<Int>
}