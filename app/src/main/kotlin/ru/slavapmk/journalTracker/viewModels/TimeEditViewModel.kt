package ru.slavapmk.journalTracker.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.slavapmk.journalTracker.dataModels.timeEdit.TimeEditItem
import ru.slavapmk.journalTracker.storageModels.StorageDependencies
import ru.slavapmk.journalTracker.storageModels.entities.TimeEntity

class TimeEditViewModel : ViewModel() {
    var startHours: Int = -1
    var startMinutes: Int = -1
    var endHours: Int = -1
    var endMinutes: Int = -1
    var timeList: MutableList<TimeEditItem> = mutableListOf()

    val timeLiveData: MutableLiveData<List<TimeEditItem>> by lazy { MutableLiveData() }

    fun addTime(item: TimeEditItem): Int {
        timeList.add(item)
        timeList.sortBy {
            it.startHours * 60 + it.startMinutes
        }
        val insertIndex = timeList.indexOf(item)

        viewModelScope.launch {
            StorageDependencies.timeRepository.insertTime(
                TimeEntity(
                    0,
                    startHours,
                    startMinutes,
                    endHours,
                    endMinutes
                )
            )
        }

        return insertIndex
    }

    fun delTime(item: TimeEditItem) {
        timeList.remove(item)
        viewModelScope.launch {
            StorageDependencies.timeRepository.deleteTime(
                item.startHours,
                item.startMinutes,
                item.endHours,
                item.endMinutes
            )
        }
    }

    fun loadTimes() {
        viewModelScope.launch {
            timeLiveData.postValue(
                StorageDependencies.timeRepository.getTimes().map {
                    TimeEditItem(
                        it.startHour,
                        it.startMinute,
                        it.endHour,
                        it.endMinute
                    )
                }
            )
        }
    }
}