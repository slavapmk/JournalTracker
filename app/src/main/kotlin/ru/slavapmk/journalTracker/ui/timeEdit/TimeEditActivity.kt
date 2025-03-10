package ru.slavapmk.journalTracker.ui.timeEdit

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import ru.slavapmk.journalTracker.R
import ru.slavapmk.journalTracker.dataModels.timeEdit.TimeEditItem
import ru.slavapmk.journalTracker.databinding.ActivityTimeEditBinding
import ru.slavapmk.journalTracker.ui.DeleteDialog
import ru.slavapmk.journalTracker.ui.MainActivity.Companion.fmanager
import ru.slavapmk.journalTracker.viewModels.TimeEditViewModel
import java.util.Locale

class TimeEditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTimeEditBinding
    val viewModel by viewModels<TimeEditViewModel>()

    private val timeEditAdapter: TimeEditAdapter by lazy {
        TimeEditAdapter(
            viewModel.timeList,
            onDelete = { delete ->
                DeleteDialog {
                    val index = viewModel.timeList.indexOf(delete)
                    val size = viewModel.timeList.size
                    viewModel.delTime(delete)
                    binding.times.adapter?.notifyItemRemoved(index)
                    binding.times.adapter?.notifyItemRangeChanged(
                        index, size - index
                    )
                }.show(
                    supportFragmentManager.beginTransaction(),
                    "delete_lessons_dialog"
                )
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        fmanager = supportFragmentManager
        binding = ActivityTimeEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 5)
            insets
        }

        loadData()
    }

    private fun loadData() {
        setLoading(true)
        viewModel.loadTimes()
        viewModel.timeLiveData.observe(this) {
            setLoading(false)
            viewModel.timeList.apply {
                clear()
                addAll(it)
            }
            initInputs()
            checkEmptyMessage()
        }
    }

    private fun initInputs() {
        binding.startTimeInput.setOnClickListener {
            val dialog = MaterialTimePicker.Builder().apply {
                setTimeFormat(TimeFormat.CLOCK_24H)
                setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                setTitleText(R.string.edit_times_start)
            }.build()
            dialog.addOnPositiveButtonClickListener {
                val time = String.format(
                    Locale.getDefault(), "%02d:%02d", dialog.hour, dialog.minute
                )
                viewModel.startHours = dialog.hour
                viewModel.startMinutes = dialog.minute
                binding.startTimeInput.setText(time)
            }
            dialog.show(supportFragmentManager, "time_start")
        }

        binding.endTimeInput.setOnClickListener {
            val dialog = MaterialTimePicker.Builder().apply {
                setTimeFormat(TimeFormat.CLOCK_24H)
                setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                setTitleText(R.string.edit_times_end)
            }.build()
            dialog.addOnPositiveButtonClickListener {
                val time = String.format(
                    Locale.getDefault(), "%02d:%02d", dialog.hour, dialog.minute
                )
                viewModel.endHours = dialog.hour
                viewModel.endMinutes = dialog.minute
                binding.endTimeInput.setText(time)
            }
            dialog.show(supportFragmentManager, "time_end")
        }

        binding.times.layoutManager = LinearLayoutManager(this)
        binding.times.adapter = timeEditAdapter

        binding.addButton.setOnClickListener {
            if (
                viewModel.startHours == -1 || viewModel.startMinutes == -1
                || viewModel.endHours == -1 || viewModel.endMinutes == -1
            ) {
                return@setOnClickListener
            }
            if (
                (viewModel.startHours > viewModel.endHours) ||
                (viewModel.startHours == viewModel.endHours &&
                        viewModel.startMinutes > viewModel.endMinutes)
            ) {
                return@setOnClickListener
            }
            val insertIndex = viewModel.addTime(
                TimeEditItem(
                    viewModel.startHours,
                    viewModel.startMinutes,
                    viewModel.endHours,
                    viewModel.endMinutes
                )
            )
            timeEditAdapter.notifyItemInserted(insertIndex)
            timeEditAdapter.notifyItemRangeChanged(
                insertIndex, viewModel.timeList.size - insertIndex
            )
            binding.times.scrollToPosition(insertIndex)
            binding.startTimeInput.text?.clear()
            binding.endTimeInput.text?.clear()
            viewModel.startHours = -1
            viewModel.startMinutes = -1
            viewModel.endHours = -1
            viewModel.endMinutes = -1
            checkEmptyMessage()
        }

        if (viewModel.startHours != -1 && viewModel.startMinutes != -1) {
            binding.startTimeInput.setText(
                String.format(
                    Locale.getDefault(), "%02d:%02d",
                    viewModel.startHours, viewModel.startMinutes
                )
            )
        }
        if (viewModel.endHours != -1 && viewModel.endMinutes != -1) {
            binding.endTimeInput.setText(
                String.format(
                    Locale.getDefault(), "%02d:%02d",
                    viewModel.endHours, viewModel.endMinutes
                )
            )
        }
    }

    private fun checkEmptyMessage() {
        binding.addRequirement.isVisible = viewModel.timeList.isEmpty()
    }

    private fun setLoading(loading: Boolean) {
        binding.loadingStatus.isVisible = loading
    }
}