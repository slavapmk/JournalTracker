package ru.slavapmk.journalTracker.viewModels

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.slavapmk.journalTracker.dataModels.lesson.LessonInfo
import ru.slavapmk.journalTracker.dataModels.lesson.LessonStudentListItem
import ru.slavapmk.journalTracker.storageModels.StorageDependencies
import ru.slavapmk.journalTracker.storageModels.StudentAttendance
import ru.slavapmk.journalTracker.storageModels.entities.CampusEntity
import ru.slavapmk.journalTracker.storageModels.entities.LessonInfoEntity
import ru.slavapmk.journalTracker.storageModels.entities.SemesterEntity
import ru.slavapmk.journalTracker.storageModels.entities.StudentAttendanceEntity
import ru.slavapmk.journalTracker.storageModels.entities.StudentEntity
import ru.slavapmk.journalTracker.storageModels.entities.TimeEntity
import ru.slavapmk.journalTracker.ui.lesson.StudentAttendanceLesson

class LessonViewModel : ViewModel() {
    var info: LessonInfo? = null

    private val campusesLiveData: MutableLiveData<List<CampusEntity>> by lazy { MutableLiveData() }
    private val semestersLiveData: MutableLiveData<List<SemesterEntity>> by lazy { MutableLiveData() }
    private val timesLiveData: MutableLiveData<List<TimeEntity>> by lazy { MutableLiveData() }
    private val studentsLiveData: MutableLiveData<List<StudentEntity>> by lazy { MutableLiveData() }
    private val attendanceLiveData: MutableLiveData<List<StudentAttendanceEntity>> by lazy { MutableLiveData() }
    private val lessonLiveData: MutableLiveData<LessonInfoEntity> by lazy { MutableLiveData() }

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
                                student.default ?: StudentAttendance.NOT_VISIT,
                                ""
                            )
                        )
                    }
                }
                if (toInit.isNotEmpty()) {
                    fillStudents(toInit)
                }
                val fullAttendance = attendances!! + toInit

                val timeIndex = times!!.indexOfFirst { it.id == lesson!!.timeId }
                val time = times!![timeIndex]
                value = LessonInfo(
                    lesson!!.id,
                    timeIndex,
                    lesson!!.name,
                    lesson!!.type,
                    lesson!!.teacher,
                    lesson!!.dateDay,
                    lesson!!.dateMonth,
                    lesson!!.dateYear,
                    time.startHour,
                    time.startMinute,
                    time.endHour,
                    time.endMinute,
                    campuses!!.find { it.id == lesson!!.campusId }!!.codename,
                    fullAttendance.map {
                        LessonStudentListItem(
                            it.studentId,
                            students!!.find { student -> student.id == it.studentId }!!.name,
                            when (it.attendance) {
                                StudentAttendance.VISIT -> StudentAttendanceLesson.VISIT
                                StudentAttendance.NOT_VISIT -> StudentAttendanceLesson.NOT_VISIT
                                StudentAttendance.SICK -> StudentAttendanceLesson.SICK
                                StudentAttendance.SICK_WITH_CERTIFICATE -> StudentAttendanceLesson.SICK_WITH_CERTIFICATE
                                StudentAttendance.RESPECTFUL_PASS -> StudentAttendanceLesson.RESPECTFUL_PASS
                            },
                            it.skipDescription
                        )
                    }
                )

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

    fun fillStudents(attendances: List<StudentAttendanceEntity>) {
        viewModelScope.launch {
            StorageDependencies.studentsAttendanceRepository.insertAttendances(
                attendances
            )
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
        TODO("Not yet implemented")
    }
}
