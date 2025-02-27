package ru.slavapmk.journalTracker.ui.campusEdit

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import ru.slavapmk.journalTracker.R
import ru.slavapmk.journalTracker.dataModels.campuses.Campus
import ru.slavapmk.journalTracker.databinding.ActivityCampusEditBinding
import ru.slavapmk.journalTracker.viewModels.CampusEditViewModel
import ru.slavapmk.journalTracker.ui.MainActivity.Companion.fmanager

class CampusEditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCampusEditBinding
    val viewModel by viewModels<CampusEditViewModel>()
    private val campusesAdapter by lazy {
        CampusesAdapter(viewModel.campuses) {
            val indexOf = viewModel.campuses.indexOf(it)
            val size = viewModel.campuses.size
            viewModel.campuses.remove(it)
            binding.campuses.adapter?.notifyItemRemoved(indexOf)
            binding.campuses.adapter?.notifyItemRangeChanged(
                indexOf, size - indexOf
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        fmanager = supportFragmentManager
        binding = ActivityCampusEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 5)
            insets
        }

        binding.codenameInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.codename = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        binding.nameInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.name = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        binding.addButton.setOnClickListener {
            if (viewModel.codename == "" || viewModel.name == "") {
                return@setOnClickListener
            }
            viewModel.campuses.add(
                Campus(
                    viewModel.codename,
                    viewModel.name
                )
            )
            binding.campuses.adapter?.notifyItemInserted(viewModel.campuses.size - 1)
        }
        binding.codenameInput.setText(viewModel.codename)
        binding.nameInput.setText(viewModel.name)

        binding.campuses.layoutManager = LinearLayoutManager(this)
        binding.campuses.adapter = campusesAdapter
    }
}