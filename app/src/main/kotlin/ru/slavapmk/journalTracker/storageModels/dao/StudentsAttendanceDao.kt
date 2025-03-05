package ru.slavapmk.journalTracker.storageModels.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ru.slavapmk.journalTracker.storageModels.entities.StudentAttendanceEntity

@Dao
interface StudentsAttendanceDao {
    @Insert(entity = StudentAttendanceEntity::class)
    fun insertStudentAttendance(studentAttendance: StudentAttendanceEntity)

    @Query("DELETE FROM student_attendance_table WHERE id = :attendanceId")
    fun deleteAttendance(attendanceId: Int)

    @Query("SELECT * FROM student_attendance_table WHERE lesson_id = :lessonId")
    fun getLessonAttendance(lessonId: Int): List<StudentAttendanceEntity>

    @Insert(entity = StudentAttendanceEntity::class)
    fun insertStudentAttendances(studentAttendanceEntity: List<StudentAttendanceEntity>)

    @Update(entity = StudentAttendanceEntity::class)
    fun updateAttendance(studentAttendanceEntity: StudentAttendanceEntity)
}