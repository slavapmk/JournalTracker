package ru.slavapmk.journalTracker.ui.lesson

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import ru.slavapmk.journalTracker.R
import ru.slavapmk.journalTracker.dataModels.lesson.LessonStudentListItem
import ru.slavapmk.journalTracker.dataModels.toEdit
import ru.slavapmk.journalTracker.databinding.ActivityLessonBinding
import ru.slavapmk.journalTracker.ui.LessonUpdateDialog
import ru.slavapmk.journalTracker.ui.MainActivity.Companion.fmanager
import ru.slavapmk.journalTracker.ui.SharedKeys
import ru.slavapmk.journalTracker.ui.lessonEdit.LessonEditActivity
import ru.slavapmk.journalTracker.viewModels.LessonViewModel

class LessonActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLessonBinding
    val viewModel by viewModels<LessonViewModel>()

    private val shared: SharedPreferences by lazy {
        getSharedPreferences(
            SharedKeys.SHARED_APP_ID, Context.MODE_PRIVATE
        )
    }

    private val lessonId by lazy {
        if (!intent.hasExtra(SharedKeys.SELECTED_LESSON)) {
            throw RuntimeException("Lesson id not catch")
        } else {
            intent.getIntExtra(SharedKeys.SELECTED_LESSON, -1)
        }
    }

    private fun showMenu(v: View, @MenuRes menuRes: Int) {
        val popup = PopupMenu(this, v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.select_all -> {
                    Toast.makeText(this, "select_all", Toast.LENGTH_SHORT).show()
                }
                R.id.deselect_all -> {}
                R.id.create_shortest_list -> {}
                R.id.create_visited_list -> {}
                R.id.create_unvisited_list -> {}
                R.id.edit_lesson -> {
                    buttonEditLesson()
                }

                R.id.delete_lesson -> {
                    buttonDeleteLesson()
                }

                else -> {
                    Toast.makeText(this, "okak", Toast.LENGTH_SHORT).show()
                }
            }
            return@setOnMenuItemClickListener true
        }

        popup.setOnDismissListener {}
        popup.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        fmanager = supportFragmentManager
        binding = ActivityLessonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel.sharedPreferences = shared

        binding.menuButton.setOnClickListener { button ->
            showMenu(button, R.menu.lesson_menu)
        }

        binding.saveButton.setOnClickListener {
            setLoading(true)
            viewModel.saveAll()
        }
        binding.students.layoutManager = LinearLayoutManager(this)
        binding.students.adapter =
            LessonStudentsAdapter(viewModel.studentAttendances) { updateStudent ->
                viewModel.updateStudent(updateStudent)
            }
    }

    private fun buttonDeleteLesson() = LessonUpdateDialog(
        {
            deleteLessons(false)
        }, {
            deleteLessons(true)
        }
    ).show(
        supportFragmentManager.beginTransaction(),
        "delete_lessons_dialog"
    )

    private fun buttonEditLesson() {
        startActivity(
            Intent(this, LessonEditActivity::class.java).apply {
                putExtra(SharedKeys.SELECTED_LESSON, lessonId)
            }
        )
    }

    override fun onResume() {
        super.onResume()
        loadData(lessonId)
    }

    private fun loadData(
        lessonId: Int
    ) {
        setLoading(true)
        viewModel.loadData(lessonId)
        viewModel.lessonInfoLiveData.observe(this) { lessonInfo ->
            viewModel.info = lessonInfo
            viewModel.loadAttendance()
        }
        viewModel.insertedLiveData.observe(this) {
            viewModel.loadAttendance()
        }
        viewModel.reloadAttendanceLiveData.observe(this) { attendances ->
            setLoading(false)
            val oldSize = viewModel.studentAttendances.size
            viewModel.studentAttendances.apply {
                clear()
                addAll(
                    attendances.map {
                        LessonStudentListItem(
                            it.id,
                            it.studentId,
                            viewModel.allStudents.find { student -> student.id == it.studentId }!!.name,
                            it.attendance.toEdit(),
                            it.skipDescription
                        )
                    }
                )
            }
            val newSize = viewModel.studentAttendances.size
            injectData()
            binding.students.adapter?.notifyItemRangeChanged(0, maxOf(oldSize, newSize))
            checkEmptyMessage()
        }
        viewModel.savedLiveData.observe(this) {
            finish()
        }
    }

    private fun injectData() {
        binding.lessonName.text = getString(
            R.string.students_lesson_name,
            (viewModel.info?.index ?: 0) + 1,
            viewModel.info?.name ?: "",
            getString(
                viewModel.info?.type?.shortNameRes ?: R.string.empty
            )
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

        viewModel.info?.type?.colorState?.let {
            binding.typeColor.setBackgroundColor(
                getColor(it)
            )
        }
    }

    private fun deleteLessons(updateNext: Boolean) {
        viewModel.deleteLessons(updateNext)
        finish()
    }

    private fun setLoading(loading: Boolean) {
        binding.loadingStatus.isVisible = loading
    }

    private fun checkEmptyMessage() {
        binding.addRequirement.isVisible = viewModel.studentAttendances.isEmpty()
    }
}