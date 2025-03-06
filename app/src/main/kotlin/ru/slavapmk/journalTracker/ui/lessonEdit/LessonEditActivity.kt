package ru.slavapmk.journalTracker.ui.lessonEdit

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ru.slavapmk.journalTracker.R
import ru.slavapmk.journalTracker.dataModels.LessonTypeEdit
import ru.slavapmk.journalTracker.dataModels.lessonEdit.LessonEditInfo
import ru.slavapmk.journalTracker.dataModels.toEdit
import ru.slavapmk.journalTracker.databinding.ActivityLessonEditBinding
import ru.slavapmk.journalTracker.ui.LessonUpdateDialog
import ru.slavapmk.journalTracker.ui.SharedKeys
import ru.slavapmk.journalTracker.viewModels.EditLessonViewModel
import ru.slavapmk.journalTracker.viewModels.SimpleDate

class LessonEditActivity : AppCompatActivity() {
    companion object {
        const val LESSON_ID = "LESSON_ID"
    }

    private lateinit var binding: ActivityLessonEditBinding
    val viewModel by viewModels<EditLessonViewModel>()
    private val editMode by lazy {
        intent.hasExtra(LESSON_ID)
    }

    private val shared: SharedPreferences by lazy {
        getSharedPreferences(
            SharedKeys.SHARED_APP_ID, Context.MODE_PRIVATE
        )
    }

    private val lessonTypes by lazy {
        LessonTypeEdit.entries.map {
            getString(it.nameRes)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLessonEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel.sharedPreferences = shared

        loadData()

        binding.addButton.setOnClickListener {
            if (
                viewModel.info.name.isNullOrEmpty() ||
                viewModel.info.typeName == null ||
                viewModel.info.teacher.isNullOrEmpty() ||
                viewModel.info.index == null ||
                viewModel.info.cabinet == null ||
                viewModel.info.campusId == null
            ) {
                return@setOnClickListener
            }

            LessonUpdateDialog(
                {
                    addLessons(false)
                }, {
                    addLessons(true)
                }
            ).show(
                supportFragmentManager.beginTransaction(),
                "update_lessons_dialog"
            )
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
                    entity.type.toEdit(),
                    entity.teacher,
                    entity.cabinet,
                    entity.campusId
                )
            })
            initInputs()
            setLoading(false)
        }
        viewModel.savingStatusLiveData.observe(this) {
            setLoading(false)
            finish()
        }
    }

    private fun initLesson() {
        if (editMode) {
            val id = intent.getIntExtra(LESSON_ID, -1)
            viewModel.loadLesson(id)
        } else {
            setLoading(false)
            setInfo(
                LessonEditInfo(
                    0, null, null, null, null, null, null
                )
            )
            initInputs()
        }
    }

    private fun initInputs() {
        binding.nameInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.toString().isNullOrBlank()) {
                    return
                }
                viewModel.info.name = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        binding.teacherInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.toString().isNullOrBlank()) {
                    return
                }
                viewModel.info.teacher = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        binding.cabinetInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.toString().isNullOrBlank()) {
                    return
                }
                viewModel.info.cabinet = s.toString().toInt()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        val campusNames = viewModel.campuses.map {
            it.name
        }
        viewModel.info.campusId?.let {
            binding.campusInput.setText(
                viewModel.campuses.find { it.id == viewModel.info.campusId }!!.name
            )
        }
        val campusesAdapter = ArrayAdapter(
            this, android.R.layout.simple_dropdown_item_1line, campusNames
        )
        binding.campusInput.setAdapter(campusesAdapter)
        binding.campusInput.setOnItemClickListener { _, _, position, _ ->
            viewModel.info.campusId = viewModel.campuses[position].id
        }

        val timeNames = List(viewModel.times.size) { index -> (index + 1).toString() }
        binding.orderInput.setText(
            if (viewModel.info.index != null) {
                String.format("%s", viewModel.info.index!! + 1)
            } else {
                ""
            }
        )
        val timesAdapter = ArrayAdapter(
            this, android.R.layout.simple_dropdown_item_1line, timeNames
        )
        binding.orderInput.setAdapter(timesAdapter)
        binding.orderInput.setOnItemClickListener { _, _, position, _ ->
            viewModel.info.index = position
        }

        binding.typeInput.setText(viewModel.info.typeName?.nameRes ?: R.string.empty)
        val typesAdapter = ArrayAdapter(
            this, android.R.layout.simple_dropdown_item_1line, lessonTypes
        )
        binding.typeInput.setAdapter(typesAdapter)
        binding.typeInput.setOnItemClickListener { _, _, position, _ ->
            viewModel.info.typeName = LessonTypeEdit.entries[position]
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
    }

    private fun addLessons(updateNext: Boolean) {
        setLoading(true)
        if (editMode) {
            viewModel.updateLessons(updateNext)
        } else {
            viewModel.addNewLesson(updateNext)
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.loadingStatus.visibility = if (loading) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }
}
