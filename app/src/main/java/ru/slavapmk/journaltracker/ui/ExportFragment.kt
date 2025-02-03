package ru.slavapmk.journaltracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.slavapmk.journaltracker.databinding.FragmentExportBinding

class ExportFragment : Fragment() {
    private lateinit var binding: FragmentExportBinding
    private val activity: MainActivity by lazy { requireActivity() as MainActivity }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        binding = FragmentExportBinding.inflate(layoutInflater)

        val pager = binding.pager

        return binding.root
    }
}