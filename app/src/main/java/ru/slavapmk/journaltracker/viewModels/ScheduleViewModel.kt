package ru.slavapmk.journaltracker.viewModels

import androidx.lifecycle.ViewModel
import ru.slavapmk.journaltracker.dataModels.schedule.ScheduleListLesson
import java.util.Calendar
import java.util.Date

class ScheduleViewModel : ViewModel() {
    var currentDate: Date = Calendar.Builder().apply {
        set(Calendar.YEAR, 2024)
        set(Calendar.MONTH, 9)
        set(Calendar.DATE, 1)
    }.build().time
    var lessons = mutableListOf(
        ScheduleListLesson(
            id = 432,
            startHour = 11, startMinute = 15,
            endHour = 12, endMinute = 45,
            type = "Л",
            auditory = 501,
            campus = "ОП",
            name = "ВышМат",
            teacher = "Штепина"
        ),
        ScheduleListLesson(
            id = 123,
            startHour = 13, startMinute = 0,
            endHour = 14, endMinute = 30,
            type = "Л",
            auditory = 535,
            campus = "ОП",
            name = "Физика",
            teacher = "Носков"
        ),
    )
}