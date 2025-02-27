package ru.slavapmk.journaltracker.viewModels

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter

class ExportPagerAdapter(
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