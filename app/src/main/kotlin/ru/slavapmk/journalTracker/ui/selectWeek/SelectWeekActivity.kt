package ru.slavapmk.journalTracker.ui.selectWeek

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import ru.slavapmk.journalTracker.R
import ru.slavapmk.journalTracker.databinding.ActivitySelectWeekBinding
import ru.slavapmk.journalTracker.ui.MainActivity.Companion.fmanager
import ru.slavapmk.journalTracker.utils.generateWeeks
import ru.slavapmk.journalTracker.viewModels.SelectWeekViewModel

class SelectWeekActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectWeekBinding
    val viewModel by viewModels<SelectWeekViewModel>()

    private val shared: SharedPreferences by lazy {
        getSharedPreferences(
            getString(R.string.shared_id), Context.MODE_PRIVATE
        )
    }

    private val semesterId: Int? by lazy {
        if (shared.contains(getString(R.string.semester_shared_id))) {
            shared.getInt(getString(R.string.semester_shared_id), -1)
        } else {
            null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        fmanager = supportFragmentManager
        binding = ActivitySelectWeekBinding.inflate(layoutInflater)
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
        binding.weeks.layoutManager = LinearLayoutManager(this)
        binding.weeks.adapter = SelectWeekAdapter(viewModel.weeks) { week ->
            shared.edit {
                putInt(
                    getString(R.string.week_shared_id),
                    viewModel.weeks.indexOf(week)
                )
            }
            finish()
        }
    }

    private fun load() {
        if (semesterId == null) {
            return
        }
        setLoading(true)
        viewModel.loadSemester(semesterId!!)
        viewModel.semesterMutableLiveData.observe(this) { semester ->
            viewModel.semester = semester
            viewModel.weeks.clear()
            viewModel.weeks.addAll(generateWeeks(viewModel.semester!!))
            binding.weeks.adapter?.notifyItemRangeChanged(
                0, viewModel.weeks.size
            )
            setLoading(false)
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.loadingStatus.isVisible = loading
    }
}