package ru.slavapmk.journalTracker.ui.selectWeek

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.slavapmk.journalTracker.R
import ru.slavapmk.journalTracker.dataModels.selectWeek.Week

class SelectWeekAdapter(
    private val weeks: List<Week>,
    private val onSelect: ((Week) -> Unit)
) : RecyclerView.Adapter<SelectWeekViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectWeekViewHolder {
        val inflate = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_week, parent, false)
        return SelectWeekViewHolder(parent.context, inflate)
    }

    override fun getItemCount(): Int = weeks.size
    override fun onBindViewHolder(holder: SelectWeekViewHolder, position: Int) {
        val week = weeks[position]
        holder.order.text = holder.context.getString(R.string.item_edit_student_order, position + 1)
        holder.dates.text = holder.context.getString(
            R.string.item_week_dates,
            week.startDay,
            week.startMonth,
            week.startYear % 100,
            week.endDay,
            week.endMonth,
            week.endYear % 100,
        )
        holder.itemView.setOnClickListener {
            onSelect(week)
        }
    }
}

class SelectWeekViewHolder(
    var context: Context, itemView: View
) : RecyclerView.ViewHolder(itemView) {
    var order: TextView = itemView.findViewById(R.id.order)
    var dates: TextView = itemView.findViewById(R.id.dates)
}
