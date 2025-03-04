package ru.slavapmk.journalTracker.ui.lesson

import ru.slavapmk.journalTracker.R

enum class StudentAttendance(
    val displayNameRes: Int
) {
    VISIT(R.string.attendance_visit),
    NOT_VISIT(R.string.attendance_skip),
    SICK(R.string.attendance_sick),
    SICK_WITH_CERTIFICATE(R.string.attendance_sick_with_certificate),
    RESPECTFUL_PASS(R.string.attendance_respectfull_pass)
}