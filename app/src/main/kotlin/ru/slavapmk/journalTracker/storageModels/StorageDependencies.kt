package ru.slavapmk.journalTracker.storageModels

import android.content.Context
import androidx.room.Room
import ru.slavapmk.journalTracker.storageModels.repositories.CampusRepository
import ru.slavapmk.journalTracker.storageModels.repositories.LessonInfoRepository
import ru.slavapmk.journalTracker.storageModels.repositories.SemesterRepository
import ru.slavapmk.journalTracker.storageModels.repositories.StudentAttendanceRepository
import ru.slavapmk.journalTracker.storageModels.repositories.StudentRepository
import ru.slavapmk.journalTracker.storageModels.repositories.TimeRepository

object StorageDependencies {
    private lateinit var applicationContext: Context

    fun init(context: Context) {
        applicationContext = context
    }

    const val DB_NAME = "database.db"

    private var _appDatabase: AppDatabase? = null
    private val appDatabase: AppDatabase
        get() {
            if (_appDatabase == null || !_appDatabase!!.isOpen) {
                _appDatabase = Room.databaseBuilder(
                    applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                ).build()
            }
            return _appDatabase!!
        }

    val campusRepository: CampusRepository by lazy {
        CampusRepository(appDatabase.getCampusDao())
    }

    val lessonInfoRepository: LessonInfoRepository by lazy {
        LessonInfoRepository(appDatabase.getLessonsDao())
    }

    val semesterRepository: SemesterRepository by lazy {
        SemesterRepository(appDatabase.getSemestersDao())
    }

    val studentsAttendanceRepository: StudentAttendanceRepository by lazy {
        StudentAttendanceRepository(appDatabase.getAttendanceDao())
    }

    val studentRepository: StudentRepository by lazy {
        StudentRepository(appDatabase.getStudentsDao())
    }

    val timeRepository: TimeRepository by lazy {
        TimeRepository(appDatabase.getTimesDao())
    }

    fun closeDb() {
        appDatabase.close()
        _appDatabase = null
    }
}