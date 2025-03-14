package ru.slavapmk.journalTracker.storageModels.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import ru.slavapmk.journalTracker.storageModels.StudentEntityAttendance
import ru.slavapmk.journalTracker.storageModels.entities.StudentAttendanceEntity
import ru.slavapmk.journalTracker.storageModels.entities.StudentWithAttendance

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

    @Transaction
    @Query("""
        SELECT 
            students_table.id AS student_id,
            students_table.name AS student_name,
            students_table.`default` AS student_default,
            student_attendance_table.id AS attendance_id,
            student_attendance_table.student_id AS attendance_student_id,
            student_attendance_table.lesson_id AS attendance_lesson_id,
            student_attendance_table.attendance AS attendance_attendance,
            student_attendance_table.skip_description AS attendance_skip_description
        FROM student_attendance_table
        INNER JOIN students_table ON students_table.id = student_attendance_table.student_id
        WHERE student_attendance_table.lesson_id = :lessonId
        ORDER BY student_name
    """)
    fun getStudentAttendanceWithNames(lessonId: Int): List<StudentWithAttendance>

    @Query("""
        UPDATE student_attendance_table
            SET attendance = :attendance, skip_description = NULL
            WHERE student_id = :studentId AND lesson_id = :lessonId
    """)
    fun updateByStudentIdAndLessonId(studentId: Int, lessonId: Int, attendance: StudentEntityAttendance?)

    @Query("""
        INSERT INTO student_attendance_table (student_id, lesson_id, attendance, skip_description)
        SELECT :studentId, :lessonId, :attendance, NULL
        WHERE NOT EXISTS (
            SELECT 1 FROM student_attendance_table WHERE
                student_id = :studentId AND lesson_id = :lessonId
        )
    """)
    fun insertIfNotExist(studentId: Int, lessonId: Int, attendance: StudentEntityAttendance?)
}