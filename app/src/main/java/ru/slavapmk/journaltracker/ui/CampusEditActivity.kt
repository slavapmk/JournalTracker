package ru.slavapmk.journaltracker.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.ui.AppBarConfiguration
import ru.slavapmk.journaltracker.R
import ru.slavapmk.journaltracker.databinding.ActivityCampusEditBinding
import ru.slavapmk.journaltracker.databinding.ActivityEditLessonBinding
import ru.slavapmk.journaltracker.databinding.ActivityMainBinding
import ru.slavapmk.journaltracker.ui.MainActivity.Companion.fmanager

class CampusEditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCampusEditBinding

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
    }
}