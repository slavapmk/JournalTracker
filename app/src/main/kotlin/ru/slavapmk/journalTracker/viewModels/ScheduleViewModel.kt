package ru.slavapmk.journalTracker.viewModels

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.slavapmk.journalTracker.dataModels.schedule.ScheduleListLesson
import ru.slavapmk.journalTracker.dataModels.selectWeek.Week
import ru.slavapmk.journalTracker.storageModels.Dependencies
import ru.slavapmk.journalTracker.storageModels.entities.CampusEntity
import ru.slavapmk.journalTracker.storageModels.entities.LessonInfoEntity
import ru.slavapmk.journalTracker.storageModels.entities.SemesterEntity
import ru.slavapmk.journalTracker.storageModels.entities.TimeEntity
import java.util.Calendar
import java.util.Date

data class LoadScheduleData(
    val semesters: List<SemesterEntity>,
    val times: List<TimeEntity>,
    val campuses: List<CampusEntity>
)

class ScheduleViewModel : ViewModel() {
    var currentDate: Date = Calendar.Builder().apply {
        set(Calendar.YEAR, 2024)
        set(Calendar.MONTH, 9)
        set(Calendar.DATE, 1)
    }.build().time
    val lessons: MutableList<ScheduleListLesson> = mutableListOf()
    var semesterId: Int? = null
    val weeks: MutableList<Week> = mutableListOf()
    var week: Int? = null

    private val semestersMutableLiveData: MutableLiveData<List<SemesterEntity>> by lazy {
        MutableLiveData()
    }
    private val timesMutableLiveData: MutableLiveData<List<TimeEntity>> by lazy {
        MutableLiveData()
    }
    private val campusesMutableLiveData: MutableLiveData<List<CampusEntity>> by lazy {
        MutableLiveData()
    }

    //    private val studentsMutableLiveData: MutableLiveData<List<StudentEntity>> by lazy {
//        MutableLiveData()
//    }
//    private val lessonsMutableLiveData: MutableLiveData<List<LessonInfoEntity>> by lazy {
//        MutableLiveData()
//    }

    val mediatorLiveData = MediatorLiveData<LoadScheduleData>().apply {
        var semesters: List<SemesterEntity>? = null
//        var students: List<StudentEntity>? = null
//        var lessons: List<LessonInfoEntity>? = null
        var times: List<TimeEntity>? = null
        var campuses: List<CampusEntity>? = null
        fun update() {
            if (semesters != null && times != null && campuses != null) {
                value = LoadScheduleData(
                    semesters!!,
//                    students!!,
//                    lessons!!,
                    times!!,
                    campuses!!
                )
            }
        }
        addSource(semestersMutableLiveData) { value ->
            semesters = value
            update()
        }
//        addSource(studentsMutableLiveData) { value ->
//            students = value
//            update()
//        }
//        addSource(lessonsMutableLiveData) { value ->
//            lessons = value
//            update()
//        }
        addSource(timesMutableLiveData) { value ->
            times = value
            update()
        }
        addSource(campusesMutableLiveData) { value ->
            campuses = value
            update()
        }
    }

    fun loadSemesters() {
        viewModelScope.launch {
            semestersMutableLiveData.postValue(
                Dependencies.semesterRepository.getSemesters()
            )
        }
    }
    fun loadTimes() {
        viewModelScope.launch {
            timesMutableLiveData.postValue(
                Dependencies.timeRepository.getTimes()
            )
        }
    }
    fun loadCampuses() {
        viewModelScope.launch {
            campusesMutableLiveData.postValue(
                Dependencies.campusRepository.getCampuses()
            )
        }
    }

    //    fun loadStudents() {
//        viewModelScope.launch {
//            studentsMutableLiveData.postValue(
//                Dependencies.studentRepository.getStudents()
//            )
//        }
//    }
//    fun loadLessons() {
//        viewModelScope.launch {
//            lessonsMutableLiveData.postValue(
//                Dependencies.lessonInfoRepository.getLessons()
//            )
//        }
//    }
}