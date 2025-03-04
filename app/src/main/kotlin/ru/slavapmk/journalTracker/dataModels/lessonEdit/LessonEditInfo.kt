package ru.slavapmk.journalTracker.dataModels.lessonEdit

data class LessonEditInfo(
    val id: Int,
    var index: Int?,
    var name: String?,
    var typeName: String?,
    var teacher: String?,
    var cabinet: Int?,
    var campusId: Int?,
)
