package ru.slavapmk.journalTracker.storageModels.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.slavapmk.journalTracker.storageModels.entities.CampusEntity
import ru.slavapmk.journalTracker.storageModels.entities.StudentEntity
import ru.slavapmk.journalTracker.storageModels.entities.TimeEntity
import java.sql.Time

@Dao
interface StudentsDao {
    @Insert(entity = StudentEntity::class)
    fun insertStudent(students: StudentEntity)

    @Query("DELETE FROM students_table WHERE id = :studentId")
    fun deleteStudent(studentId: Int)
}