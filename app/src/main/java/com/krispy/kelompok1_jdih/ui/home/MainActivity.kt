package com.krispy.kelompok1_jdih.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.krispy.kelompok1_jdih.ui.home.lainnya.OtherFragment
import com.krispy.kelompok1_jdih.R
import com.krispy.kelompok1_jdih.databinding.ActivityMainBinding
import com.krispy.kelompok1_jdih.ui.admin.MainActivityAdm
import com.krispy.kelompok1_jdih.ui.home.dokumen.DokumenDaftarFragment
import com.krispy.kelompok1_jdih.ui.home.dokumen.DokumenTambahActivity

class MainActivity : AppCompatActivity(), HomeFragment.OnFragmentInteractionListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check login status
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)

        if (isLoggedIn) {
            startActivity(Intent(this, MainActivityAdm::class.java))
            finish() // Finish current activity to prevent back navigation to MainActivity
            return
        }

        // Setup bottom navigation and default fragment
        replaceFragment(HomeFragment())
        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home_icon -> replaceFragment(HomeFragment())
                R.id.docs_icon -> replaceFragment(DokumenDaftarFragment())
                R.id.info_icon -> replaceFragment(OtherFragment())
            }
            true
        }
    }

    override fun onFragmentInteraction(fragment: Fragment) {
        replaceFragment(fragment)
        if (fragment is DokumenDaftarFragment) {
            binding.bottomNav.selectedItemId = R.id.docs_icon
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main, fragment)
            .commit()
    }
}
