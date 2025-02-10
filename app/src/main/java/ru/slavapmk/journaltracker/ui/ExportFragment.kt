package ru.slavapmk.journaltracker.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerAdapter
import ru.slavapmk.journaltracker.R
import ru.slavapmk.journaltracker.databinding.FragmentExportBinding

class ExportFragment : Fragment() {
    private lateinit var binding: FragmentExportBinding
    private val activity: MainActivity by lazy { requireActivity() as MainActivity }
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

        binding.pager.adapter = ExportAdapter(
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

class ExportAdapter(
    private val context: Context,
    private val listNames: List<String>,
    private val listLayouts: List<Int>,
) : PagerAdapter() {
    override fun getCount(): Int {
        return 3
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getPageTitle(position: Int): CharSequence {
        return listNames[position]
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(
            listLayouts[position],
            container,
            false
        )
        container.addView(layout)
        return layout
    }

    override fun destroyItem(collection: ViewGroup, position: Int, `object`: Any) {
        collection.removeView(`object` as View?)
    }
}
