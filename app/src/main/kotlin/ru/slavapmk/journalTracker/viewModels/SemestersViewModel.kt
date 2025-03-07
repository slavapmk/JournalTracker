package ru.slavapmk.journalTracker.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.slavapmk.journalTracker.dataModels.semesters.Semester
import ru.slavapmk.journalTracker.storageModels.StorageDependencies
import ru.slavapmk.journalTracker.storageModels.entities.SemesterEntity

class SemestersViewModel : ViewModel() {
    val semesters: MutableList<Semester> = mutableListOf()

    val semestersLiveData: MutableLiveData<List<Semester>> by lazy {
        MutableLiveData()
    }

    val semesterUpdateLiveData: MutableLiveData<Pair<Semester, Semester>> by lazy {
        MutableLiveData()
    }

    val endDeleteLiveData: MutableLiveData<Unit> by lazy {
        MutableLiveData()
    }

    fun loadSemesters() {
        viewModelScope.launch {
            val semesters = StorageDependencies.semesterRepository.getSemesters().map {
                Semester(
                    it.id,
                    it.startDay,
                    it.startMonth,
                    it.startYear,
                    it.endDay,
                    it.endMonth,
                    it.endYear
                )
            }
            semestersLiveData.postValue(semesters)
        }
    }

    fun addSemester(insert: Semester): Int {
        semesters.add(insert)
        semesters.sortWith(
            compareBy(
                Semester::startYear, Semester::startMonth, Semester::startDay
            )
        )
        viewModelScope.launch {
            val id = StorageDependencies.semesterRepository.insertSemester(
                SemesterEntity(
                    0,
                    insert.startDay,
                    insert.startMonth,
                    insert.startYear,
                    insert.endDay,
                    insert.endMonth,
                    insert.endYear
                )
            )
            semesterUpdateLiveData.postValue(
                Pair(
                    insert,
                    insert.copy(id = id)
                )
            )
        }
        return semesters.indexOf(insert)
    }

    fun deleteSemester(semester: Semester) {
        viewModelScope.launch {
            StorageDependencies.semesterRepository.deleteSemester(
                semester.id!!
            )
            endDeleteLiveData.postValue(Unit)
        }
    }
}