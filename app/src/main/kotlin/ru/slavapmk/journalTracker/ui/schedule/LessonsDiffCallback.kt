package ru.slavapmk.journalTracker.ui.schedule

import androidx.recyclerview.widget.DiffUtil
import ru.slavapmk.journalTracker.dataModels.schedule.ScheduleListLesson

class LessonsDiffCallback(
    private val oldList: List<ScheduleListLesson>,
    private val newList: List<ScheduleListLesson>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}