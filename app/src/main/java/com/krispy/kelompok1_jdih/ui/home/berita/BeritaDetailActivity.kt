package com.krispy.kelompok1_jdih.ui.home.berita

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import android.widget.SeekBar
import android.widget.TextView
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
        private const val BERITA_DATA_UPDATED = "berita_data_updated"
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
            btnFzise.setOnClickListener { showFontSizeDialog() }
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
            loadSavedFontSize()
        } ?: run {
            Toast.makeText(this, "Failed to retrieve class data", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadSavedFontSize() {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val savedFontSize = sharedPreferences.getInt("font_size", 1) // Default to medium
        applyFontSize(savedFontSize)
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

    private fun showFontSizeDialog() {
        val dialogView = layoutInflater.inflate(R.layout.sb_font, null)
        val seekBarFontSize = dialogView.findViewById<SeekBar>(R.id.seekBarFontSize)
        val tvFontSizeLabel = dialogView.findViewById<TextView>(R.id.tvFontSizeLabel)

        // Load saved font size preference
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val savedFontSize = sharedPreferences.getInt("font_size", 1) // Default to medium
        seekBarFontSize.progress = savedFontSize

        updateFontSizeLabel(tvFontSizeLabel, savedFontSize)

        seekBarFontSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateFontSizeLabel(tvFontSizeLabel, progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        AlertDialog.Builder(this)
            .setTitle("Select Font Size")
            .setView(dialogView)
            .setPositiveButton("OK") { dialog, which ->
                val selectedFontSize = seekBarFontSize.progress
                saveFontSizePreference(selectedFontSize)
                applyFontSize(selectedFontSize)
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    private fun updateFontSizeLabel(label: TextView, fontSize: Int) {
        val sizeText = when (fontSize) {
            0 -> "Small"
            1 -> "Medium"
            2 -> "Large"
            else -> "Medium"
        }
        label.text = "Font Size: $sizeText"
    }

    private fun saveFontSizePreference(fontSize: Int) {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("font_size", fontSize)
        editor.apply()
    }

    private fun applyFontSize(fontSize: Int) {
        val sizeInSp = when (fontSize) {
            0 -> 12f // Small
            1 -> 16f // Medium
            2 -> 20f // Large
            else -> 16f // Default to Medium
        }
        binding.tvJudul.textSize = sizeInSp + 2
        binding.tvIsi.textSize = sizeInSp
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_EDIT_BERITA && resultCode == Activity.RESULT_OK) {
            if (data?.getBooleanExtra(BERITA_DATA_UPDATED, false) == true) {
                Log.d("CekDetail",  BERITA_DATA_UPDATED)
                setupData() // Memuat ulang data berita setelah berhasil diupdate
            }
        }
    }






}