package ru.slavapmk.journalTracker.ui.lesson

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.slavapmk.journalTracker.R
import ru.slavapmk.journalTracker.dataModels.lesson.LessonStudentListItem

class LessonStudentsAdapter(
    private val students: List<LessonStudentListItem>
) : RecyclerView.Adapter<LessonStudentsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonStudentsViewHolder {
        val inflate = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student, parent, false)
        return LessonStudentsViewHolder(parent.context, inflate)
    }

    override fun getItemCount(): Int = students.size
    override fun onBindViewHolder(holder: LessonStudentsViewHolder, position: Int) {
        val student = students[position]
        holder.index.text = holder.context.getString(R.string.item_edit_student_order, position + 1)
        holder.name.text = holder.context.getString(R.string.item_edit_student_name, student.name)
        holder.visited.isChecked = student.visited
        holder.visited.setOnCheckedChangeListener { _, isChecked ->
            student.visited = isChecked
        }
    }
}

class LessonStudentsViewHolder(
    var context: Context, itemView: View
) : RecyclerView.ViewHolder(itemView) {
    var index: TextView = itemView.findViewById(R.id.position)
    var name: TextView = itemView.findViewById(R.id.name)
    var visited: CheckBox = itemView.findViewById(R.id.visited)
}
