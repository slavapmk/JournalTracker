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
import java.util.GregorianCalendar

data class LoadScheduleData(
    val semesters: List<SemesterEntity>,
    val times: List<TimeEntity>,
    val campuses: List<CampusEntity>
)

data class SimpleDate(
    val day: Int,
    val month: Int,
    val year: Int
)

operator fun SimpleDate.compareTo(other: SimpleDate): Int {
    return compareValuesBy(this, other, SimpleDate::year, SimpleDate::month, SimpleDate::day)
}

class ScheduleViewModel : ViewModel() {
    val lessons: MutableList<ScheduleListLesson> = mutableListOf()
    var semesters: List<SemesterEntity> = emptyList()
    var semesterId: Int? = null
    val weeks: MutableList<Week> = mutableListOf()
    private var selectedDate: SimpleDate? = null
    var week: Week? = null
    val timesMap: MutableMap<Int, TimeEntity> = mutableMapOf()
    val campusesMap: MutableMap<Int, CampusEntity> = mutableMapOf()

    private val semestersMutableLiveData: MutableLiveData<List<SemesterEntity>> by lazy {
        MutableLiveData()
    }
    private val timesMutableLiveData: MutableLiveData<List<TimeEntity>> by lazy {
        MutableLiveData()
    }
    private val campusesMutableLiveData: MutableLiveData<List<CampusEntity>> by lazy {
        MutableLiveData()
    }

    val lessonsMutableLiveData: MutableLiveData<List<LessonInfoEntity>> by lazy {
        MutableLiveData()
    }

    val mediatorLiveData = MediatorLiveData<LoadScheduleData>().apply {
        var semesters: List<SemesterEntity>? = null
        var times: List<TimeEntity>? = null
        var campuses: List<CampusEntity>? = null
        fun update() {
            if (semesters != null && times != null && campuses != null) {
                value = LoadScheduleData(
                    semesters!!,
                    times!!,
                    campuses!!
                )
            }
        }
        addSource(semestersMutableLiveData) { value ->
            semesters = value
            update()
        }
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
    fun loadLessons() {
        viewModelScope.launch {
            val date = getDate()
            lessonsMutableLiveData.postValue(
                Dependencies.lessonInfoRepository.getLessonsByDate(
                    date.day,
                    date.month,
                    date.year
                )
            )
        }
    }

    fun getDate(): SimpleDate {
        if (selectedDate == null) {
            val calendar: Calendar = GregorianCalendar.getInstance().apply {
                time = Date()
            }
            val date = SimpleDate(
                calendar[Calendar.DAY_OF_MONTH],
                calendar[Calendar.MONTH] + 1,
                calendar[Calendar.YEAR]
            )
            setDate(date)
            return date
        } else {
            return selectedDate!!
        }
    }

    fun setDate(date: SimpleDate) {
        selectedDate = date
    }
}