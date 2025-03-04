package ru.slavapmk.journalTracker.viewModels

import android.content.SharedPreferences
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.slavapmk.journalTracker.dataModels.lessonEdit.LessonEditInfo
import ru.slavapmk.journalTracker.storageModels.StorageDependencies
import ru.slavapmk.journalTracker.storageModels.entities.CampusEntity
import ru.slavapmk.journalTracker.storageModels.entities.InsertLesson
import ru.slavapmk.journalTracker.storageModels.entities.LessonInfoEntity
import ru.slavapmk.journalTracker.storageModels.entities.SemesterEntity
import ru.slavapmk.journalTracker.storageModels.entities.TimeEntity
import ru.slavapmk.journalTracker.ui.SharedKeys
import java.time.LocalDate

data class EditLessonLoadData(
    val times: List<TimeEntity>,
    val semesters: List<SemesterEntity>,
    val campuses: List<CampusEntity>,
)

class EditLessonViewModel : ViewModel() {
    var sharedPreferences: SharedPreferences? = null

    lateinit var info: LessonEditInfo

    val times: MutableList<TimeEntity> = mutableListOf()
    val campuses: MutableList<CampusEntity> = mutableListOf()
    var date: SimpleDate? = null
    var semester: SemesterEntity? = null
    val weekTypes by lazy {
        sharedPreferences!!.getInt(SharedKeys.WEEK_TYPES_KEY, 1)
    }

    val savingStatusLiveData: MutableLiveData<Unit> by lazy { MutableLiveData() }
    val lessonLiveData: MutableLiveData<LessonInfoEntity> by lazy { MutableLiveData() }
    private val timesLiveData: MutableLiveData<List<TimeEntity>> by lazy { MutableLiveData() }
    private val semestersLiveData: MutableLiveData<List<SemesterEntity>> by lazy { MutableLiveData() }
    private val campusesLiveData: MutableLiveData<List<CampusEntity>> by lazy { MutableLiveData() }

    val loadLiveData: MediatorLiveData<EditLessonLoadData> =
        MediatorLiveData<EditLessonLoadData>().apply {
            var times: List<TimeEntity>? = null
            var semesters: List<SemesterEntity>? = null
            var campuses: List<CampusEntity>? = null

            fun update() {
                if (times != null && semesters != null && campuses != null) {
                    value = EditLessonLoadData(
                        times!!,
                        semesters!!,
                        campuses!!
                    )
                    times = null
                    semesters = null
                    campuses = null
                }
            }
            addSource(timesLiveData) { value ->
                times = value
                update()
            }
            addSource(semestersLiveData) { value ->
                semesters = value
                update()
            }
            addSource(campusesLiveData) { value ->
                campuses = value
                update()
            }
        }

    val collisionCheckLive: MutableLiveData<Boolean> by lazy {
        MutableLiveData()
    }

    fun loadData() {
        viewModelScope.launch {
            timesLiveData.postValue(
                StorageDependencies.timeRepository.getTimes()
            )
        }
        viewModelScope.launch {
            semestersLiveData.postValue(
                StorageDependencies.semesterRepository.getSemesters()
            )
        }

        viewModelScope.launch {
            campusesLiveData.postValue(
                StorageDependencies.campusRepository.getCampuses()
            )
        }
    }

    fun loadLesson(id: Int) {
        viewModelScope.launch {
            lessonLiveData.postValue(
                StorageDependencies.lessonInfoRepository.getLessonById(id)
            )
        }
    }

    fun checkCollisions(lesson: LessonEditInfo) {
        if (date == null) {
            throw IllegalStateException("Date must not be null")
        }
        if (lesson.index == null) {
            throw IllegalStateException("Index must not be null")
        }
        val lessonId = times[lesson.index!!].id
        viewModelScope.launch {
            val usedTimes: List<Int> = StorageDependencies.lessonInfoRepository.getLessonsByDate(
                date!!.day,
                date!!.month,
                date!!.year,
            ).map {
                it.timeId
            }

            collisionCheckLive.postValue(
                usedTimes.contains(lessonId)
            )
        }
    }

    fun genDates(fromDate: SimpleDate, toDate: SimpleDate, weekOffset: Int): List<SimpleDate> {
        val dates = mutableListOf<SimpleDate>()

        var current = LocalDate.of(fromDate.year, fromDate.month, fromDate.day)
        val end = LocalDate.of(toDate.year, toDate.month, toDate.day)

        while (!current.isAfter(end)) {
            dates.add(SimpleDate(current.dayOfMonth, current.monthValue, current.year))
            current = current.plusWeeks(weekOffset.toLong())
        }

        return dates
    }

    fun addNewLesson(updateNext: Boolean) {
        val resultDates = if (updateNext) {
            val toDate = SimpleDate(semester!!.endDay, semester!!.endMonth, semester!!.endYear)
            genDates(date!!, toDate, weekTypes)
        } else {
            mutableListOf(date)
        }
        val addLessons = resultDates.map {
            InsertLesson(
                semester!!.id,
                info.name!!,
                info.typeName!!,
                it!!.day,
                it.month,
                it.year,
                times[info.index!!].id,
                info.teacher!!,
                info.cabinet!!,
                info.campusId!!
            )
        }
        viewModelScope.launch {
            for (date1 in resultDates) {
                StorageDependencies.lessonInfoRepository.deleteLessonsByDateTime(
                    date1!!.day,
                    date1.month,
                    date1.year,
                    times[info.index!!].id,
                )
            }
            StorageDependencies.lessonInfoRepository.insertLessons(addLessons)
            savingStatusLiveData.postValue(Unit)
        }
    }

    fun updateLessons(updateNext: Boolean) {
        val resultDates = if (updateNext) {
            val toDate = SimpleDate(semester!!.endDay, semester!!.endMonth, semester!!.endYear)
            genDates(date!!, toDate, weekTypes)
        } else {
            mutableListOf(date)
        }
        val addLessons = resultDates.map {
            InsertLesson(
                semester!!.id,
                info.name!!,
                info.typeName!!,
                it!!.day,
                it.month,
                it.year,
                times[info.index!!].id,
                info.teacher!!,
                info.cabinet!!,
                info.campusId!!
            )
        }
        viewModelScope.launch {
            for (addLesson in addLessons) {
                StorageDependencies.lessonInfoRepository.updateByDateTime(
                    addLesson.dateDay,
                    addLesson.dateMonth,
                    addLesson.dateDay,
                    addLesson.semesterId,
                    addLesson.name,
                    addLesson.type,
                    addLesson.timeId,
                    addLesson.teacher,
                    addLesson.cabinet,
                    addLesson.campusId
                )
            }
            savingStatusLiveData.postValue(Unit)
        }
    }
}
