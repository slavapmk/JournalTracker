package ru.slavapmk.journalTracker.dataModels.lessonEdit

import ru.slavapmk.journalTracker.dataModels.LessonTypeEdit

data class LessonEditInfo(
    val id: Int,
    var index: Int?,
    var name: String?,
    var typeName: LessonTypeEdit?,
    var teacher: String?,
    var cabinet: Int?,
    var campusId: Int?,
)
