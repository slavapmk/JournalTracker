package ru.slavapmk.journalTracker.dataModels

import ru.slavapmk.journalTracker.R
import ru.slavapmk.journalTracker.storageModels.LessonEntityType

enum class LessonTypeEdit(
    val shortNameRes: Int,
    val nameRes: Int,
    val colorState: Int
) {
    LECTURE(
        R.string.type_lecture_short,
        R.string.type_lecture,
        R.color.color_selector_lecture
    ),
    PRACTISE(
        R.string.type_practise_short,
        R.string.type_practise,
        R.color.color_selector_practise
    ),
    LABORATORY_WORK(
        R.string.type_laboratory_short,
        R.string.type_laboratory,
        R.color.color_selector_laboratory_work
    ),
    TEST(
        R.string.type_test_short,
        R.string.type_test,
        R.color.color_selector_exam
    ),
    DIFFERENTIAL_TEST(
        R.string.type_test_differential_short,
        R.string.type_test_differential,
        R.color.color_selector_exam
    ),
    EXAM(
        R.string.type_exam_short,
        R.string.type_exam,
        R.color.color_selector_exam
    )
}

fun LessonEntityType.toEdit(): LessonTypeEdit = when (this) {
    LessonEntityType.LECTURE -> LessonTypeEdit.LECTURE
    LessonEntityType.PRACTISE -> LessonTypeEdit.PRACTISE
    LessonEntityType.LABORATORY_WORK -> LessonTypeEdit.LABORATORY_WORK
    LessonEntityType.TEST -> LessonTypeEdit.TEST
    LessonEntityType.DIFFERENTIAL_TEST -> LessonTypeEdit.DIFFERENTIAL_TEST
    LessonEntityType.EXAM -> LessonTypeEdit.EXAM
}


fun LessonTypeEdit.toEntity(): LessonEntityType = when (this) {
    LessonTypeEdit.LECTURE -> LessonEntityType.LECTURE
    LessonTypeEdit.PRACTISE -> LessonEntityType.PRACTISE
    LessonTypeEdit.LABORATORY_WORK -> LessonEntityType.LABORATORY_WORK
    LessonTypeEdit.TEST -> LessonEntityType.TEST
    LessonTypeEdit.DIFFERENTIAL_TEST -> LessonEntityType.DIFFERENTIAL_TEST
    LessonTypeEdit.EXAM -> LessonEntityType.EXAM
}
