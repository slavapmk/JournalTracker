package ru.slavapmk.journalTracker.viewModels

import android.content.SharedPreferences
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.slavapmk.journalTracker.dataModels.lesson.LessonInfo
import ru.slavapmk.journalTracker.dataModels.lesson.LessonStudentListItem
import ru.slavapmk.journalTracker.dataModels.toEdit
import ru.slavapmk.journalTracker.dataModels.toEntity
import ru.slavapmk.journalTracker.storageModels.StorageDependencies
import ru.slavapmk.journalTracker.storageModels.entities.CampusEntity
import ru.slavapmk.journalTracker.storageModels.entities.LessonInfoEntity
import ru.slavapmk.journalTracker.storageModels.entities.SemesterEntity
import ru.slavapmk.journalTracker.storageModels.entities.StudentAttendanceEntity
import ru.slavapmk.journalTracker.storageModels.entities.StudentEntity
import ru.slavapmk.journalTracker.storageModels.entities.TimeEntity
import ru.slavapmk.journalTracker.ui.SharedKeys
import java.time.LocalDate

class LessonViewModel : ViewModel() {
    var sharedPreferences: SharedPreferences? = null

    var info: LessonInfo? = null
    var semester: SemesterEntity? = null
    var allTimes: MutableList<TimeEntity> = mutableListOf()
    var studentAttendances: MutableList<LessonStudentListItem> = mutableListOf()
    var allStudents: MutableList<StudentEntity> = mutableListOf()

    private val campusesLiveData: MutableLiveData<List<CampusEntity>> by lazy { MutableLiveData() }
    private val semestersLiveData: MutableLiveData<List<SemesterEntity>> by lazy { MutableLiveData() }
    private val timesLiveData: MutableLiveData<List<TimeEntity>> by lazy { MutableLiveData() }
    private val studentsLiveData: MutableLiveData<List<StudentEntity>> by lazy { MutableLiveData() }
    private val attendanceLiveData: MutableLiveData<List<StudentAttendanceEntity>> by lazy { MutableLiveData() }
    private val lessonLiveData: MutableLiveData<LessonInfoEntity> by lazy { MutableLiveData() }
    val reloadAttendanceLiveData by lazy { MutableLiveData<List<StudentAttendanceEntity>>() }
    val insertedLiveData by lazy { MutableLiveData<Unit>() }

    val weekTypes: Int by lazy {
        sharedPreferences!!.getInt(SharedKeys.WEEK_TYPES_KEY, 1)
    }

    val lessonInfoLiveData = MediatorLiveData<LessonInfo>().apply {
        var campuses: List<CampusEntity>? = null
        var semesters: List<SemesterEntity>? = null
        var times: List<TimeEntity>? = null
        var students: List<StudentEntity>? = null
        var attendances: List<StudentAttendanceEntity>? = null
        var lesson: LessonInfoEntity? = null
        fun update() {
            if (
                campuses != null &&
                semesters != null &&
                times != null &&
                students != null &&
                attendances != null &&
                lesson != null
            ) {
                val exists = attendances!!.map { it.studentId }
                val toInit = mutableListOf<StudentAttendanceEntity>()
                for (student in students!!) {
                    if (!exists.contains(student.id)) {
                        toInit.add(
                            StudentAttendanceEntity(
                                0,
                                student.id,
                                lesson!!.id,
                                student.default,
                                ""
                            )
                        )
                    }
                }
                fillStudents(toInit)

                val timeIndex = times!!.indexOfFirst { it.id == lesson!!.timeId }
                val time = times!![timeIndex]
                value = LessonInfo(
                    lesson!!.id,
                    timeIndex,
                    lesson!!.name,
                    lesson!!.type.toEdit(),
                    lesson!!.teacher,
                    lesson!!.dateDay,
                    lesson!!.dateMonth,
                    lesson!!.dateYear,
                    time.startHour,
                    time.startMinute,
                    time.endHour,
                    time.endMinute,
                    campuses!!.find { it.id == lesson!!.campusId }!!.codename
                )
                allStudents.apply {
                    clear()
                    addAll(students!!)
                }
                allTimes.apply {
                    clear()
                    addAll(times!!)
                }
                semester = semesters!!.find {
                    val lessonDate = SimpleDate(
                        lesson!!.dateDay, lesson!!.dateMonth, lesson!!.dateYear
                    )
                    SimpleDate(
                        it.startDay, it.startMonth, it.startYear
                    ) <= lessonDate && lessonDate <= SimpleDate(
                        it.endDay, it.endMonth, it.endYear
                    )
                }

                campuses = null
                semesters = null
                times = null
                students = null
                attendances = null
                lesson = null
            }
        }
        addSource(campusesLiveData) { value ->
            campuses = value
            update()
        }
        addSource(semestersLiveData) { value ->
            semesters = value
            update()
        }
        addSource(timesLiveData) { value ->
            times = value
            update()
        }
        addSource(studentsLiveData) { value ->
            students = value
            update()
        }
        addSource(attendanceLiveData) { value ->
            attendances = value
            update()
        }
        addSource(lessonLiveData) { value ->
            lesson = value
            update()
        }
    }

