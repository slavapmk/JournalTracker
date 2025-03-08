package ru.slavapmk.journalTracker.dataModels

import ru.slavapmk.journalTracker.R
import ru.slavapmk.journalTracker.storageModels.LessonType

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

fun LessonType.toEdit(): LessonTypeEdit = when (this) {
    LessonType.LECTURE -> LessonTypeEdit.LECTURE
    LessonType.PRACTISE -> LessonTypeEdit.PRACTISE
    LessonType.LABORATORY_WORK -> LessonTypeEdit.LABORATORY_WORK
    LessonType.TEST -> LessonTypeEdit.TEST
    LessonType.DIFFERENTIAL_TEST -> LessonTypeEdit.DIFFERENTIAL_TEST
    LessonType.EXAM -> LessonTypeEdit.EXAM
}


fun LessonTypeEdit.toEntity(): LessonType = when (this) {
    LessonTypeEdit.LECTURE -> LessonType.LECTURE
    LessonTypeEdit.PRACTISE -> LessonType.PRACTISE
    LessonTypeEdit.LABORATORY_WORK -> LessonType.LABORATORY_WORK
    LessonTypeEdit.TEST -> LessonType.TEST
    LessonTypeEdit.DIFFERENTIAL_TEST -> LessonType.DIFFERENTIAL_TEST
    LessonTypeEdit.EXAM -> LessonType.EXAM
}
