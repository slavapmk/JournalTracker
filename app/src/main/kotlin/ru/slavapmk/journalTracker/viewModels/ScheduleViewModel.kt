package ru.slavapmk.journalTracker.viewModels

import android.content.SharedPreferences
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.slavapmk.journalTracker.dataModels.schedule.ScheduleListLesson
import ru.slavapmk.journalTracker.dataModels.selectWeek.Week
import ru.slavapmk.journalTracker.dataModels.settings.WeeksFormats
import ru.slavapmk.journalTracker.storageModels.Dependencies
import ru.slavapmk.journalTracker.storageModels.entities.CampusEntity
import ru.slavapmk.journalTracker.storageModels.entities.LessonInfoEntity
import ru.slavapmk.journalTracker.storageModels.entities.SemesterEntity
import ru.slavapmk.journalTracker.storageModels.entities.TimeEntity
import ru.slavapmk.journalTracker.ui.SharedKeys
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale


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

data class ItemDate(
    val day: Int,
    val month: Int,
    val year: Int,
    val dayOfWeek: Int,
    val contains: Boolean = true
)

operator fun SimpleDate.compareTo(other: SimpleDate): Int {
    return compareValuesBy(this, other, SimpleDate::year, SimpleDate::month, SimpleDate::day)
}

class ScheduleViewModel : ViewModel() {
    var sharedPreferences: SharedPreferences? = null

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
                semesters = null
                times = null
                campuses = null
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
            val date = nowDate()
            setDate(date)
            return date
        } else {
            return selectedDate!!
        }
    }

    fun nowDate(): SimpleDate {
        val calendar: Calendar = GregorianCalendar.getInstance().apply {
            time = Date()
        }
        val date = SimpleDate(
            calendar[Calendar.DAY_OF_MONTH],
            calendar[Calendar.MONTH] + 1,
            calendar[Calendar.YEAR]
        )
        return date
    }

    fun setDate(date: SimpleDate?) {
        if (date == null) {
            sharedPreferences?.edit()?.apply {
                remove(SharedKeys.SELECTED_DAY)
                remove(SharedKeys.SELECTED_MONTH)
                remove(SharedKeys.SELECTED_YEAR)
                apply()
            }
        } else {
            sharedPreferences?.edit()?.apply {
                putInt(SharedKeys.SELECTED_DAY, date.day)
                putInt(SharedKeys.SELECTED_MONTH, date.month)
                putInt(SharedKeys.SELECTED_YEAR, date.year)
                apply()
            }
        }
        selectedDate = date
    }

    fun loadDate() {
        selectedDate = if (
            sharedPreferences != null &&
            sharedPreferences!!.contains(SharedKeys.SELECTED_DAY) &&
            sharedPreferences!!.contains(SharedKeys.SELECTED_MONTH) &&
            sharedPreferences!!.contains(SharedKeys.SELECTED_YEAR)
        ) {
            SimpleDate(
                sharedPreferences!!.getInt(SharedKeys.SELECTED_DAY, -1),
                sharedPreferences!!.getInt(SharedKeys.SELECTED_MONTH, -1),
                sharedPreferences!!.getInt(SharedKeys.SELECTED_YEAR, -1),
            )
        } else {
            null
        }
    }

    val weekFormat: WeeksFormats
        get() = when (sharedPreferences!!.getString(SharedKeys.WEEK_FORMAT_KEY, SharedKeys.EVEN_UNEVEN_VALUE_KEY)) {
            SharedKeys.EVEN_UNEVEN_VALUE_KEY -> WeeksFormats.EVEN_UNEVEN
            SharedKeys.UP_DOWN_VALUE_KEY -> WeeksFormats.UP_DOWN
            SharedKeys.DOWN_UP_VALUE_KEY -> WeeksFormats.DOWN_UP
            else -> throw IllegalStateException()
        }

    val weekTypes: Int
        get() = sharedPreferences!!.getInt(SharedKeys.WEEK_TYPES_KEY, 1)

    fun parseWeek(): List<ItemDate>? = week?.let { weekToList(it) }

    private fun weekToList(week: Week): List<ItemDate> {
        val startDate = LocalDate.of(week.startYear, week.startMonth, week.startDay)
        val endDate = LocalDate.of(week.endYear, week.endMonth, week.endDay)
        val weekStart = startDate.with(WeekFields.of(Locale.getDefault()).firstDayOfWeek)

        return (0..6).map { offset ->
            val currentDate = weekStart.plusDays(offset.toLong())
            val contains = !currentDate.isBefore(startDate) && !currentDate.isAfter(endDate)
            ItemDate(
                day = currentDate.dayOfMonth,
                month = (currentDate.monthValue - 1) % 12,
                year = currentDate.year,
                dayOfWeek = (currentDate.dayOfWeek.value - 1) % 7,
                contains = contains
            )
        }
    }

    companion object {
    }
}