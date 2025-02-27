package ru.slavapmk.journalTracker.viewModels

import androidx.lifecycle.ViewModel
import ru.slavapmk.journalTracker.dataModels.campuses.Campus

class CampusEditViewModel : ViewModel() {
    var codename: String = ""
    var name: String = ""
    val campuses: MutableList<Campus> = mutableListOf()
}