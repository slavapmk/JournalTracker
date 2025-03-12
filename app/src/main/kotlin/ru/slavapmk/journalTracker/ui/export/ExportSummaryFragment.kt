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
import ru.slavapmk.journalTracker.databinding.FragmentExportSemesterBinding
import ru.slavapmk.journalTracker.ui.MainActivity
import ru.slavapmk.journalTracker.ui.SharedKeys
import ru.slavapmk.journalTracker.viewModels.ExportSummaryViewModel

class ExportSummaryFragment : Fragment() {
    private lateinit var binding: FragmentExportSemesterBinding
    private val activity: MainActivity by lazy { requireActivity() as MainActivity }
    val viewModel by viewModels<ExportSummaryViewModel>()

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
        binding = FragmentExportSemesterBinding.inflate(layoutInflater)

        viewModel.shared = shared

        binding.saveExcel.setOnClickListener {
            activity.setLoading(true)
            viewModel.saveExcel(
                requireContext()
            )
        }

        viewModel.savedLiveStatus.observe(viewLifecycleOwner) {
            activity.setLoading(false)
            setStatus(null)
            Toast.makeText(
                requireContext(),
                "Файл успешно создан",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.shareExcel.setOnClickListener {
            activity.setLoading(true)
            setStatus(null)
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
            setStatus(null)
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