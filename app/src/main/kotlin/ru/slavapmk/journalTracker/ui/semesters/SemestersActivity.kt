package ru.slavapmk.journalTracker.ui.semesters

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import ru.slavapmk.journalTracker.R
import ru.slavapmk.journalTracker.dataModels.semesters.Semester
import ru.slavapmk.journalTracker.databinding.ActivitySemestersBinding
import ru.slavapmk.journalTracker.ui.DeleteDialog
import ru.slavapmk.journalTracker.ui.MainActivity.Companion.fmanager
import ru.slavapmk.journalTracker.ui.SharedKeys
import ru.slavapmk.journalTracker.viewModels.SemestersViewModel
import java.util.Calendar
import java.util.TimeZone

class SemestersActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySemestersBinding
    val viewModel by viewModels<SemestersViewModel>()

    private val shared: SharedPreferences by lazy {
        getSharedPreferences(
            SharedKeys.SHARED_APP_ID, Context.MODE_PRIVATE
        )
    }

    private val semestersAdapter by lazy {
        SemestersAdapter(
            viewModel.semesters, { semester ->
                DeleteDialog {
                    binding.loadingStatus.visibility = View.VISIBLE
                    val indexOf = viewModel.semesters.indexOf(semester)
                    val size = viewModel.semesters.size
                    viewModel.semesters.remove(semester)
                    val updateCount = size - indexOf
                    binding.semesters.adapter?.notifyItemRemoved(indexOf)
                    binding.semesters.adapter?.notifyItemRangeChanged(
                        indexOf,
                        updateCount
                    )
                    viewModel.deleteSemester(semester)
                    binding.addRequirement.isVisible = viewModel.semesters.isEmpty()
                }.show(
                    supportFragmentManager.beginTransaction(),
                    "delete_lessons_dialog"
                )
            }, { semester ->
                shared.edit {
                    putInt(
                        SharedKeys.SEMESTER_ID,
                        semester.id!!
                    )
                }
                finish()
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        fmanager = supportFragmentManager
        binding = ActivitySemestersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        init()
        load()
    }

    private fun init() {
        binding.addButton.setOnClickListener {
            val dialog = MaterialDatePicker.Builder.dateRangePicker().apply {
                setTitleText(R.string.semester_dialog)
                setPositiveButtonText(R.string.semester_dialog_add)
                setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
            }.build()
            dialog.addOnPositiveButtonClickListener {
                val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                calendar.timeInMillis = it.first
                val startDay = calendar.get(Calendar.DAY_OF_MONTH)
                val startMonth = calendar.get(Calendar.MONTH) + 1
                val startYear = calendar.get(Calendar.YEAR)
                calendar.timeInMillis = it.second
                val endDay = calendar.get(Calendar.DAY_OF_MONTH)
                val endMonth = calendar.get(Calendar.MONTH) + 1
                val endYear = calendar.get(Calendar.YEAR)

                binding.loadingStatus.visibility = View.VISIBLE
                val element = Semester(
                    null,
                    startDay,
                    startMonth,
                    startYear,
                    endDay,
                    endMonth,
                    endYear,
                )
                val indexOf = viewModel.addSemester(element)
                binding.semesters.adapter?.notifyItemInserted(indexOf)
                binding.semesters.adapter?.notifyItemRangeChanged(
                    indexOf, viewModel.semesters.size - indexOf
                )
                binding.semesters.scrollToPosition(indexOf)
                binding.addRequirement.isVisible = viewModel.semesters.isEmpty()
            }
            dialog.show(supportFragmentManager, "SEMESTER_DATES")
        }

        binding.semesters.layoutManager = LinearLayoutManager(this)
        binding.semesters.adapter = semestersAdapter

        viewModel.semestersLiveData.observe(this) {
            viewModel.semesters.clear()
            viewModel.semesters.addAll(it)
            semestersAdapter.notifyItemRangeChanged(0, it.size)
            binding.loadingStatus.visibility = View.GONE
            binding.addRequirement.isVisible = viewModel.semesters.isEmpty()
        }

        viewModel.semesterUpdateLiveData.observe(this) { (old, new) ->
            val indexOf = viewModel.semesters.indexOf(old)
            viewModel.semesters[indexOf] = new
            semestersAdapter.notifyItemChanged(indexOf)
            binding.loadingStatus.visibility = View.GONE
            binding.addRequirement.isVisible = viewModel.semesters.isEmpty()
        }

        viewModel.endDeleteLiveData.observe(this) {
            binding.loadingStatus.visibility = View.GONE
        }
    }

    private fun load() {
        binding.loadingStatus.visibility = View.VISIBLE
        viewModel.loadSemesters()
    }
}