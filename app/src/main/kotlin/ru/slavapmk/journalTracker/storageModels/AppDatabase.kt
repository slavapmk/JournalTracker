package ru.slavapmk.journalTracker.storageModels

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.slavapmk.journalTracker.storageModels.dao.CampusesDao
import ru.slavapmk.journalTracker.storageModels.dao.LessonsInfoDao
import ru.slavapmk.journalTracker.storageModels.dao.SemestersDao
import ru.slavapmk.journalTracker.storageModels.dao.StudentsAttendanceDao
import ru.slavapmk.journalTracker.storageModels.dao.StudentsDao
import ru.slavapmk.journalTracker.storageModels.dao.TimesDao
import ru.slavapmk.journalTracker.storageModels.entities.CampusEntity
import ru.slavapmk.journalTracker.storageModels.entities.LessonInfoEntity
import ru.slavapmk.journalTracker.storageModels.entities.SemesterEntity
import ru.slavapmk.journalTracker.storageModels.entities.StudentAttendanceEntity
import ru.slavapmk.journalTracker.storageModels.entities.StudentEntity
import ru.slavapmk.journalTracker.storageModels.entities.TimeEntity

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
@TypeConverters(
    AttendanceConverter::class,
    LessonTypeConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getLessonsDao(): LessonsInfoDao
    abstract fun getTimesDao(): TimesDao
    abstract fun getStudentsDao(): StudentsDao
    abstract fun getSemestersDao(): SemestersDao
    abstract fun getAttendanceDao(): StudentsAttendanceDao
    abstract fun getCampusDao(): CampusesDao
}