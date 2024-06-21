package com.krispy.kelompok1_jdih.ui.admin
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.krispy.kelompok1_jdih.R
import com.krispy.kelompok1_jdih.databinding.ActivityMainAdmBinding
import com.krispy.kelompok1_jdih.ui.home.berita.BeritaTambahActivity
import com.krispy.kelompok1_jdih.ui.home.dokumen.DokumenTambahActivity
import com.krispy.kelompok1_jdih.ui.admin.profile.AccountFragment
import com.krispy.kelompok1_jdih.ui.home.dokumen.DokumenDaftarFragment
import com.krispy.kelompok1_jdih.ui.home.lainnya.OtherFragment

class MainActivityAdm : AppCompatActivity(), DashboardFragment.OnFragmentInteractionListener {
    private lateinit var binding: ActivityMainAdmBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainAdmBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.bottomNavigationView.background = null

        binding.fab.setOnClickListener {
            setupTambah()
        }

        replaceFragment(DashboardFragment())
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.db_icon -> replaceFragment(DashboardFragment())
                R.id.search_icon -> replaceFragment(DokumenDaftarFragment())
                R.id.account_icon -> replaceFragment(AccountFragment())
                R.id.settings_icon -> replaceFragment(OtherFragment())
                else -> {}
            }
            true
        }
    }

    private fun setupTambah() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bs_tambah, null)
        bottomSheetDialog.setContentView(view)
        val tambahDokumen = view.findViewById<Button>(R.id.btn_tambah_dokumen)
        val tambahBerita = view.findViewById<Button>(R.id.btn_tambah_berita)

        tambahDokumen.setOnClickListener {
            val intent = Intent(this, DokumenTambahActivity::class.java)
            startActivity(intent)
        }
        tambahBerita.setOnClickListener{
            val intent = Intent(this, BeritaTambahActivity::class.java)
            startActivity(intent)
        }

        bottomSheetDialog.show()
    }

    override fun onFragmentInteraction(fragment: Fragment) {
        replaceFragment(fragment)
        if (fragment is DokumenDaftarFragment) {
            binding.bottomNavigationView.selectedItemId = R.id.search_icon
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .addToBackStack(null)
            .commit()
    }
}
