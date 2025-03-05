package ru.slavapmk.journalTracker.ui.timeEdit

import android.app.TimePickerDialog
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import ru.slavapmk.journalTracker.R
import ru.slavapmk.journalTracker.dataModels.timeEdit.TimeEditItem
import ru.slavapmk.journalTracker.databinding.ActivityTimeEditBinding
import ru.slavapmk.journalTracker.ui.DeleteDialog
import ru.slavapmk.journalTracker.ui.MainActivity.Companion.fmanager
import ru.slavapmk.journalTracker.viewModels.TimeEditViewModel
import java.util.Calendar
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
        }
    }

    private fun initInputs() {
        binding.startTimeInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(
                this,
                { _, selectedHour, selectedMinute ->
                    val time = String.format(
                        Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute
                    )
                    viewModel.startHours = selectedHour
                    viewModel.startMinutes = selectedMinute
                    binding.startTimeInput.setText(time)
                },
                hour, minute, true
            )

            timePickerDialog.show()
        }

        binding.endTimeInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(
                this,
                { _, selectedHour, selectedMinute ->
                    val time = String.format(
                        Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute
                    )
                    viewModel.endHours = selectedHour
                    viewModel.endMinutes = selectedMinute
                    binding.endTimeInput.setText(time)
                },
                hour, minute, true
            )

            timePickerDialog.show()
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

    fun setLoading(loading: Boolean) {
        binding.loadingStatus.isVisible = loading
    }
}