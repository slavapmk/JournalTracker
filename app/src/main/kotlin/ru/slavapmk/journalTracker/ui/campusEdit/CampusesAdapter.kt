package ru.slavapmk.journalTracker.ui.campusEdit

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import ru.slavapmk.journalTracker.R
import ru.slavapmk.journalTracker.dataModels.campuses.Campus
import ru.slavapmk.journalTracker.dataModels.lesson.LessonStudentListItem

class CampusesAdapter(
    private val campuses: List<Campus>,
    private val onDelete: ((Campus) -> Unit)
) : RecyclerView.Adapter<CampusesViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CampusesViewHolder {
        val inflate = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_campus_edit, parent, false)
        return CampusesViewHolder(parent.context, inflate)
    }

    override fun getItemCount(): Int = campuses.size
    override fun onBindViewHolder(holder: CampusesViewHolder, position: Int) {
        val campus = campuses[position]
        holder.order.text = holder.context.getString(R.string.item_edit_student_order, position + 1)
        holder.codename.text = campus.codename
        holder.name.text = campus.name
        holder.deleteButton.setOnClickListener {
            onDelete(campus)
        }
    }
}

class CampusesViewHolder(
    var context: Context, itemView: View
) : RecyclerView.ViewHolder(itemView) {
    var order: TextView = itemView.findViewById(R.id.order)
    var codename: TextView = itemView.findViewById(R.id.codename)
    var name: TextView = itemView.findViewById(R.id.name)
    var deleteButton: MaterialButton = itemView.findViewById(R.id.delete_button)
}
