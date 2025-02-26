package ru.slavapmk.journaltracker.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import ru.slavapmk.journaltracker.R
import ru.slavapmk.journaltracker.datamodels.ListStudentItem

class StudentsListAdapter(
    private val students: MutableList<ListStudentItem>,
    private val delete: ((Int, ListStudentItem) -> Unit)
) : RecyclerView.Adapter<StudentListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentListViewHolder {
        val inflate = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student_edit, parent, false)
        return StudentListViewHolder(parent.context, inflate)
    }

    override fun getItemCount(): Int = students.size

    override fun onBindViewHolder(holder: StudentListViewHolder, position: Int) {
        val student = students[position]
        holder.index.text = holder.context.getString(R.string.item_edit_student_order, position + 1)
        holder.name.text = holder.context.getString(R.string.item_edit_student_name, student.name)
        holder.delete.setOnClickListener {
            delete(position, student)
        }
    }
}

class StudentListViewHolder(
    var context: Context, itemView: View
) : RecyclerView.ViewHolder(itemView) {
    var index: TextView = itemView.findViewById(R.id.position)
    var name: TextView = itemView.findViewById(R.id.name)
    var delete: MaterialButton = itemView.findViewById(R.id.delete_button)
}
