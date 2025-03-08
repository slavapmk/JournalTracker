package ru.slavapmk.journalTracker.storageModels

import androidx.room.TypeConverter

class LessonTypeConverter {
    @TypeConverter
    fun fromType(value: LessonEntityType?): String? {
        return value?.let {
            when (it) {
                LessonEntityType.LECTURE -> LECTURE
                LessonEntityType.PRACTISE -> PRACTISE
                LessonEntityType.LABORATORY_WORK -> LABORATORY_WORK
                LessonEntityType.TEST -> TEST
                LessonEntityType.DIFFERENTIAL_TEST -> DIFFERENTIAL_TEST
                LessonEntityType.EXAM -> EXAM
            }
        }
    }

    @TypeConverter
    fun toAttendance(raw: String?): LessonEntityType? {
        return raw?.let {
            when (it) {
                LECTURE -> LessonEntityType.LECTURE
                PRACTISE -> LessonEntityType.PRACTISE
                LABORATORY_WORK -> LessonEntityType.LABORATORY_WORK
                TEST -> LessonEntityType.TEST
                DIFFERENTIAL_TEST -> LessonEntityType.DIFFERENTIAL_TEST
                EXAM -> LessonEntityType.EXAM
                else -> throw IllegalStateException()
            }
        }
    }

    companion object {
        private const val LECTURE = "LECTURE"
        private const val PRACTISE = "PRACTISE"
        private const val LABORATORY_WORK = "LABORATORY_WORK"
        private const val TEST = "TEST"
        private const val DIFFERENTIAL_TEST = "DIFFERENTIAL_TEST"
        private const val EXAM = "EXAM"
    }
}