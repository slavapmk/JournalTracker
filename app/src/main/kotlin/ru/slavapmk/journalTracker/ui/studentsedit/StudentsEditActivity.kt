package ru.slavapmk.journalTracker.ui.studentsedit

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import ru.slavapmk.journalTracker.R
import ru.slavapmk.journalTracker.databinding.ActivityStudentsEditBinding
import ru.slavapmk.journalTracker.dataModels.studentsEdit.StudentsEditListItem
import ru.slavapmk.journalTracker.storageModels.Dependencies
import ru.slavapmk.journalTracker.viewModels.StudentsEditViewModel
import ru.slavapmk.journalTracker.ui.MainActivity.Companion.fmanager

class StudentsEditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStudentsEditBinding
    val viewModel by viewModels<StudentsEditViewModel>()

    private val studentsEditListAdapter by lazy {
        StudentsEditListAdapter(viewModel.studentsList) { _, student ->
            val indexOf = viewModel.studentsList.indexOf(student)
            val size = viewModel.studentsList.size
            viewModel.studentsList.remove(student)
            val updateCount = size - indexOf
            binding.studentsList.adapter?.notifyItemRemoved(indexOf)
            binding.studentsList.adapter?.notifyItemRangeChanged(
                indexOf,
                updateCount
            )
            viewModel.deleteStudent(student)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        fmanager = supportFragmentManager
        binding = ActivityStudentsEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 5)
            insets
        }

        init()
        load()
    }

    private fun init() {
        binding.studentInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.studentName = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        binding.studentInput.setText(viewModel.studentName)

        binding.studentsList.layoutManager = LinearLayoutManager(this)
        binding.studentsList.adapter = studentsEditListAdapter

        binding.studentField.setEndIconOnClickListener {
            if (binding.studentInput.text.isNullOrEmpty()) {
                return@setEndIconOnClickListener
            }
            addStudentFromInput()
            this.currentFocus?.let { view ->
                val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
        binding.studentInput.setOnEditorActionListener { _, actionId, event ->
            if ((event != null && (event.keyCode == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                addStudentFromInput()
            }
            return@setOnEditorActionListener false
        }

        viewModel.studentsLiveData.observe(this) {
            viewModel.studentsList.clear()
            viewModel.studentsList.addAll(it)
            studentsEditListAdapter.notifyItemRangeChanged(0, it.size)
            binding.loadingStatus.visibility = View.GONE
        }
    }

    private fun load() {
        binding.loadingStatus.visibility = View.VISIBLE
        viewModel.loadStudents()
    }

    private fun addStudentFromInput() {
        val text = binding.studentInput.text?.toString()
        if (text.isNullOrEmpty()) {
            return
        }
        binding.studentInput.text?.clear()
        val insertIndex = viewModel.addStudent(
            StudentsEditListItem(
                null,
                text
            )
        )
        studentsEditListAdapter.notifyItemInserted(insertIndex)
        studentsEditListAdapter.notifyItemRangeChanged(
            insertIndex, viewModel.studentsList.size - insertIndex
        )
    }
}