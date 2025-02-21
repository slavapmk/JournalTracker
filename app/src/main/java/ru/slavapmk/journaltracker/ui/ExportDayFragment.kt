package ru.slavapmk.journaltracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ru.slavapmk.journaltracker.databinding.FragmentExportDayBinding
import ru.slavapmk.journaltracker.viewmodels.ExportDayViewModel

class ExportDayFragment : Fragment() {
    private lateinit var binding: FragmentExportDayBinding
    private val activity: MainActivity by lazy { requireActivity() as MainActivity }
    val viewModel by viewModels<ExportDayViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        binding = FragmentExportDayBinding.inflate(layoutInflater)
        return binding.root
    }
}