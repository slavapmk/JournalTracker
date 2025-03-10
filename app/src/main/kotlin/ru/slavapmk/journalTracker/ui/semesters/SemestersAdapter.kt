package ru.slavapmk.journalTracker.ui.semesters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import ru.slavapmk.journalTracker.R
import ru.slavapmk.journalTracker.dataModels.semesters.Semester

class SemestersAdapter(
    private val semesters: MutableList<Semester>,
    private val onDelete: ((Semester) -> Unit),
    private val onSelect: ((Semester) -> Unit),
    private val selectedId: Int? = null
) : RecyclerView.Adapter<SemestersViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SemestersViewHolder {
        val inflate = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_semester, parent, false)
        return SemestersViewHolder(parent.context, inflate)
    }

    override fun getItemCount(): Int = semesters.size
    override fun onBindViewHolder(holder: SemestersViewHolder, position: Int) {
        val semester = semesters[position]
        holder.order.text = holder.context.getString(
            R.string.item_lesson_order, position + 1
        )
        holder.name.text = holder.context.getString(
            R.string.item_semester_dates,
            semester.startDay, semester.startMonth, semester.startYear % 100,
            semester.endDay, semester.endMonth, semester.endYear % 100,
        )
        holder.deleteButton.setOnClickListener {
            onDelete(semester)
        }
        holder.itemView.setOnClickListener {
            onSelect(semester)
        }
        holder.selected.isChecked = semester.id == selectedId
    }
}

class SemestersViewHolder(
    var context: Context, itemView: View
) : RecyclerView.ViewHolder(itemView) {
    var order: TextView = itemView.findViewById(R.id.order)
    var name: TextView = itemView.findViewById(R.id.name)
    var deleteButton: MaterialButton = itemView.findViewById(R.id.delete_button)
    var selected: RadioButton = itemView.findViewById(R.id.selected)
}
