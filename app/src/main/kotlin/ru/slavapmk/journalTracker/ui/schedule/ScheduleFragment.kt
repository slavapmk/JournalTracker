package ru.slavapmk.journalTracker.ui.schedule

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import ru.slavapmk.journalTracker.R
import ru.slavapmk.journalTracker.dataModels.schedule.ScheduleListLesson
import ru.slavapmk.journalTracker.dataModels.selectWeek.Semester
import ru.slavapmk.journalTracker.databinding.FragmentScheduleBinding
import ru.slavapmk.journalTracker.storageModels.entities.SemesterEntity
import ru.slavapmk.journalTracker.ui.MainActivity
import ru.slavapmk.journalTracker.ui.lesson.LessonActivity
import ru.slavapmk.journalTracker.ui.lessonEdit.LessonEditActivity
import ru.slavapmk.journalTracker.ui.selectWeek.SelectWeekActivity
import ru.slavapmk.journalTracker.ui.semesters.SemestersActivity
import ru.slavapmk.journalTracker.utils.generateWeeks
import ru.slavapmk.journalTracker.viewModels.ItemDate
import ru.slavapmk.journalTracker.viewModels.ScheduleViewModel
import ru.slavapmk.journalTracker.viewModels.SimpleDate
import ru.slavapmk.journalTracker.viewModels.compareTo
import java.util.Locale

class ScheduleFragment : Fragment() {
    private lateinit var binding: FragmentScheduleBinding
    private val activity: MainActivity by lazy { requireActivity() as MainActivity }
    val viewModel by viewModels<ScheduleViewModel>()

    private val shared: SharedPreferences by lazy {
        activity.getSharedPreferences(
            getString(R.string.shared_id), Context.MODE_PRIVATE
        )
    }

    val dates by lazy {
        listOf(
            binding.monday,
            binding.tuesday,
            binding.wednesday,
            binding.thursday,
            binding.friday,
            binding.saturday,
            binding.sunday
        )
    }

    private val monthsStrings by lazy {
        listOf(
            getString(R.string.month_january),
            getString(R.string.month_february),
            getString(R.string.month_march),
            getString(R.string.month_april),
            getString(R.string.month_may),
            getString(R.string.month_june),
            getString(R.string.month_july),
            getString(R.string.month_august),
            getString(R.string.month_september),
            getString(R.string.month_october),
            getString(R.string.month_november),
            getString(R.string.month_december)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)

        binding = FragmentScheduleBinding.inflate(layoutInflater)

        init()
        initDays()
        viewModel.sharedPreferences = shared

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        load()
    }

    private fun initDays() {
        binding.monday.dayOfWeek.text = getString(R.string.day_monday)
        binding.tuesday.dayOfWeek.text = getString(R.string.day_tuesday)
        binding.wednesday.dayOfWeek.text = getString(R.string.day_wednesday)
        binding.thursday.dayOfWeek.text = getString(R.string.day_thurday)
        binding.friday.dayOfWeek.text = getString(R.string.day_friday)
        binding.saturday.dayOfWeek.text = getString(R.string.day_saturday)
        binding.sunday.dayOfWeek.text = getString(R.string.day_sunday)

        val parseWeek = viewModel.parseWeek()
        val selectedDate: SimpleDate = viewModel.getDate()
        for ((i, date) in dates.withIndex()) {
            if (parseWeek != null) {
                val dayItemDate: ItemDate = parseWeek[i]
                if (dayItemDate.contains) {
                    date.root.visibility = View.VISIBLE
                } else {
                    date.root.visibility = View.INVISIBLE
                }
                date.date.text = getString(
                    R.string.item_date_date, dayItemDate.day
                )
                date.month.text = monthsStrings[dayItemDate.month]
                if (
                    selectedDate.day == dayItemDate.day &&
                    selectedDate.month - 1 == dayItemDate.month &&
                    selectedDate.year == dayItemDate.year
                ) {
                    date.selected.visibility = View.VISIBLE
                } else {
                    date.selected.visibility = View.GONE
                }
                date.root.setOnClickListener {
                    unselectDates()
                    date.selected.visibility = View.VISIBLE
                    viewModel.setDate(
                        SimpleDate(
                            dayItemDate.day,
                            dayItemDate.month + 1,
                            dayItemDate.year
                        )
                    )
                }
            } else {
                date.root.visibility = View.INVISIBLE
            }
        }
    }

