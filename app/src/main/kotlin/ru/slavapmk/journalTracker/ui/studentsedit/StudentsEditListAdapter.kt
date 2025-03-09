package ru.slavapmk.journalTracker.ui.studentsedit

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import ru.slavapmk.journalTracker.R
import ru.slavapmk.journalTracker.dataModels.studentsEdit.StudentsEditListItem
import ru.slavapmk.journalTracker.dataModels.StudentAttendanceLesson

class StudentsEditListAdapter(
    private val students: MutableList<StudentsEditListItem>,
    private val onDelete: (StudentsEditListItem) -> Unit,
    private val onUpdate: (StudentsEditListItem) -> Unit
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
            onDelete(student)
        }

        holder.defaultInput.setText(student.default?.displayNameRes ?: R.string.empty)
        val entries = StudentAttendanceLesson.entries
        val visitTypes = entries.map {
            holder.context.getString(it.displayNameRes)
        }
        val adapter = ArrayAdapter(
            holder.context, android.R.layout.simple_dropdown_item_1line, visitTypes
        )
        holder.defaultInput.setAdapter(adapter)
        holder.defaultInput.setOnItemClickListener { _, _, clickPosition, _ ->
            student.default = entries[clickPosition]
            onUpdate(student)
        }

        holder.itemView.setOnClickListener {
            val visibility = !holder.defaultTitle.isVisible
            holder.defaultLayout.isVisible = visibility
            holder.defaultTitle.isVisible = visibility
        }
    }
}

class StudentEditListViewHolder(
    var context: Context, itemView: View
) : RecyclerView.ViewHolder(itemView) {
    var index: TextView = itemView.findViewById(R.id.order)
    var name: TextView = itemView.findViewById(R.id.name)
    var delete: MaterialButton = itemView.findViewById(R.id.delete_button)
    var defaultTitle: TextView = itemView.findViewById(R.id.default_title)
    var defaultLayout: TextInputLayout = itemView.findViewById(R.id.default_layout)
    var defaultInput: AutoCompleteTextView = itemView.findViewById(R.id.default_input)
}
