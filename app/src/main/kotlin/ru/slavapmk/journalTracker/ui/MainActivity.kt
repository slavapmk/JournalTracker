package ru.slavapmk.journalTracker.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.slavapmk.journalTracker.R
import ru.slavapmk.journalTracker.databinding.ActivityMainBinding
import ru.slavapmk.journalTracker.viewModels.MainActivityViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding


    companion object {
        lateinit var fmanager: FragmentManager
    }

    val viewModel by viewModels<MainActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        fmanager = supportFragmentManager
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 5)
            insets
        }

        findViewById<BottomNavigationView>(R.id.bottom_bar).setupWithNavController(
            (supportFragmentManager.findFragmentById(
                R.id.fragment_container
            ) as NavHostFragment).navController
        )
    }
}