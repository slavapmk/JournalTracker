package ru.slavapmk.journaltracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ru.slavapmk.journaltracker.databinding.FragmentExportWeekBinding
import ru.slavapmk.journaltracker.viewModels.ExportWeekViewModel

class ExportWeekFragment : Fragment() {
    private lateinit var binding: FragmentExportWeekBinding
    private val activity: MainActivity by lazy { requireActivity() as MainActivity }
    val viewModel by viewModels<ExportWeekViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        binding = FragmentExportWeekBinding.inflate(layoutInflater)
        return binding.root
    }
}