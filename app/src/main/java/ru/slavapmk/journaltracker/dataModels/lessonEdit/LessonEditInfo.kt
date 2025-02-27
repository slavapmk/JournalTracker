package ru.slavapmk.journaltracker.dataModels.lessonEdit

data class LessonEditInfo(
    val id: Int,
    var index: Int,
    var name: String,
    var type: String,
    var teacher: String,
    var cabinet: Int,
    var campus: String,
)
