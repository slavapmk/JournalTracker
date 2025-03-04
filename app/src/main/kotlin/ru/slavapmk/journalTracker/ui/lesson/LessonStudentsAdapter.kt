package ru.slavapmk.journalTracker.ui.lesson

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import ru.slavapmk.journalTracker.R
import ru.slavapmk.journalTracker.dataModels.lesson.LessonStudentListItem

class LessonStudentsAdapter(
    private val students: List<LessonStudentListItem>,
    private val onUpdate: (LessonStudentListItem) -> Unit
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

        holder.visitingInput.setText(student.attendance.displayNameRes)
        val entries = StudentAttendance.entries
        val visitTypes = entries.map { it.displayNameRes }
        val adapter = ArrayAdapter(
            holder.context, android.R.layout.simple_dropdown_item_1line, visitTypes
        )
        holder.visitingInput.setAdapter(adapter)
        holder.visitingInput.setOnItemClickListener { parent, view, clickPosition, id ->
            student.attendance = entries[clickPosition]
            checkDescriptionVisibility(student, holder)
            onUpdate(student)
        }

        holder.descriptionInput.setText(student.description)
        holder.descriptionInput.doAfterTextChanged {
            student.description = holder.descriptionInput.text.toString()
            onUpdate(student)
        }
        checkDescriptionVisibility(student, holder)
    }

    fun checkDescriptionVisibility(
        info: LessonStudentListItem, holder: LessonStudentsViewHolder
    ) {
        holder.descriptionLayout.isVisible = info.attendance == StudentAttendance.RESPECTFUL_PASS
    }
}

class LessonStudentsViewHolder(
    var context: Context, itemView: View
) : RecyclerView.ViewHolder(itemView) {
    var index: TextView = itemView.findViewById(R.id.order)
    var name: TextView = itemView.findViewById(R.id.dates)
    var visitingLayout: TextInputLayout = itemView.findViewById(R.id.visiting_layout)
    var visitingInput: AutoCompleteTextView = itemView.findViewById(R.id.visiting_input)
    var descriptionLayout: TextInputLayout = itemView.findViewById(R.id.description_layout)
    var descriptionInput: TextInputEditText = itemView.findViewById(R.id.description_input)
}
