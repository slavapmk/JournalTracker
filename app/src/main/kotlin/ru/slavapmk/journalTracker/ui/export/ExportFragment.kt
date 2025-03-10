package ru.slavapmk.journalTracker.ui.export

import ExportPagerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import ru.slavapmk.journalTracker.R
import ru.slavapmk.journalTracker.databinding.FragmentExportBinding
import ru.slavapmk.journalTracker.ui.MainActivity
import ru.slavapmk.journalTracker.viewModels.ExportViewModel

class ExportFragment : Fragment() {
    private var _binding: FragmentExportBinding? = null
    private val binding get() = _binding!!

    private val activity: MainActivity by lazy { requireActivity() as MainActivity }
    private val viewModel by viewModels<ExportViewModel>()

    private val tabsNames by lazy {
        listOf(
            getString(R.string.export_tab_day),
            getString(R.string.export_tab_week),
            getString(R.string.export_tab_semester)
        )
    }

    private val tabsIcons by lazy {
        listOf(
            R.drawable.baseline_today_24,
            R.drawable.baseline_calendar_view_week_24,
            R.drawable.baseline_summarize_24
        )
    }

    private val fragments by lazy {
        listOf(
            ExportDayFragment(),
            ExportWeekFragment(),
            ExportSummaryFragment()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExportBinding.inflate(inflater, container, false)

        val adapter = ExportPagerAdapter(requireActivity(), fragments)
        binding.pager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            tab.text = tabsNames[position]
            tab.setIcon(tabsIcons[position])
        }.attach()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
