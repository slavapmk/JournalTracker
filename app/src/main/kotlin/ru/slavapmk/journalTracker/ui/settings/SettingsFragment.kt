package ru.slavapmk.journalTracker.ui.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ru.slavapmk.journalTracker.R
import ru.slavapmk.journalTracker.dataModels.settings.AttendanceFormats
import ru.slavapmk.journalTracker.dataModels.settings.WeeksFormats
import ru.slavapmk.journalTracker.databinding.FragmentSettingsBinding
import ru.slavapmk.journalTracker.storageModels.StorageDependencies
import ru.slavapmk.journalTracker.storageModels.StorageDependencies.DB_NAME
import ru.slavapmk.journalTracker.ui.MainActivity
import ru.slavapmk.journalTracker.ui.SharedKeys
import ru.slavapmk.journalTracker.ui.campusEdit.CampusEditActivity
import ru.slavapmk.journalTracker.ui.studentsedit.StudentsEditActivity
import ru.slavapmk.journalTracker.ui.timeEdit.TimeEditActivity
import ru.slavapmk.journalTracker.viewModels.SettingsViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private val activity: MainActivity by lazy { requireActivity() as MainActivity }
    val viewModel by viewModels<SettingsViewModel>()

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
        binding = FragmentSettingsBinding.inflate(layoutInflater)

        viewModel.sharedPreferences = shared
        viewModel.importStatusCallback.observe(viewLifecycleOwner) {
            activity.setStatus(it)
        }
        viewModel.importDone.observe(viewLifecycleOwner) {
            activity.setLoading(false)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        init()
    }

    private val dbPickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                importDatabase(uri)
            }
        }
    }

    private val excelPickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                importExcel(uri)
            }
        }
    }

    private fun importDatabase(uri: Uri) {
        val dbName = DB_NAME
        val dbPath = requireContext().getDatabasePath(dbName)

        StorageDependencies.closeDb()

        try {
            requireContext().contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(dbPath).use { output ->
                    val aa = input.copyTo(output)
                    println(aa)
                }
            }
            Toast.makeText(requireContext(), R.string.db_import_success, Toast.LENGTH_LONG).show()
            Log.d("Import", "DB imported: ${dbPath.absolutePath}")

            restartApplication()
        } catch (e: IOException) {
            Toast.makeText(requireContext(), R.string.db_import_error, Toast.LENGTH_LONG).show()
            Log.e("Import", "DB import error", e)
        }
    }

    private fun importExcel(uri: Uri) {
        copyExcelToTemp(uri)?.let { file ->
            activity.setLoading(true)
            viewModel.importExcel(file, requireContext())
        }
    }

    private fun copyExcelToTemp(uri: Uri): File? {
        val tempFile = File.createTempFile(
            "imported_excel", ".xlsx", requireContext().cacheDir
        )
        return try {
            requireContext().contentResolver.openInputStream(uri)?.use { inputStream ->
                tempFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            tempFile
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun restartApplication() {
        val intent = requireContext().packageManager.getLaunchIntentForPackage(
            requireContext().packageName
        )
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            requireContext().startActivity(intent)
        }
        Runtime.getRuntime().exit(0)
    }


    private fun init() {
        binding.dbExport.setOnClickListener {
            val calendar: Calendar = GregorianCalendar.getInstance().apply {
                time = Date()
            }
            val dbPath = context?.getDatabasePath(DB_NAME)
            val backupName = getString(
                R.string.export_filename,
                calendar[Calendar.YEAR],
                calendar[Calendar.MONTH] + 1,
                calendar[Calendar.DAY_OF_MONTH],
                calendar[Calendar.HOUR_OF_DAY],
                calendar[Calendar.MINUTE]
            )
            val backupPath = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                backupName
            )

            try {
                dbPath?.inputStream().use { input ->
                    backupPath.outputStream().use { output ->
                        input?.copyTo(output)
                    }
                }
                Toast.makeText(
                    requireContext(),
                    R.string.db_saved,
                    Toast.LENGTH_LONG
                ).show()
                Log.d("Backup", "Saved BD ${backupPath.absolutePath}")
            } catch (e: IOException) {
                Toast.makeText(
                    requireContext(),
                    R.string.db_save_error,
                    Toast.LENGTH_LONG
                ).show()
                Log.e("Backup", "Error export BD", e)
            }
        }

        binding.dbImport.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/octet-stream"
            }
            dbPickerLauncher.launch(intent)
        }

        binding.tableImport.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            }
            excelPickerLauncher.launch(intent)
        }

        binding.lessonsButton.setOnClickListener {
            val intent = Intent(activity, TimeEditActivity::class.java)
            activity.startActivity(intent)
        }

        binding.campusButton.setOnClickListener {
            val intent = Intent(activity, CampusEditActivity::class.java)
            activity.startActivity(intent)
        }

        binding.studentsButton.setOnClickListener {
            val intent = Intent(activity, StudentsEditActivity::class.java)
            activity.startActivity(intent)
        }

        binding.groupInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.groupName = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        binding.groupInput.setText(viewModel.groupName)


        binding.weeksInput.setText(
            getString(
                R.string.settings_weeks_types, viewModel.weekTypes
            )
        )
        updateFormatVisiblity()
        val weeksCountTypes = listOf("1", "2")
        val weeksCountAdapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_dropdown_item_1line, weeksCountTypes
        )
        binding.weeksInput.setAdapter(weeksCountAdapter)
        binding.weeksInput.setOnItemClickListener { _, _, position, _ ->
            viewModel.weekTypes = position + 1
            updateFormatVisiblity()
        }

        val weeksFormatTypes = listOf(
            getString(R.string.week_format_type_even_uneven),
            getString(R.string.week_format_type_up_down),
            getString(R.string.week_format_type_down_up)
        )
        binding.weeksFormatInput.setText(
            when (viewModel.weekFormat) {
                WeeksFormats.EVEN_UNEVEN -> weeksFormatTypes[0]
                WeeksFormats.UP_DOWN -> weeksFormatTypes[1]
                WeeksFormats.DOWN_UP -> weeksFormatTypes[2]
                null -> ""
            }
        )
        val weeksAdapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_dropdown_item_1line, weeksFormatTypes
        )
        binding.weeksFormatInput.setAdapter(weeksAdapter)
        binding.weeksFormatInput.setOnItemClickListener { _, _, position, _ ->
            viewModel.weekFormat = when (position) {
                0 -> WeeksFormats.EVEN_UNEVEN
                1 -> WeeksFormats.UP_DOWN
                2 -> WeeksFormats.DOWN_UP
                else -> throw IllegalStateException()
            }
        }

        val attendanceFormatTypes = listOf(
            getString(R.string.attendance_format_type_plus_minus),
            getString(R.string.attendance_format_type_skip_hours)
        )
        binding.studentsFormatInput.setText(
            when (viewModel.attendanceFormat) {
                AttendanceFormats.PLUS_MINUS -> attendanceFormatTypes[0]
                AttendanceFormats.SKIP_HOURS -> attendanceFormatTypes[1]
                null -> ""
            }
        )
        val attendanceAdapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_dropdown_item_1line, attendanceFormatTypes
        )
        val attendanceFormatInput = binding.studentsFormatInput
        attendanceFormatInput.setAdapter(attendanceAdapter)
        attendanceFormatInput.setOnItemClickListener { _, _, position, _ ->
            viewModel.attendanceFormat = when (position) {
                0 -> AttendanceFormats.PLUS_MINUS
                1 -> AttendanceFormats.SKIP_HOURS
                else -> throw IllegalStateException()
            }
        }
    }

    private fun updateFormatVisiblity() {
        binding.weeksFormatField.isVisible = when (viewModel.weekTypes) {
            1 -> false
            2 -> true
            else -> throw IllegalStateException()
        }
    }
}