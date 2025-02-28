package ru.slavapmk.journalTracker.storageModels.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.slavapmk.journalTracker.storageModels.entities.StudentEntity

@Dao
interface StudentsDao {
    @Insert(entity = StudentEntity::class)
    fun insertStudent(students: StudentEntity): Long

    @Query("DELETE FROM students_table WHERE id = :studentId")
    fun deleteStudent(studentId: Int)

    @Query("SELECT * FROM students_table ORDER by name")
    fun getStudents(): List<StudentEntity>
}