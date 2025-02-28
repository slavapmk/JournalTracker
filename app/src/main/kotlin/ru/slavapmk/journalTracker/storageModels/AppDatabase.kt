package ru.slavapmk.journalTracker.storageModels

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.slavapmk.journalTracker.storageModels.entities.CampusEntity
import ru.slavapmk.journalTracker.storageModels.entities.LessonInfoEntity
import ru.slavapmk.journalTracker.storageModels.entities.SemesterEntity
import ru.slavapmk.journalTracker.storageModels.entities.StudentAttendanceEntity
import ru.slavapmk.journalTracker.storageModels.entities.StudentEntity
import ru.slavapmk.journalTracker.storageModels.entities.TimeEntity
import ru.slavapmk.journalTracker.storageModels.dao.CampusesDao

@Database(
    version = 1,
    entities = [
        CampusEntity::class,
        LessonInfoEntity::class,
        SemesterEntity::class,
        StudentAttendanceEntity::class,
        StudentEntity::class,
        TimeEntity::class
    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getLessonsDao(): CampusesDao
}