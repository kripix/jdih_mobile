package com.krispy.kelompok1_jdih.ui.home.berita

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.krispy.kelompok1_jdih.R
import com.krispy.kelompok1_jdih.data.api.ApiRetrofit
import com.krispy.kelompok1_jdih.data.model.BERITA_DATA_EXTRA
import com.krispy.kelompok1_jdih.data.model.BeritaModel
import com.krispy.kelompok1_jdih.data.model.DOKUMEN_DATA_EXTRA
import com.krispy.kelompok1_jdih.data.model.DokumenModel
import com.krispy.kelompok1_jdih.data.model.ResponseModel
import com.krispy.kelompok1_jdih.databinding.ActivityBeritaTambahBinding
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BeritaTambahActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBeritaTambahBinding
    private var berita: BeritaModel.Data? = null
    private val api by lazy { ApiRetrofit().endpoint }

    lateinit var url_cover : String

    companion object {
        const val REQUEST_EDIT_BERITA = 123
        const val BERITA_DATA_UPDATED = "berita_data_updated"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBeritaTambahBinding.inflate(layoutInflater)
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
            btnSimpan.setOnClickListener { saveBerita() }
        }
    }

    private fun setupData() {
        berita = intent.getSerializableExtra(BERITA_DATA_EXTRA) as? BeritaModel.Data
        berita?.let {
            displayData(it)
        }
    }

    private fun displayData(berita: BeritaModel.Data) {
        with(binding) {
            etJudul.setText(berita.judul)
            etIsi.setText(berita.isi)
            url_cover = berita.url_cover
            Log.d("CekDetail", "displayData: ${berita.tgl}")
        }
        Log.d("CekDetail", "displayData: ${berita.url_cover}")
        Picasso.get().load(berita.url_cover).into(binding.pvImg)
    }

    private fun saveBerita() {
        val judul = binding.etJudul.text.toString().trim()
        val isi = binding.etIsi.text.toString().trim()
        if (judul.isEmpty() || isi.isEmpty()) {
            Toast.makeText(this, "Judul dan isi tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }
        val call: Call<ResponseModel> = berita?.let {
            api.update_berita(it.id, judul, isi, url_cover)
        } ?: run {
            api.create_berita(judul, isi, url_cover)
        }

        call.enqueue(object : Callback<ResponseModel> {
            override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                if (response.isSuccessful) {
                    response.body()?.message?.let { message ->
                        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                        val intent = Intent().apply {
                            putExtra(BERITA_DATA_UPDATED, true)
                        }
                        setResult(RESULT_OK, intent)
                        finish()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                Toast.makeText(applicationContext, "Gagal menyimpan document", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_EDIT_BERITA && resultCode == RESULT_OK) {
            if (data?.getBooleanExtra(BERITA_DATA_UPDATED, false) == true) {
                setupData()
            }
        }
    }
}

