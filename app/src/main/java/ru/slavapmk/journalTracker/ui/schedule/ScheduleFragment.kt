package ru.slavapmk.journalTracker.ui.schedule

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import ru.slavapmk.journalTracker.databinding.FragmentScheduleBinding
import ru.slavapmk.journalTracker.ui.lessonEdit.LessonEditActivity
import ru.slavapmk.journalTracker.ui.lesson.LessonActivity
import ru.slavapmk.journalTracker.ui.MainActivity
import ru.slavapmk.journalTracker.ui.SelectWeekActivity
import ru.slavapmk.journalTracker.ui.SemestersActivity
import ru.slavapmk.journalTracker.viewModels.ScheduleViewModel

class ScheduleFragment : Fragment() {
    private lateinit var binding: FragmentScheduleBinding
    private val activity: MainActivity by lazy { requireActivity() as MainActivity }
    val viewModel by viewModels<ScheduleViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)

        binding = FragmentScheduleBinding.inflate(layoutInflater)

        binding.addLessonButton.setOnClickListener {
            val intent = Intent(activity, LessonEditActivity::class.java)
            activity.startActivity(intent)
        }

        binding.selectWeek.setOnClickListener {
            val intent = Intent(activity, SelectWeekActivity::class.java)
            activity.startActivity(intent)
        }

        binding.week.setOnClickListener {
            val intent = Intent(activity, SemestersActivity::class.java)
            activity.startActivity(intent)
        }

        binding.lessons.layoutManager = LinearLayoutManager(requireContext())
        binding.lessons.adapter = ScheduleLessonsAdapter(
            viewModel.lessons
        ) { lesson ->
            startActivity(
                Intent(activity, LessonActivity::class.java).apply {
                    putExtra("LESSON_ID", lesson.id)
                }
            )
        }

        return binding.root
    }
}