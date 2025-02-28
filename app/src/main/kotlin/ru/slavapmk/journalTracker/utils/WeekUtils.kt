package ru.slavapmk.journalTracker.utils

import ru.slavapmk.journalTracker.dataModels.selectWeek.Semester
import ru.slavapmk.journalTracker.dataModels.selectWeek.Week
import java.time.DayOfWeek
import java.time.LocalDate

fun generateWeeks(semester: Semester): List<Week> {
    val startDate = LocalDate.of(semester.startYear, semester.startMonth, semester.startDay)
    val endDate = LocalDate.of(semester.endYear, semester.endMonth, semester.endDay)

    val weeks = mutableListOf<Week>()
    var currentStart = startDate

    val firstWeekEnd = currentStart.with(DayOfWeek.SUNDAY).coerceAtMost(endDate)
    weeks.add(
        Week(
            startDay = currentStart.dayOfMonth,
            startMonth = currentStart.monthValue,
            startYear = currentStart.year,
            endDay = firstWeekEnd.dayOfMonth,
            endMonth = firstWeekEnd.monthValue,
            endYear = firstWeekEnd.year
        )
    )

    currentStart = firstWeekEnd.plusDays(1)

    while (currentStart.isBefore(endDate) || currentStart.isEqual(endDate)) {
        val currentEnd = currentStart.plusDays(6).coerceAtMost(endDate)

        weeks.add(
            Week(
                startDay = currentStart.dayOfMonth,
                startMonth = currentStart.monthValue,
                startYear = currentStart.year,
                endDay = currentEnd.dayOfMonth,
                endMonth = currentEnd.monthValue,
                endYear = currentEnd.year
            )
        )

        currentStart = currentEnd.plusDays(1)
    }

    return weeks
}