package ru.slavapmk.journalTracker.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.slavapmk.journalTracker.dataModels.selectWeek.Semester
import ru.slavapmk.journalTracker.dataModels.selectWeek.Week
import ru.slavapmk.journalTracker.storageModels.Dependencies

class SelectWeekViewModel : ViewModel() {
    var semester: Semester? = null
    val weeks: MutableList<Week> = mutableListOf()

    val semesterMutableLiveData: MutableLiveData<Semester> by lazy {
        MutableLiveData()
    }

    fun loadSemester(semesterId: Int) {
        viewModelScope.launch {
            val semesterEntity = Dependencies.semesterRepository.getSemester(semesterId)
            semesterMutableLiveData.postValue(
                Semester(
                    semesterEntity.startDay,
                    semesterEntity.startMonth,
                    semesterEntity.startYear,
                    semesterEntity.endDay,
                    semesterEntity.endMonth,
                    semesterEntity.endYear,
                )
            )
        }
    }
}