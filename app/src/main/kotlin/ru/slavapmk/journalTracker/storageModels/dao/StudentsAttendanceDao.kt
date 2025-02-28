package ru.slavapmk.journalTracker.storageModels.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.slavapmk.journalTracker.storageModels.entities.CampusEntity
import ru.slavapmk.journalTracker.storageModels.entities.StudentAttendanceEntity
import ru.slavapmk.journalTracker.storageModels.entities.TimeEntity
import java.sql.Time

@Dao
interface StudentsAttendanceDao {
    @Insert(entity = StudentAttendanceEntity::class)
    fun insertStudentAttendance(studentAttendance: StudentAttendanceEntity)

    @Query("DELETE FROM student_attendance_table WHERE id = :attendanceId")
    fun deleteAttendance(attendanceId: Int)
}