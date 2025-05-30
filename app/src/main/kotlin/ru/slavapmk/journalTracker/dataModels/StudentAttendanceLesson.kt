package ru.slavapmk.journalTracker.dataModels

import ru.slavapmk.journalTracker.R
import ru.slavapmk.journalTracker.storageModels.StudentEntityAttendance

enum class StudentAttendanceLesson(
    val displayNameRes: Int,
    val color: Int?,
    val allVariants: List<String> = listOf()
) {
    NULL(
        R.string.empty,
        null,
        listOf("", " ", "null")
    ),
    VISIT(
        R.string.attendance_visit,
        R.color.export_table_visit,
        listOf("+", ".", "·", "•")
    ),
    NOT_VISIT(
        R.string.attendance_skip,
        R.color.export_table_skip,
        listOf("—", "-", "--", "2")
    ),
    SICK(
        R.string.attendance_sick,
        R.color.export_table_sick,
        listOf("S", "Б")
    ),
    SICK_WITH_CERTIFICATE(
        R.string.attendance_sick_with_certificate,
        R.color.export_table_sick,
        listOf("Б/У", "S/R", "-/Б", "—/Б")
    ),
    RESPECTFUL_PASS(
        R.string.attendance_respectfull_pass,
        R.color.export_table_respect_pass,
        listOf("-/R", "—/R", "R", "-/У", "—/У", "У")
    )
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