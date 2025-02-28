package ru.slavapmk.journalTracker.ui.schedule

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import ru.slavapmk.journalTracker.R
import ru.slavapmk.journalTracker.dataModels.schedule.ScheduleListLesson
import ru.slavapmk.journalTracker.databinding.FragmentScheduleBinding
import ru.slavapmk.journalTracker.ui.MainActivity
import ru.slavapmk.journalTracker.ui.lesson.LessonActivity
import ru.slavapmk.journalTracker.ui.lessonEdit.LessonEditActivity
import ru.slavapmk.journalTracker.ui.selectWeek.SelectWeekActivity
import ru.slavapmk.journalTracker.ui.semesters.SemestersActivity
import ru.slavapmk.journalTracker.viewModels.LoadScheduleData
import ru.slavapmk.journalTracker.viewModels.ScheduleViewModel

class ScheduleFragment : Fragment() {
    private lateinit var binding: FragmentScheduleBinding
    private val activity: MainActivity by lazy { requireActivity() as MainActivity }
    val viewModel by viewModels<ScheduleViewModel>()

    private val shared: SharedPreferences by lazy {
        activity.getSharedPreferences(
            getString(R.string.shared_id), Context.MODE_PRIVATE
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)

        binding = FragmentScheduleBinding.inflate(layoutInflater)

        init()
        load()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        load()
    }

    private fun init() {
        binding.addLessonButton.setOnClickListener {
            val intent = Intent(activity, LessonEditActivity::class.java)
            activity.startActivity(intent)
        }

        binding.selectWeek.setOnClickListener {
            val intent = Intent(activity, SelectWeekActivity::class.java)
            activity.startActivity(intent)
        }

        binding.semester.setOnClickListener {
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

        viewModel.mediatorLiveData.observe(viewLifecycleOwner) { loadData ->
            binding.semester.text = getString(
                R.string.schedule_semester,
                when (val i = loadData.semesters.indexOfFirst { it.id == viewModel.semesterId }) {
                    -1 -> "?"
                    else -> i + 1
                }
            )

            updateLessons(loadData)
            activity.setLoading(false)
        }
    }

    private fun updateLessons(loadData: LoadScheduleData) {
        val timesMap = loadData.times.associateBy { it.id }
        val campusesMap = loadData.campuses.associateBy { it.id }

        viewModel.lessons.clear()
        viewModel.lessons.addAll(
            loadData.lessons.map { lessonEntity ->
                val time = timesMap[lessonEntity.timeId]
                ScheduleListLesson(
                    lessonEntity.id,
                    time!!.startHour,
                    time.startMinute,
                    time.endHour,
                    time.endMinute,
                    lessonEntity.type,
                    lessonEntity.cabinet,
                    campusesMap[lessonEntity.campusId]!!.codename,
                    lessonEntity.name,
                    lessonEntity.teacher,
                )
            }
        )
        binding.lessons.adapter?.notifyItemRangeChanged(0, viewModel.lessons.size)
    }

    private fun load() {
        activity.setLoading(true)
        val key = getString(R.string.semester_shared_id)
        if (shared.contains(key)) {
            viewModel.semesterId = shared.getInt(key, -1)
        }

        viewModel.loadSemesters()

        binding.semester.text = getString(
            R.string.schedule_semester,
            viewModel.semesterId?.toString() ?: "?"
        )
    }
}