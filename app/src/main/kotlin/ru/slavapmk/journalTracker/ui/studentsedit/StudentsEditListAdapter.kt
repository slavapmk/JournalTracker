package ru.slavapmk.journalTracker.ui.studentsedit

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import ru.slavapmk.journalTracker.R
import ru.slavapmk.journalTracker.dataModels.studentsEdit.StudentsEditListItem

class StudentsEditListAdapter(
    private val students: MutableList<StudentsEditListItem>,
    private val delete: ((Int, StudentsEditListItem) -> Unit)
) : RecyclerView.Adapter<StudentEditListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentEditListViewHolder {
        val inflate = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student_edit, parent, false)
        return StudentEditListViewHolder(parent.context, inflate)
    }

    override fun getItemCount(): Int = students.size

    override fun onBindViewHolder(holder: StudentEditListViewHolder, position: Int) {
        val student = students[position]
        holder.index.text = holder.context.getString(R.string.item_edit_student_order, position + 1)
        holder.name.text = holder.context.getString(R.string.item_edit_student_name, student.name)
        holder.delete.setOnClickListener {
            delete(position, student)
        }
    }
}

class StudentEditListViewHolder(
    var context: Context, itemView: View
) : RecyclerView.ViewHolder(itemView) {
    var index: TextView = itemView.findViewById(R.id.order)
    var name: TextView = itemView.findViewById(R.id.name)
    var delete: MaterialButton = itemView.findViewById(R.id.delete_button)
}
