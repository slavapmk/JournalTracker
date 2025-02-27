package ru.slavapmk.journalTracker.ui.timeEdit

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import ru.slavapmk.journalTracker.R
import ru.slavapmk.journalTracker.dataModels.timeEdit.TimeEditItem

class TimeEditAdapter(
    private val times: List<TimeEditItem>,
    private val onDelete: ((TimeEditItem) -> Unit)
) : RecyclerView.Adapter<TimeEditViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeEditViewHolder {
        val inflate = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_time_edit, parent, false)
        return TimeEditViewHolder(parent.context, inflate)
    }

    override fun getItemCount(): Int = times.size
    override fun onBindViewHolder(holder: TimeEditViewHolder, position: Int) {
        val time = times[position]
        holder.order.text = holder.context.getString(R.string.item_edit_student_order, position + 1)
        holder.times.text = holder.context.getString(
            R.string.item_edit_time,
            time.startHours,
            time.startMinutes,
            time.endHours,
            time.endMinutes
        )
        holder.deleteButton.setOnClickListener {
            onDelete(time)
        }
    }
}

class TimeEditViewHolder(
    var context: Context, itemView: View
) : RecyclerView.ViewHolder(itemView) {
    var order: TextView = itemView.findViewById(R.id.order)
    var times: TextView = itemView.findViewById(R.id.dates)
    var deleteButton: MaterialButton = itemView.findViewById(R.id.delete_button)
}
