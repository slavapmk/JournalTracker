package ru.slavapmk.journaltracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ru.slavapmk.journaltracker.databinding.FragmentExportSemesterBinding
import ru.slavapmk.journaltracker.models.ExportSummaryViewModel

class ExportSummaryFragment : Fragment() {
    private lateinit var binding: FragmentExportSemesterBinding
    private val activity: MainActivity by lazy { requireActivity() as MainActivity }
    val viewModel by viewModels<ExportSummaryViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        binding = FragmentExportSemesterBinding.inflate(layoutInflater)
        return binding.root
    }
}