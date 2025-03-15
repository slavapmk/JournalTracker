package ru.slavapmk.journalTracker.viewModels

import android.content.SharedPreferences
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.slavapmk.journalTracker.dataModels.lessonEdit.LessonEditInfo
import ru.slavapmk.journalTracker.dataModels.toEntity
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

    val allLessonNames = mutableListOf<String>()
    val allTeachers = mutableListOf<String>()
    val allCabinets = mutableListOf<Int>()
    val allCabinetsNames: Array<String>
        get() = allCabinets.map { it.toString() }.toTypedArray()

    val allLessonLiveData by lazy { MediatorLiveData<List<String>>() }
    val allTeachersLiveData by lazy { MediatorLiveData<List<String>>() }
    val allCabinetsLiveData by lazy { MediatorLiveData<List<Int>>() }

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

    fun loadData() {
        viewModelScope.launch {
            allLessonLiveData.postValue(
                StorageDependencies.lessonInfoRepository.getLessonNames()
            )
            allTeachersLiveData.postValue(
                StorageDependencies.lessonInfoRepository.getTeacherNames()
            )
            timesLiveData.postValue(
                StorageDependencies.timeRepository.getTimes()
            )
            semestersLiveData.postValue(
                StorageDependencies.semesterRepository.getSemesters()
            )
            campusesLiveData.postValue(
                StorageDependencies.campusRepository.getCampuses()
            )
        }
    }

    fun loadCabinets(campusId: Int?) {
        viewModelScope.launch {
            if (campusId == null) {
                allCabinetsLiveData.postValue(
                    StorageDependencies.lessonInfoRepository.getAllCabinets()
                )
            } else {
                allCabinetsLiveData.postValue(
                    StorageDependencies.lessonInfoRepository.getCabinets(campusId)
                )
            }
        }
    }

    fun loadLesson(id: Int) {
        viewModelScope.launch {
            lessonLiveData.postValue(
                StorageDependencies.lessonInfoRepository.getLessonById(id)
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
                info.typeName!!.toEntity(),
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
            genDates(
                date!!,
                SimpleDate(semester!!.endDay, semester!!.endMonth, semester!!.endYear),
                weekTypes
            )
        } else {
            listOf(date)
        }
        val addLessons = resultDates.map {
            InsertLesson(
                semester!!.id,
                info.name!!,
                info.typeName!!.toEntity(),
                it!!.day,
                it.month,
                it.year,
                times[info.index!!].id,
                info.teacher!!,
                info.cabinet!!,
                info.campusId!!
            ) to times[info.oldTime!!].id
        }
        viewModelScope.launch {
            for ((addLesson, addOldTime) in addLessons) {
                StorageDependencies.lessonInfoRepository.updateByDateTime(
                    addOldTime,
                    addLesson.dateDay,
                    addLesson.dateMonth,
                    addLesson.dateYear,
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
