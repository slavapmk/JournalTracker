package ru.slavapmk.journalTracker.ui.lessonEdit

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.slavapmk.journalTracker.dataModels.lessonEdit.LessonEditInfo
import ru.slavapmk.journalTracker.databinding.ActivityLessonEditBinding
import ru.slavapmk.journalTracker.ui.SharedKeys
import ru.slavapmk.journalTracker.viewModels.EditLessonViewModel
import ru.slavapmk.journalTracker.viewModels.SimpleDate

class LessonEditActivity : AppCompatActivity() {
    companion object {
        const val LESSON_ID = "LESSON_ID"
    }

    private lateinit var binding: ActivityLessonEditBinding
    val viewModel by viewModels<EditLessonViewModel>()

    private val shared: SharedPreferences by lazy {
        getSharedPreferences(
            SharedKeys.SHARED_APP_ID, Context.MODE_PRIVATE
        )
    }

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
                viewModel.info.campusName = s.toString()
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
                viewModel.info.typeName = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        binding.addButton.setOnClickListener {
            LessonUpdateDialog(
                {
                    Toast.makeText(this, "gerwdsqw", Toast.LENGTH_SHORT).show()
                }, {
                    Toast.makeText(this, "njvekfdsnkl", Toast.LENGTH_SHORT).show()
                }
            ).show(supportFragmentManager.beginTransaction(), "update_lessons_dialog")
//            finish()
        }

        init()
    }

    private fun init() {
        viewModel.loadLiveData.observe(this) { loadBundle ->
            viewModel.times.apply {
                clear()
                addAll(loadBundle.times)
            }
            val semester = shared.getInt(SharedKeys.SEMESTER_ID, -1)
            viewModel.semester = loadBundle.semesters.find {
                it.id == semester
            } ?: loadBundle.semesters.last()
            viewModel.campuses.apply {
                clear()
                addAll(loadBundle.campuses)
            }
            viewModel.date = SimpleDate(
                shared.getInt(SharedKeys.SELECTED_DAY, 1),
                shared.getInt(SharedKeys.SELECTED_MONTH, 9),
                shared.getInt(SharedKeys.SELECTED_YEAR, 2024)
            )
            initLesson()
        }
        viewModel.lessonLiveData.observe(this) { lesson ->
            setInfo(lesson.let { entity ->
                LessonEditInfo(
                    entity.id,
                    viewModel.times.indexOfFirst { it.id == entity.timeId },
                    entity.name,
                    entity.type,
                    entity.teacher,
                    entity.cabinet,
                    viewModel.campuses.find { it.id == entity.campusId }!!.name
                )
            })
            setLoading(false)
        }
    }

    private fun initLesson() {
        if (intent.hasExtra(LESSON_ID)) {
            val id = intent.getIntExtra(LESSON_ID, -1)
            viewModel.loadLesson(id)
        } else {
            setLoading(false)
            setInfo(
                LessonEditInfo(
                    0, null, null, null, null, null, null
                )
            )
        }
    }

    private fun loadData() {
        setLoading(true)

        viewModel.loadData()
    }

    private fun setInfo(info: LessonEditInfo) {
        viewModel.info = info

        binding.nameInput.setText(viewModel.info.name ?: "")
        binding.teacherInput.setText(viewModel.info.teacher ?: "")
        binding.cabinetInput.setText(
            if (viewModel.info.cabinet == null) {
                ""
            } else {
                String.format("%s", viewModel.info.cabinet)
            }
        )
        binding.campusInput.setText(viewModel.info.campusName ?: "")
        binding.orderInput.setText(
            if (viewModel.info.index != null) {
                String.format("%s", viewModel.info.index!! + 1)
            } else {
                ""
            }
        )
        binding.typeInput.setText(viewModel.info.typeName ?: "")
    }

    private fun setLoading(loading: Boolean) {
        binding.loadingStatus.visibility = if (loading) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }
}