package ru.slavapmk.journalTracker.ui.export

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ru.slavapmk.journalTracker.databinding.FragmentExportDayBinding
import ru.slavapmk.journalTracker.ui.MainActivity
import ru.slavapmk.journalTracker.viewModels.ExportDayViewModel

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

        binding.saveExcel.setOnClickListener {
            activity.setLoading(true)
            viewModel.saveExcel(requireContext())
        }

        viewModel.savedLiveStatus.observe(viewLifecycleOwner) {
            activity.setLoading(false)
            Toast.makeText(
                requireContext(),
                "Файл успешно создан",
                Toast.LENGTH_SHORT
            ).show()
        }

        return binding.root
    }
}