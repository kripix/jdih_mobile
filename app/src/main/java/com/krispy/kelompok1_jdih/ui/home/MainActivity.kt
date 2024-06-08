package com.krispy.kelompok1_jdih.ui.home

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.krispy.kelompok1_jdih.ui.home.lainnya.OtherFragment
import com.krispy.kelompok1_jdih.R
import com.krispy.kelompok1_jdih.databinding.ActivityMainBinding
import com.krispy.kelompok1_jdih.ui.home.dokumen.DocsFragment
import com.krispy.kelompok1_jdih.ui.home.main.HomeFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        replaceFragment(HomeFragment())
        binding.bottomNav.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.home_icon -> replaceFragment(HomeFragment())
                R.id.docs_icon -> replaceFragment(DocsFragment())
                R.id.info_icon -> replaceFragment(OtherFragment())
                else -> {
                }
            }
            true
        }


        val intent = intent
        val fragmentName = intent.getStringExtra("list")

        if (fragmentName == "Docs") {
            binding.bottomNav.selectedItemId = R.id.docs_icon
            replaceFragment(DocsFragment())
        }

    }


    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main,fragment)
        fragmentTransaction.commit()
    }
}
