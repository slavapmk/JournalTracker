package ru.slavapmk.journalTracker.ui.campusEdit

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import ru.slavapmk.journalTracker.R
import ru.slavapmk.journalTracker.dataModels.campuses.Campus
import ru.slavapmk.journalTracker.databinding.ActivityCampusEditBinding
import ru.slavapmk.journalTracker.ui.DeleteDialog
import ru.slavapmk.journalTracker.ui.MainActivity.Companion.fmanager
import ru.slavapmk.journalTracker.viewModels.CampusEditViewModel

class CampusEditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCampusEditBinding
    val viewModel by viewModels<CampusEditViewModel>()
    private val campusesAdapter by lazy {
        CampusesAdapter(viewModel.campuses) { campus ->
            DeleteDialog {
                val indexOf = viewModel.campuses.indexOf(campus)
                val size = viewModel.campuses.size
                viewModel.deleteCampus(campus)
                binding.campuses.adapter?.notifyItemRemoved(indexOf)
                binding.campuses.adapter?.notifyItemRangeChanged(
                    indexOf, size - indexOf
                )
            }.show(
                supportFragmentManager.beginTransaction(),
                "delete_lessons_dialog"
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
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        init()
    }

    override fun onResume() {
        super.onResume()
        setLoading(true)
        viewModel.loadCampuses()
    }

    private fun init() {
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
            viewModel.addCampus(
                Campus(
                    0,
                    viewModel.codename,
                    viewModel.name
                )
            )
            binding.campuses.adapter?.notifyItemInserted(viewModel.campuses.size - 1)
            binding.nameInput.text?.clear()
            binding.nameInput.clearFocus()
            binding.codenameInput.text?.clear()
            binding.codenameInput.clearFocus()
            viewModel.name = ""
            viewModel.codename = ""
        }
        binding.codenameInput.setText(viewModel.codename)
        binding.nameInput.setText(viewModel.name)

        binding.campuses.layoutManager = LinearLayoutManager(this)
        binding.campuses.adapter = campusesAdapter

        viewModel.campusesLiveData.observe(this) {
            setLoading(false)

            viewModel.campuses.clear()
            viewModel.campuses.addAll(it)
            binding.campuses.adapter?.notifyItemRangeChanged(
                0, it.size
            )
        }

        viewModel.campusUpdateLiveData.observe(this) { (old, new) ->
            val indexOf = viewModel.campuses.indexOf(old)
            viewModel.campuses[indexOf] = new
            binding.campuses.adapter?.notifyItemChanged(indexOf)
        }
    }

    fun setLoading(loading: Boolean) {
        binding.loadingStatus.isVisible = loading
    }
}