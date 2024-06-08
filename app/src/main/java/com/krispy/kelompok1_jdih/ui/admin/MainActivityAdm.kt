package com.krispy.kelompok1_jdih.ui.admin
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.krispy.kelompok1_jdih.ui.home.MainActivity
import com.krispy.kelompok1_jdih.R
import com.krispy.kelompok1_jdih.databinding.ActivityMainAdmBinding
import com.krispy.kelompok1_jdih.data.room.constant
import com.krispy.kelompok1_jdih.ui.admin.berita.cud.AddNewsActivity
import com.krispy.kelompok1_jdih.ui.admin.dokumen.cud.AddDocsActivity
import com.krispy.kelompok1_jdih.data.util.SharedPrefManager
import com.krispy.kelompok1_jdih.ui.admin.dokumen.cud.DataDocsFragment
import com.krispy.kelompok1_jdih.ui.admin.profile.AccountFragment
import com.krispy.kelompok1_jdih.ui.admin.lainnya.AboutFragment
import com.krispy.kelompok1_jdih.ui.admin.main.DashboardFragment

class MainActivityAdm : AppCompatActivity() {
    private lateinit var binding: ActivityMainAdmBinding


    private lateinit var sharedPrefManager: SharedPrefManager
    private var username: String? = null

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

        sharedPrefManager = SharedPrefManager(this)
        if (!sharedPrefManager.isLoggedIn()) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Optionally finish MainActivity if user isn't logged in
        }

        //bottom navigation
        binding.bottomNavigationView.background = null
        binding.bottomNavigationView.menu.getItem(2).isEnabled = false
        val checkedItem = intArrayOf(-2)

        //fab
        binding.fab.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this)
                .setTitle("Pilih Tindakan") // Set dialog box title (optional)

            val listItems = arrayOf("Tambah Produk Hukum", "Tambah Berita")

            alertDialog.setSingleChoiceItems(listItems, -1) { dialog, which ->
                when (which) {
                    0 -> {
                        // Handle "Tambah Produk Hukum" selection
                        intentEdit(constant.TYPE_CREATE, 0) // Assuming intentEdit is defined elsewhere
                        dialog.dismiss()
                    }
                    1 -> {
                        // Handle "Tambah Berita" selection
                        val intent = Intent(this, AddNewsActivity::class.java)
                        startActivity(intent)
                        dialog.dismiss()
                    }
                    else -> {
                        // Handle unexpected selection (optional)
                        dialog.dismiss()
                    }
                }
            }

            val customAlertDialog = alertDialog.create()
            customAlertDialog.show()
        }

        replaceFragment(DashboardFragment())
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.db_icon -> replaceFragment(DashboardFragment())
                R.id.search_icon -> replaceFragment(DataDocsFragment())
                R.id.account_icon -> replaceFragment(AccountFragment())
                R.id.settings_icon -> replaceFragment(AboutFragment())
                else -> {
                }
            }
            true
        }


        val intent = intent
        val fragmentName = intent.getStringExtra("list")

        if (fragmentName == "Docs") {
            binding.bottomNavigationView.selectedItemId = R.id.search_icon
            replaceFragment(DataDocsFragment())
        }


    }


    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container,fragment)
        fragmentTransaction.commit()
    }

    fun intentEdit(intent_type: Int, idDokumen: Int) {
        val intent = Intent(this, AddDocsActivity::class.java).putExtra("intent_type", intent_type).putExtra("idDokumen", idDokumen)
        startActivity(intent)

    }


}