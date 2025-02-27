package ru.slavapmk.journaltracker.ui.lesson

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import ru.slavapmk.journaltracker.R
import ru.slavapmk.journaltracker.databinding.ActivityLessonBinding
import ru.slavapmk.journaltracker.ui.EditLessonActivity
import ru.slavapmk.journaltracker.ui.MainActivity.Companion.fmanager
import ru.slavapmk.journaltracker.viewModels.LessonViewModel

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

        binding.lessonName.text = getString(
            R.string.students_lesson_name,
            viewModel.info.index + 1,
            viewModel.info.name,
            viewModel.info.type
        )

        binding.lessonDate.text = getString(
            R.string.students_lesson_date,
            viewModel.info.dateDay,
            viewModel.info.dateMonth,
            viewModel.info.dateYear
        )

        binding.lessonTeacher.text = viewModel.info.teacher

        val lessonId = if (!intent.hasExtra("LESSON_ID")) {
            throw RuntimeException("Lesson id not catch")
        } else {
            intent.getIntExtra("LESSON_ID", -1)
        }

        binding.lessonTimes.text = getString(
            R.string.item_lesson_times,
            viewModel.info.startHour, viewModel.info.startMinute,
            viewModel.info.endHour, viewModel.info.endMinute
        )
        binding.students.layoutManager = LinearLayoutManager(this)
        binding.students.adapter = LessonStudentsAdapter(
            viewModel.info.students
        )
        binding.editButton.setOnClickListener {
            startActivity(
                Intent(this, EditLessonActivity::class.java).apply {
                    putExtra("LESSON_ID", lessonId)
                }
            )
        }
        binding.deleteButton.setOnClickListener {
            TODO()
        }
    }
}