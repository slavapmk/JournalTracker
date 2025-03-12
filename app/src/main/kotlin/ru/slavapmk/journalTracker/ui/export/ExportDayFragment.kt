package ru.slavapmk.journalTracker.ui.export

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ru.slavapmk.journalTracker.databinding.FragmentExportDayBinding
import ru.slavapmk.journalTracker.ui.MainActivity
import ru.slavapmk.journalTracker.ui.SharedKeys
import ru.slavapmk.journalTracker.viewModels.ExportDayViewModel
import ru.slavapmk.journalTracker.viewModels.SimpleDate
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar

class ExportDayFragment : Fragment() {
    private lateinit var binding: FragmentExportDayBinding
    private val activity: MainActivity by lazy { requireActivity() as MainActivity }
    val viewModel by viewModels<ExportDayViewModel>()

    private val shared: SharedPreferences by lazy {
        activity.getSharedPreferences(
            SharedKeys.SHARED_APP_ID, Context.MODE_PRIVATE
        )
    }

    private var _date: SimpleDate?
        get() = if (
            shared.contains(SharedKeys.SELECTED_DAY) &&
            shared.contains(SharedKeys.SELECTED_MONTH) &&
            shared.contains(SharedKeys.SELECTED_YEAR)
        ) {
            SimpleDate(
                shared.getInt(SharedKeys.SELECTED_DAY, -1),
                shared.getInt(SharedKeys.SELECTED_MONTH, -1),
                shared.getInt(SharedKeys.SELECTED_YEAR, -1),
            )
        } else {
            null
        }
        set(value) {
            if (value == null) {
                shared.edit()?.apply {
                    remove(SharedKeys.SELECTED_DAY)
                    remove(SharedKeys.SELECTED_MONTH)
                    remove(SharedKeys.SELECTED_YEAR)
                    apply()
                }
            } else {
                shared.edit()?.apply {
                    putInt(SharedKeys.SELECTED_DAY, value.day)
                    putInt(SharedKeys.SELECTED_MONTH, value.month)
                    putInt(SharedKeys.SELECTED_YEAR, value.year)
                    apply()
                }
            }
        }

    val date: SimpleDate
        get() =
            if (_date == null) {
                val now = nowDate()
                _date = now
                now
            } else {
                _date!!
            }

    private fun nowDate(): SimpleDate {
        val calendar: Calendar = GregorianCalendar.getInstance().apply {
            time = Date()
        }
        return SimpleDate(
            calendar[Calendar.DAY_OF_MONTH],
            calendar[Calendar.MONTH] + 1,
            calendar[Calendar.YEAR]
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        binding = FragmentExportDayBinding.inflate(layoutInflater)

        binding.saveExcel.setOnClickListener {
            activity.setLoading(true)
            viewModel.saveExcel(
                requireContext(),
                date,
                shared.getString(SharedKeys.GROUP_NAME_KEY, "")!!
            )
        }

        viewModel.savedLiveStatus.observe(viewLifecycleOwner) {
            activity.setLoading(false)
            Toast.makeText(
                requireContext(),
                "Файл успешно создан",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.shareExcel.setOnClickListener {
            activity.setLoading(true)
            viewModel.shareExcel(
                requireContext(),
                date,
                shared.getString(SharedKeys.GROUP_NAME_KEY, "")!!
            )
        }

        viewModel.sharedLiveStatus.observe(viewLifecycleOwner) {
            activity.setLoading(false)
            it?.let {
                startActivity(it)
            }
        }

        binding.openExcel.setOnClickListener {
            activity.setLoading(true)
            viewModel.openExcel(
                requireContext(),
                date,
                shared.getString(SharedKeys.GROUP_NAME_KEY, "")!!
            )
        }

        viewModel.openLiveStatus.observe(viewLifecycleOwner) {
            activity.setLoading(false)
            it?.let {
                startActivity(it)
            }
        }

        viewModel.statusCallback.observe(viewLifecycleOwner) {
            setStatus(it)
        }

        return binding.root
    }

    private fun setStatus(it: String?) {
        activity.setStatus(it ?: "")
    }
}