package ru.slavapmk.journalTracker.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.slavapmk.journalTracker.dataModels.semesters.Semester
import ru.slavapmk.journalTracker.storageModels.Dependencies

class SemestersViewModel : ViewModel() {
    var startDay: Int? = null
    var startMonth: Int? = null
    var startYear: Int? = null
    var endDay: Int? = null
    var endMonth: Int? = null
    var endYear: Int? = null
    val semesters: MutableList<Semester> = mutableListOf()

    val semestersLiveData: MutableLiveData<List<Semester>> by lazy {
        MutableLiveData()
    }

    fun loadStudents() {
        viewModelScope.launch {
            val semesters = Dependencies.semesterRepository.getSemesters().map {
                Semester(
                    it.id,
                    it.startMonth,
                    it.startDay,
                    it.startYear,
                    it.endMonth,
                    it.endDay,
                    it.endYear
                )
            }
            semestersLiveData.postValue(semesters)
        }
    }
}