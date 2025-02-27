package ru.slavapmk.journaltracker.ui.lessonEdit

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.slavapmk.journaltracker.dataModels.lessonEdit.LessonEditInfo
import ru.slavapmk.journaltracker.databinding.ActivityLessonEditBinding
import ru.slavapmk.journaltracker.viewModels.EditLessonViewModel

class LessonEditActivity : AppCompatActivity() {
    companion object {
        const val LESSON_ID = "LESSON_ID"
    }

    private lateinit var binding: ActivityLessonEditBinding
    val viewModel by viewModels<EditLessonViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLessonEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadData()

        binding.nameInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.info.name = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        binding.teacherInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.info.teacher = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        binding.cabinetInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.info.cabinet = s.toString().toInt()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        binding.campusInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.info.campus = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        binding.orderInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.info.index = s.toString().toInt() - 1
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        binding.typeInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.info.type = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        binding.addButton.setOnClickListener {
            finish()
        }

        binding.nameInput.setText(viewModel.info.name)
        binding.teacherInput.setText(viewModel.info.teacher)
        binding.cabinetInput.setText(viewModel.info.cabinet)
        binding.campusInput.setText(viewModel.info.campus)
        binding.orderInput.setText(viewModel.info.index + 1)
        binding.typeInput.setText(viewModel.info.type)
    }

    private fun loadData() {
        viewModel.info = if (intent.hasExtra(LESSON_ID)) {
            LessonEditInfo(
                id = intent.getIntExtra(LESSON_ID, -1),
                index = 0,
                name = "Физика",
                type = "Л",
                teacher = "Носков",
                cabinet = 535,
                campus = "ОП"
            )
        } else {
            LessonEditInfo(
                id = 123,
                index = 0,
                name = "Физика",
                type = "Л",
                teacher = "Носков",
                cabinet = 535,
                campus = "ОП"
            )
        }
    }
}