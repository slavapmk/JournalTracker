package ru.slavapmk.journalTracker.dataModels.selectWeek

data class Week(
    val startDay: Int,
    val startMonth: Int,
    val startYear: Int,
    val endDay: Int,
    val endMonth: Int,
    val endYear: Int,
)
