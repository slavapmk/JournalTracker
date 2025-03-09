package ru.slavapmk.journalTracker.dataModels

import ru.slavapmk.journalTracker.R
import ru.slavapmk.journalTracker.storageModels.StudentEntityAttendance

enum class StudentAttendanceLesson(
    val displayNameRes: Int
) {
    NULL(R.string.empty),
    VISIT(R.string.attendance_visit),
    NOT_VISIT(R.string.attendance_skip),
    SICK(R.string.attendance_sick),
    SICK_WITH_CERTIFICATE(R.string.attendance_sick_with_certificate),
    RESPECTFUL_PASS(R.string.attendance_respectfull_pass)
}

fun StudentEntityAttendance?.toEdit(): StudentAttendanceLesson? = when (this) {
    StudentEntityAttendance.VISIT -> StudentAttendanceLesson.VISIT
    StudentEntityAttendance.NOT_VISIT -> StudentAttendanceLesson.NOT_VISIT
    StudentEntityAttendance.SICK -> StudentAttendanceLesson.SICK
    StudentEntityAttendance.SICK_WITH_CERTIFICATE -> StudentAttendanceLesson.SICK_WITH_CERTIFICATE
    StudentEntityAttendance.RESPECTFUL_PASS -> StudentAttendanceLesson.RESPECTFUL_PASS
    null -> null
}

fun StudentAttendanceLesson?.toEntity(): StudentEntityAttendance? = when (this) {
    StudentAttendanceLesson.VISIT -> StudentEntityAttendance.VISIT
    StudentAttendanceLesson.NOT_VISIT -> StudentEntityAttendance.NOT_VISIT
    StudentAttendanceLesson.SICK -> StudentEntityAttendance.SICK
    StudentAttendanceLesson.SICK_WITH_CERTIFICATE -> StudentEntityAttendance.SICK_WITH_CERTIFICATE
    StudentAttendanceLesson.RESPECTFUL_PASS -> StudentEntityAttendance.RESPECTFUL_PASS
    StudentAttendanceLesson.NULL -> null
    null -> null
}