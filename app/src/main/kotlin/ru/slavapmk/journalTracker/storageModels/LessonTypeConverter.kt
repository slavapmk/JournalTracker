package ru.slavapmk.journalTracker.storageModels

import androidx.room.TypeConverter

class LessonTypeConverter {
    @TypeConverter
    fun fromType(value: LessonType?): String? {
        return value?.let {
            when (it) {
                LessonType.LECTURE -> LECTURE
                LessonType.PRACTISE -> PRACTISE
                LessonType.LABORATORY_WORK -> LABORATORY_WORK
                LessonType.TEST -> TEST
                LessonType.DIFFERENTIAL_TEST -> DIFFERENTIAL_TEST
                LessonType.EXAM -> EXAM
            }
        }
    }

    @TypeConverter
    fun toAttendance(raw: String?): LessonType? {
        return raw?.let {
            when (it) {
                LECTURE -> LessonType.LECTURE
                PRACTISE -> LessonType.PRACTISE
                LABORATORY_WORK -> LessonType.LABORATORY_WORK
                TEST -> LessonType.TEST
                DIFFERENTIAL_TEST -> LessonType.DIFFERENTIAL_TEST
                EXAM -> LessonType.EXAM
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