    private fun fillStudents(attendances: List<StudentAttendanceEntity>) {
        viewModelScope.launch {
            if (attendances.isNotEmpty()) {
                StorageDependencies.studentsAttendanceRepository.insertAttendances(
                    attendances
                )
                insertedLiveData.postValue(Unit)
            }
        }
    }

    fun loadAttendance() {
        viewModelScope.launch {
            info?.id?.let {
                reloadAttendanceLiveData.postValue(
                    StorageDependencies.studentsAttendanceRepository.getLessonAttendance(it)
                )
            }
        }
    }

    fun loadData(lessonId: Int) {
        viewModelScope.launch {
            campusesLiveData.postValue(
                StorageDependencies.campusRepository.getCampuses()
            )
        }
        viewModelScope.launch {
            semestersLiveData.postValue(
                StorageDependencies.semesterRepository.getSemesters()
            )
        }
        viewModelScope.launch {
            timesLiveData.postValue(
                StorageDependencies.timeRepository.getTimes()
            )
        }
        viewModelScope.launch {
            studentsLiveData.postValue(
                StorageDependencies.studentRepository.getStudents()
            )
        }
        viewModelScope.launch {
            lessonLiveData.postValue(
                StorageDependencies.lessonInfoRepository.getLessonById(lessonId)
            )
        }
        viewModelScope.launch {
            attendanceLiveData.postValue(
                StorageDependencies.studentsAttendanceRepository.getLessonAttendance(lessonId)
            )
        }
    }

    fun deleteLessons(updateNext: Boolean) {
        val nowDate = SimpleDate(info!!.dateDay, info!!.dateMonth, info!!.dateYear)
        val dates = if (updateNext) {
            genDates(
                nowDate,
                SimpleDate(semester!!.endDay, semester!!.endMonth, semester!!.endYear),
                weekTypes
            )
        } else {
            mutableListOf(nowDate)
        }
        viewModelScope.launch {
            for (date in dates) {
                StorageDependencies.lessonInfoRepository.deleteLessonsByDateTimeName(
                    date.day,
                    date.month,
                    date.year,
                    allTimes[info!!.index].id,
                    info!!.name
                )
            }
        }
    }

    private fun genDates(
        fromDate: SimpleDate,
        toDate: SimpleDate,
        weekOffset: Int
    ): List<SimpleDate> {
        val dates = mutableListOf<SimpleDate>()

        var current = LocalDate.of(fromDate.year, fromDate.month, fromDate.day)
        val end = LocalDate.of(toDate.year, toDate.month, toDate.day)

        while (!current.isAfter(end)) {
            dates.add(SimpleDate(current.dayOfMonth, current.monthValue, current.year))
            current = current.plusWeeks(weekOffset.toLong())
        }

        return dates
    }

    fun updateStudent(updateStudent: LessonStudentListItem) {
        val entity = StudentAttendanceEntity(
            updateStudent.id,
            updateStudent.studentId,
            info!!.id,
            updateStudent.attendance.toEntity(),
            skipDescription = updateStudent.description
        )
        viewModelScope.launch {
            StorageDependencies.studentsAttendanceRepository.updateAttendance(entity)
        }
    }
}
