package ru.slavapmk.journalTracker.ui.schedule

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import ru.slavapmk.journalTracker.R
import ru.slavapmk.journalTracker.dataModels.schedule.ScheduleListLesson
import ru.slavapmk.journalTracker.dataModels.selectWeek.Semester
import ru.slavapmk.journalTracker.dataModels.settings.WeeksFormats
import ru.slavapmk.journalTracker.databinding.FragmentScheduleBinding
import ru.slavapmk.journalTracker.storageModels.entities.SemesterEntity
import ru.slavapmk.journalTracker.ui.MainActivity
import ru.slavapmk.journalTracker.ui.SharedKeys
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
            SharedKeys.SHARED_APP_ID, Context.MODE_PRIVATE
        )
    }

    fun dates() = listOf(
        binding.monday,
        binding.tuesday,
        binding.wednesday,
        binding.thursday,
        binding.friday,
        binding.saturday,
        binding.sunday
    )

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

        viewModel.sharedPreferences = shared

        init()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        load()
    }

    private fun updateDays() {
        binding.monday.dayOfWeek.text = getString(R.string.day_monday)
        binding.tuesday.dayOfWeek.text = getString(R.string.day_tuesday)
        binding.wednesday.dayOfWeek.text = getString(R.string.day_wednesday)
        binding.thursday.dayOfWeek.text = getString(R.string.day_thurday)
        binding.friday.dayOfWeek.text = getString(R.string.day_friday)
        binding.saturday.dayOfWeek.text = getString(R.string.day_saturday)
        binding.sunday.dayOfWeek.text = getString(R.string.day_sunday)

        binding.nextButton.isEnabled =
            viewModel.weeks.indexOf(viewModel.week) != viewModel.weeks.size - 1
        binding.previousButton.isEnabled = viewModel.weeks.indexOf(viewModel.week) != 0

        val parseWeek = viewModel.parseWeek()
        val selectedDate: SimpleDate = viewModel.getDate()
        val nowDate = viewModel.nowDate()
        for ((i, date) in dates().withIndex()) {
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
                if (
                    nowDate.day == dayItemDate.day &&
                    nowDate.month - 1 == dayItemDate.month &&
                    nowDate.year == dayItemDate.year
                ) {
                    date.nowDate.visibility = View.VISIBLE
                } else {
                    date.nowDate.visibility = View.GONE
                }
                date.root.setOnClickListener {
                    shared.edit {
                        remove(SharedKeys.WEEK_SHARED_ID)
                    }
                    unselectDates()
                    date.selected.visibility = View.VISIBLE
                    viewModel.setDate(
                        SimpleDate(
                            dayItemDate.day,
                            dayItemDate.month + 1,
                            dayItemDate.year
                        )
                    )
                    activity.setLoading(true)
                    viewModel.loadLessons()
                }
            } else {
                date.root.visibility = View.INVISIBLE
            }
        }
    }

    private fun unselectDates() {
        for (date in dates()) {
            date.selected.visibility = View.GONE
        }
    }

    private fun init() {
        binding.addLessonButton.setOnClickListener {
            activity.startActivity(
                Intent(activity, LessonEditActivity::class.java).apply {

                }
            )
        }

        binding.selectWeek.setOnClickListener {
            val intent = Intent(activity, SelectWeekActivity::class.java)
            activity.startActivity(intent)
        }

        binding.selectSemester.setOnClickListener {
            val intent = Intent(activity, SemestersActivity::class.java)
            activity.startActivity(intent)
        }
        binding.selectSemesterGone.setOnClickListener {
            val intent = Intent(activity, SemestersActivity::class.java)
            activity.startActivity(intent)
        }

        binding.currentDay.setOnClickListener {
            viewModel.setDate(null)
            selectDateAndUpdateDays()
            activity.setLoading(true)
            viewModel.loadLessons()
        }

        binding.dayBefore.setOnClickListener {
            viewModel.setDate(
                viewModel.getDate().minusDays(1)
            )
            selectDateAndUpdateDays()
            activity.setLoading(true)
            viewModel.loadLessons()
        }

        binding.dayNext.setOnClickListener {
            viewModel.setDate(
                viewModel.getDate().plusDays(1)
            )
            selectDateAndUpdateDays()
            activity.setLoading(true)
            viewModel.loadLessons()
        }

        binding.lessons.layoutManager = LinearLayoutManager(requireContext())
        binding.lessons.adapter = ScheduleLessonsAdapter(
            viewModel.lessons
        ) { lesson ->
            startActivity(
                Intent(activity, LessonActivity::class.java).apply {
                    putExtra(SharedKeys.SELECTED_LESSON, lesson.id)
                }
            )
        }

        viewModel.mediatorLiveData.observe(viewLifecycleOwner) { loadData ->
            if (loadData.semesters.isEmpty()) {
                activity.setLoading(false)
                binding.week.isInvisible = false
                checkVisibility()
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
            selectDateAndUpdateDays()

            if (shared.contains(SharedKeys.WEEK_SHARED_ID)) {
                val weekIndex = shared.getInt(SharedKeys.WEEK_SHARED_ID, 0)
                if (weekIndex >= viewModel.weeks.size) {
                    shared.edit {
                        remove(SharedKeys.WEEK_SHARED_ID)
                    }
                } else {
                    viewModel.week = viewModel.weeks[weekIndex]
                    updateWeekTitle()
                    updateDays()
                }
            }

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
            val newList = lessons.map { lessonEntity ->
                val time = viewModel.timesMap[lessonEntity.timeId]
                ScheduleListLesson(
                    lessonEntity.id,
                    viewModel.timesMap.values.indexOf(time),
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
            val diffCallback = LessonsDiffCallback(viewModel.lessons, newList)
            val diffResult = DiffUtil.calculateDiff(diffCallback)

            viewModel.lessons.clear()
            viewModel.lessons.addAll(
                newList
            )
            diffResult.dispatchUpdatesTo(binding.lessons.adapter!!)
            activity.setLoading(false)

            checkVisibility()
        }

        binding.previousButton.setOnClickListener {
            viewModel.week = viewModel.weeks[viewModel.weeks.indexOf(viewModel.week) - 1]
            updateDays()
            updateWeekTitle()
        }

        binding.nextButton.setOnClickListener {
            viewModel.week = viewModel.weeks[viewModel.weeks.indexOf(viewModel.week) + 1]
            updateDays()
            updateWeekTitle()
        }
    }

    private fun checkVisibility() {
        val visibility = viewModel.semesters.isNotEmpty()
        binding.addLessonButton.isVisible = visibility
        binding.semester.isVisible = visibility
        binding.week.isVisible = visibility
        binding.selectWeek.isVisible = visibility
        binding.currentDay.isVisible = visibility
        binding.stroke.isVisible = visibility
        binding.dayBefore.isVisible = visibility
        binding.dayNext.isVisible = visibility
        binding.selectSemester.isVisible = visibility
        binding.selectSemesterGone.isVisible = !visibility
    }

    private fun selectDateAndUpdateDays() {
        selectDate()
        updateWeekTitle()
        updateDays()
    }

    private fun selectDate() {
        if (viewModel.semesters.isEmpty() || viewModel.weeks.isEmpty()) {
            return
        }
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
        binding.week.isInvisible = false
    }

    private fun updateWeekTitle() {
        val weekOrder = viewModel.weeks.indexOf(viewModel.week) + 1
        binding.week.text = when (viewModel.weekTypes) {
            1 -> {
                getString(
                    R.string.schedule_week_single,
                    weekOrder
                )
            }

            else -> {
                getString(
                    R.string.schedule_week,
                    weekOrder,
                    when (weekOrder % 2) {
                        0 -> when (viewModel.weekFormat) {
                            WeeksFormats.EVEN_UNEVEN -> getString(R.string.week_type_even)
                            WeeksFormats.UP_DOWN -> getString(R.string.week_type_up)
                            WeeksFormats.DOWN_UP -> getString(R.string.week_type_down)
                        }

                        else -> when (viewModel.weekFormat) {
                            WeeksFormats.EVEN_UNEVEN -> getString(R.string.week_type_uneven)
                            WeeksFormats.UP_DOWN -> getString(R.string.week_type_down)
                            WeeksFormats.DOWN_UP -> getString(R.string.week_type_up)
                        }
                    }
                )
            }
        }
    }

    private fun load() {
        activity.setLoading(true)
        viewModel.loadDate()

        if (shared.contains(SharedKeys.SEMESTER_ID)) {
            viewModel.semesterId = shared.getInt(SharedKeys.SEMESTER_ID, -1)
        }

        viewModel.loadSemesters()
        viewModel.loadCampuses()
        viewModel.loadTimes()

        binding.semester.text = getString(
            R.string.schedule_semester,
            viewModel.semesterId?.toString() ?: "?"
        )
        binding.week.isInvisible = false
    }
}
