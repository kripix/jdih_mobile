package com.krispy.kelompok1_jdih.ui.home.berita

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.krispy.kelompok1_jdih.R
import com.krispy.kelompok1_jdih.data.api.ApiRetrofit
import com.krispy.kelompok1_jdih.data.model.BERITA_DATA_EXTRA
import com.krispy.kelompok1_jdih.data.model.BERITA_ID_EXTRA
import com.krispy.kelompok1_jdih.data.model.BeritaModel
import com.krispy.kelompok1_jdih.data.model.ResponseModel
import com.krispy.kelompok1_jdih.databinding.ActivityBeritaDetailBinding
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BeritaDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBeritaDetailBinding
    private lateinit var berita: BeritaModel.Data
    private val api by lazy { ApiRetrofit().endpoint }

    companion object {
        private const val REQUEST_EDIT_BERITA = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBeritaDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupData()
        setupListener()
    }

    private fun setupListener() {

        with(binding) {
            btnBack.setOnClickListener { finish() }

            val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)
            if (isLoggedIn) {
                btnMore.visibility = View.VISIBLE
                btnMore.setOnClickListener { showPopMenu() }
            } else {
                btnMore.visibility = View.GONE
            }
        }
    }

    private fun showPopMenu() {
        val popupMenu = PopupMenu(this, binding.btnMore)
        popupMenu.menuInflater.inflate(R.menu.pop_up_menu_item, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.hapus -> {
                    showConfirmationDialog()
                    true
                }
                R.id.ubah -> {
                    editBerita()
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun editBerita() {
        val intent = Intent(this, BeritaTambahActivity::class.java).apply {
            putExtra(BERITA_ID_EXTRA, berita.id)
            putExtra(BERITA_DATA_EXTRA, berita)
        }
        startActivityForResult(intent, REQUEST_EDIT_BERITA)
    }

    private fun showConfirmationDialog() {
        AlertDialog.Builder(this).apply {
            setTitle("Konfirmasi")
            setMessage("Apakah Anda yakin ingin menghapus?")
            setPositiveButton("Ya") { _, _ -> deleteBerita() }
            setNegativeButton("Tidak", null)
            create()
            show()
        }
    }

    private fun deleteBerita() {
        val deleteCall = api.delete_berita(berita.id)
        deleteCall.enqueue(object : Callback<ResponseModel> {
            override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                if (response.isSuccessful) {
                    response.body()?.message?.let { message ->
                        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                // Handle failure here if needed
            }
        })
    }

    private fun setupData() {
        intent.getSerializableExtra(BERITA_DATA_EXTRA)?.let {
            berita = it as BeritaModel.Data
            displayData(berita)
        } ?: run {
            Toast.makeText(this, "Failed to retrieve class data", Toast.LENGTH_SHORT).show()

        }

    }

    private fun displayData(berita: BeritaModel.Data) {
        with(binding) {
            tvTgl.text = berita.tgl
            tvJudul.text = berita.judul
            tvIsi.text = berita.isi
            Log.d("CekDetail", "displayData: ${berita.tgl}")
        }
        Log.d("CekDetail", "displayData: ${berita.url_cover}")
        Picasso.get().load(berita.url_cover).into(binding.ivCover)
    }


}