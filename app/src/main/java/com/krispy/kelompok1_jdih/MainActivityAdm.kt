package com.krispy.kelompok1_jdih
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.krispy.kelompok1_jdih.databinding.ActivityMainAdmBinding
import com.krispy.kelompok1_jdih.room.constant

class MainActivityAdm : AppCompatActivity() {
    private lateinit var binding: ActivityMainAdmBinding
    lateinit var db: SQLiteDatabase

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

        db = DBOpenHelper(this).writableDatabase

        //bottom navigation
        binding.bottomNavigationView.background = null
        binding.bottomNavigationView.menu.getItem(2).isEnabled = false
        val checkedItem = intArrayOf(-2)

        //fab
        binding.fab.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this)
            val listItems = arrayOf("Tambah Produk Hukum", "Tambah Berita")
            fun navigateToActivity(position: Int) {
                val intent = when (position) {
                    0 -> Intent(this, AddDocsActivity::class.java)
                    1 -> Intent(this, AddNewsActivity::class.java)
                    else -> throw IllegalArgumentException("Invalid option selected")
                }
                startActivity(intent)
            }
            alertDialog.setSingleChoiceItems(listItems, checkedItem[0]) { dialog, which ->
                checkedItem[0] = which
                navigateToActivity(which)
                dialog.dismiss()
                checkedItem[0] = -2
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

    fun getDbObject():SQLiteDatabase{
        return db
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