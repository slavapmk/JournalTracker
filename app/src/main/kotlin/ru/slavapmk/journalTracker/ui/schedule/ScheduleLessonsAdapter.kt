package ru.slavapmk.journalTracker.ui.schedule

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.slavapmk.journalTracker.R
import ru.slavapmk.journalTracker.dataModels.schedule.ScheduleListLesson

class ScheduleLessonsAdapter(
    private val lessons: MutableList<ScheduleListLesson>,
    private val onSelect: ((ScheduleListLesson) -> Unit)
) : RecyclerView.Adapter<ScheduleLessonsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleLessonsViewHolder {
        val inflate = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lesson, parent, false)
        return ScheduleLessonsViewHolder(parent.context, inflate)
    }

    override fun getItemCount(): Int = lessons.size
    override fun onBindViewHolder(holder: ScheduleLessonsViewHolder, position: Int) {
        val lesson = lessons[position]
        holder.order.text = holder.context.getString(
            R.string.item_lesson_order, lesson.index + 1
        )
        holder.time.text = holder.context.getString(
            R.string.item_lesson_times,
            lesson.startHour, lesson.startMinute, lesson.endHour, lesson.endMinute
        )
        holder.cabinet.text = holder.context.getString(
            R.string.item_lesson_cabinet,
            lesson.auditory, lesson.campus
        )
        holder.name.text = holder.context.getString(
            R.string.item_lesson_name,
            lesson.name,
            holder.context.getString(lesson.type.shortNameRes)
        )
        holder.teacher.text = holder.context.getString(
            R.string.item_lesson_teacher,
            lesson.teacher
        )
        holder.itemView.setOnClickListener {
            onSelect(lesson)
        }
        holder.typeColorView.backgroundTintList = ColorStateList.valueOf(
            holder.context.getColor(
                lesson.type.colorState
            )
        )
    }
}

class ScheduleLessonsViewHolder(
    var context: Context, itemView: View
) : RecyclerView.ViewHolder(itemView) {
    var order: TextView = itemView.findViewById(R.id.order_field)
    var time: TextView = itemView.findViewById(R.id.time)
    var cabinet: TextView = itemView.findViewById(R.id.cabinet)
    var name: TextView = itemView.findViewById(R.id.title)
    var teacher: TextView = itemView.findViewById(R.id.teacher)
    val typeColorView: View = itemView.findViewById(R.id.type_color)
}
