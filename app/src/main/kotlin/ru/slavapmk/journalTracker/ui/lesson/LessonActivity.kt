package ru.slavapmk.journalTracker.ui.lesson

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import ru.slavapmk.journalTracker.R
import ru.slavapmk.journalTracker.dataModels.lesson.LessonStudentListItem
import ru.slavapmk.journalTracker.databinding.ActivityLessonBinding
import ru.slavapmk.journalTracker.storageModels.StudentAttendance
import ru.slavapmk.journalTracker.ui.lessonEdit.LessonEditActivity
import ru.slavapmk.journalTracker.ui.MainActivity.Companion.fmanager
import ru.slavapmk.journalTracker.ui.SharedKeys
import ru.slavapmk.journalTracker.ui.lessonEdit.LessonUpdateDialog
import ru.slavapmk.journalTracker.viewModels.LessonViewModel

class LessonActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLessonBinding
    val viewModel by viewModels<LessonViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        fmanager = supportFragmentManager
        binding = ActivityLessonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val lessonId = if (!intent.hasExtra(SharedKeys.SELECTED_LESSON)) {
            throw RuntimeException("Lesson id not catch")
        } else {
            intent.getIntExtra(SharedKeys.SELECTED_LESSON, -1)
        }

        binding.editButton.setOnClickListener {
            startActivity(
                Intent(this, LessonEditActivity::class.java).apply {
                    putExtra(SharedKeys.SELECTED_LESSON, lessonId)
                }
            )
        }
        binding.deleteButton.setOnClickListener {
            LessonUpdateDialog(
                {
                    deleteLessons(false)
                }, {
                    deleteLessons(true)
                }
            ).show(
                supportFragmentManager.beginTransaction(),
                "delete_lessons_dialog"
            )
        }

        loadData(lessonId)
    }

    private fun loadData(
        lessonId: Int
    ) {
        setLoading(true)
        viewModel.loadData(lessonId)
        viewModel.lessonInfoLiveData.observe(this) { lessonInfo ->
            viewModel.info = lessonInfo
            viewModel.loadFilledStudents()
        }
        viewModel.fillAttendanceLiveData.observe(this) { new ->
            viewModel.students?.apply {
                clear()
                addAll(
                    new.map {
                        LessonStudentListItem(
                            it.id,
                            it.studentId,
                            viewModel.students!!.find { student -> student.id == it.studentId }!!.name,
                            when (it.attendance) {
                                StudentAttendance.VISIT -> StudentAttendanceLesson.VISIT
                                StudentAttendance.NOT_VISIT -> StudentAttendanceLesson.NOT_VISIT
                                StudentAttendance.SICK -> StudentAttendanceLesson.SICK
                                StudentAttendance.SICK_WITH_CERTIFICATE -> StudentAttendanceLesson.SICK_WITH_CERTIFICATE
                                StudentAttendance.RESPECTFUL_PASS -> StudentAttendanceLesson.RESPECTFUL_PASS
                            },
                            it.skipDescription
                        )
                    }
                )
            }
            setLoading(false)
            injectData()
        }
    }

    private fun injectData() {
        binding.lessonName.text = getString(
            R.string.students_lesson_name,
            (viewModel.info?.index ?: 0) + 1,
            viewModel.info?.name ?: "",
            viewModel.info?.type ?: ""
        )

        binding.lessonDate.text = getString(
            R.string.students_lesson_date,
            viewModel.info?.dateDay ?: 1,
            viewModel.info?.dateMonth ?: 1,
            viewModel.info?.dateYear ?: 1
        )

        binding.lessonTeacher.text = viewModel.info?.teacher ?: ""

        binding.lessonTimes.text = getString(
            R.string.item_lesson_times,
            viewModel.info?.startHour ?: 0,
            viewModel.info?.startMinute ?: 0,
            viewModel.info?.endHour ?: 0,
            viewModel.info?.endMinute ?: 0
        )
        binding.students.layoutManager = LinearLayoutManager(this)
        binding.students.adapter = viewModel.students?.let {
            LessonStudentsAdapter(it) { updateStudent ->
                viewModel.updateStudent(updateStudent)
            }
        }
    }

    private fun deleteLessons(updateNext: Boolean) {
        viewModel.deleteLessons(updateNext)
        finish()
    }

    private fun setLoading(loading: Boolean) {
        binding.loadingStatus.isVisible = loading
    }
}