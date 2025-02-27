package ru.slavapmk.journaltracker.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import ru.slavapmk.journaltracker.R
import ru.slavapmk.journaltracker.databinding.ActivityLessonBinding
import ru.slavapmk.journaltracker.ui.MainActivity.Companion.fmanager
import ru.slavapmk.journaltracker.viewmodels.LessonViewModel
import java.util.Calendar

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
            viewModel.index,
            viewModel.name
        )

        val instance = Calendar.getInstance()
        instance.time = viewModel.date

        binding.lessonDate.text = getString(
            R.string.students_lesson_date,
            instance.get(Calendar.DAY_OF_MONTH),
            instance.get(Calendar.MONTH),
            instance.get(Calendar.YEAR)
        )

        binding.lessonTeacher.text = viewModel.teacher

        val lessonId = if (!intent.hasExtra("LESSON_ID")) {
            throw RuntimeException("Lesson id not catch")
        } else {
            intent.getIntExtra("LESSON_ID", -1)
        }

        binding.students.layoutManager = LinearLayoutManager(this)
        binding.students.adapter = LessonStudentsAdapter(
            viewModel.students
        )
    }
}