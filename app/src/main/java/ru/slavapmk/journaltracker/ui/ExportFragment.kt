package ru.slavapmk.journaltracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ru.slavapmk.journaltracker.R
import ru.slavapmk.journaltracker.databinding.FragmentExportBinding
import ru.slavapmk.journaltracker.viewModels.ExportPagerAdapter
import ru.slavapmk.journaltracker.viewModels.ExportViewModel

class ExportFragment : Fragment() {
    private lateinit var binding: FragmentExportBinding
    private val activity: MainActivity by lazy { requireActivity() as MainActivity }
    val viewModel by viewModels<ExportViewModel>()

    private val tabsNames by lazy {
        listOf(
            getString(R.string.export_tab_day),
            getString(R.string.export_tab_week),
            getString(R.string.export_tab_semester)
        )
    }
    private val tabsLayouts by lazy {
        listOf(
            R.layout.fragment_export_day,
            R.layout.fragment_export_week,
            R.layout.fragment_export_semester
        )
    }
    private val tabsIcons by lazy {
        listOf(
            R.drawable.baseline_today_24,
            R.drawable.baseline_calendar_view_week_24,
            R.drawable.baseline_summarize_24
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        binding = FragmentExportBinding.inflate(layoutInflater)

        binding.pager.adapter = ExportPagerAdapter(
            requireContext(),
            tabsNames,
            tabsLayouts
        )

        binding.tabLayout.setupWithViewPager(
            binding.pager
        )
        for ((i, tabIcon) in tabsIcons.withIndex()) {
            binding.tabLayout.getTabAt(
                i
            )?.setIcon(tabIcon)
        }
        return binding.root
    }
}


