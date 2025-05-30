package ru.slavapmk.journalTracker.ui.export

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ru.slavapmk.journalTracker.databinding.FragmentExportDayBinding
import ru.slavapmk.journalTracker.ui.MainActivity
import ru.slavapmk.journalTracker.ui.SharedKeys
import ru.slavapmk.journalTracker.viewModels.ExportDayViewModel

class ExportDayFragment : Fragment() {
    private lateinit var binding: FragmentExportDayBinding
    private val activity: MainActivity by lazy { requireActivity() as MainActivity }
    val viewModel by viewModels<ExportDayViewModel>()

    private val shared: SharedPreferences by lazy {
        activity.getSharedPreferences(
            SharedKeys.SHARED_APP_ID, Context.MODE_PRIVATE
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        binding = FragmentExportDayBinding.inflate(layoutInflater)

        viewModel.shared = shared

        binding.saveExcel.setOnClickListener {
            activity.setLoading(true)
            viewModel.saveExcel(
                requireContext()
            )
        }

        viewModel.savedLiveStatus.observe(viewLifecycleOwner) {
            activity.setLoading(false)
            Toast.makeText(
                requireContext(),
                "Файл успешно создан",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.shareExcel.setOnClickListener {
            activity.setLoading(true)
            viewModel.shareExcel(
                requireContext()
            )
        }

        viewModel.sharedLiveStatus.observe(viewLifecycleOwner) {
            activity.setLoading(false)
            it?.let {
                startActivity(it)
            }
        }

        binding.openExcel.setOnClickListener {
            activity.setLoading(true)
            viewModel.openExcel(
                requireContext()
            )
        }

        viewModel.openLiveStatus.observe(viewLifecycleOwner) {
            activity.setLoading(false)
            it?.let {
                startActivity(it)
            }
        }

        viewModel.statusCallback.observe(viewLifecycleOwner) {
            setStatus(it)
        }

        return binding.root
    }

    private fun setStatus(it: String?) {
        activity.setStatus(it ?: "")
    }
}