    private fun unselectDates() {
        for (date in dates) {
            date.selected.visibility = View.GONE
        }
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

        binding.currentDay.setOnClickListener {
            viewModel.setDate(null)
            selectDate()
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
            if (loadData.semesters.isEmpty()) {
                activity.setLoading(false)
                binding.week.isVisible = true
                return@observe
            }
            viewModel.semesters = loadData.semesters

            var semesterIndex = viewModel.semesters.indexOfFirst { it.id == viewModel.semesterId }
            if (semesterIndex == -1) {
                semesterIndex = 0
                viewModel.semesterId = viewModel.semesters[semesterIndex].id
            }
            val semester = viewModel.semesters[semesterIndex]
            binding.semester.text = getString(
                R.string.schedule_semester,
                String.format(Locale.getDefault(), "%02d", semesterIndex + 1)
            )

            viewModel.weeks.clear()
            viewModel.weeks.addAll(
                generateWeeks(
                    Semester(
                        semester.startDay,
                        semester.startMonth,
                        semester.startYear,
                        semester.endDay,
                        semester.endMonth,
                        semester.endYear,
                    )
                )
            )
            selectDate()

            viewModel.timesMap.clear()
            viewModel.timesMap.putAll(
                loadData.times.associateBy { it.id }
            )

            viewModel.campusesMap.clear()
            viewModel.campusesMap.putAll(
                loadData.campuses.associateBy { it.id }
            )

            viewModel.loadLessons()
        }

        viewModel.lessonsMutableLiveData.observe(viewLifecycleOwner) { lessons ->
            viewModel.lessons.clear()
            viewModel.lessons.addAll(
                lessons.map { lessonEntity ->
                    val time = viewModel.timesMap[lessonEntity.timeId]
                    ScheduleListLesson(
                        lessonEntity.id,
                        time!!.startHour,
                        time.startMinute,
                        time.endHour,
                        time.endMinute,
                        lessonEntity.type,
                        lessonEntity.cabinet,
                        viewModel.campusesMap[lessonEntity.campusId]!!.codename,
                        lessonEntity.name,
                        lessonEntity.teacher,
                    )
                }
            )
            binding.lessons.adapter?.notifyItemRangeChanged(0, viewModel.lessons.size)
            activity.setLoading(false)
        }

        viewModel.loadDate()
    }

    private fun selectDate() {
        val semester: SemesterEntity = viewModel.semesters.find {
            it.id == viewModel.semesterId
        } ?: viewModel.semesters.last().also {
            viewModel.semesterId = it.id
        }

        val selectedDate = viewModel.getDate()

        var week = viewModel.weeks.find { week ->
            val startDate = SimpleDate(
                week.startDay,
                week.startMonth,
                week.startYear
            )
            val endDate = SimpleDate(
                week.endDay,
                week.endMonth,
                week.endYear
            )
            return@find startDate <= selectedDate && selectedDate <= endDate
        }
        if (week == null) {
            val startSemester = SimpleDate(
                semester.startDay,
                semester.startMonth,
                semester.startYear
            )
            val endSemester = SimpleDate(
                semester.endDay,
                semester.endMonth,
                semester.endYear
            )
            if (selectedDate <= startSemester) {
                week = viewModel.weeks[0]
                viewModel.setDate(startSemester)
            } else {
                week = viewModel.weeks.last()
                viewModel.setDate(endSemester)
            }
        }
        viewModel.week = week
        val weekOrder = viewModel.weeks.indexOf(viewModel.week) + 1
        binding.week.text = getString(
            R.string.schedule_week,
            weekOrder,
            when (weekOrder % 2) {
                0 -> getString(R.string.week_type_even)
                else -> getString(R.string.week_type_uneven)
            }
        )
        binding.week.isVisible = true
        initDays()
    }

    private fun load() {
        activity.setLoading(true)
        val key = getString(R.string.semester_shared_id)
        if (shared.contains(key)) {
            viewModel.semesterId = shared.getInt(key, -1)
        }

        viewModel.loadSemesters()
        viewModel.loadCampuses()
        viewModel.loadTimes()

        binding.semester.text = getString(
            R.string.schedule_semester,
            viewModel.semesterId?.toString() ?: "?"
        )
        binding.week.isVisible = false
    }
}
