package ru.slavapmk.journaltracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ru.slavapmk.journaltracker.R
import ru.slavapmk.journaltracker.databinding.FragmentLessonBinding
import ru.slavapmk.journaltracker.viewmodels.LessonViewModel
import java.util.Calendar

class LessonFragment : Fragment() {
    private lateinit var binding: FragmentLessonBinding
    private val activity: MainActivity by lazy { requireActivity() as MainActivity }
    val viewModel by viewModels<LessonViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        binding = FragmentLessonBinding.inflate(layoutInflater)

        binding.lessonName.text = activity.getString(
            R.string.students_lesson_name,
            viewModel.index,
            viewModel.name
        )

        val instance = Calendar.getInstance()
        instance.time = viewModel.date

        binding.lessonDate.text = activity.getString(
            R.string.students_lesson_date,
            instance.get(Calendar.DAY_OF_MONTH),
            instance.get(Calendar.MONTH),
            instance.get(Calendar.YEAR)
        )

        binding.lessonTeacher.text = viewModel.teacher

        return binding.root
    }
}