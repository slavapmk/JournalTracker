package ru.slavapmk.journalTracker.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.slavapmk.journalTracker.dataModels.campuses.Campus
import ru.slavapmk.journalTracker.storageModels.Dependencies
import ru.slavapmk.journalTracker.storageModels.entities.CampusEntity

class CampusEditViewModel : ViewModel() {
    var codename: String = ""
    var name: String = ""
    val campuses: MutableList<Campus> = mutableListOf()

    val campusesLiveData by lazy { MutableLiveData<List<Campus>>() }
    val campusUpdateLiveData by lazy { MutableLiveData<Pair<Campus, Campus>>() }

    fun loadCampuses() {
        viewModelScope.launch {
            campusesLiveData.postValue(
                Dependencies.campusRepository.getCampuses().map {
                    Campus(it.id, it.codename, it.name)
                }
            )
        }
    }

    fun addCampus(campus: Campus) {
        campuses.add(campus)
        viewModelScope.launch {
            val insertId = Dependencies.campusRepository.insertCampus(
                CampusEntity(
                    0,
                    campus.codename,
                    campus.name
                )
            )
            campusUpdateLiveData.postValue(
                campus to campus.copy(id = insertId)
            )
        }
    }

    fun deleteCampus(delete: Campus) {
        campuses.remove(delete)
        viewModelScope.launch {
            Dependencies.campusRepository.deleteCampus(
                delete.id
            )
        }
    }
}