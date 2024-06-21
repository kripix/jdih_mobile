package com.krispy.kelompok1_jdih.ui.home.dokumen

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.krispy.kelompok1_jdih.R
import com.krispy.kelompok1_jdih.data.api.ApiRetrofit
import com.krispy.kelompok1_jdih.data.model.DOKUMEN_DATA_EXTRA
import com.krispy.kelompok1_jdih.data.model.DOKUMEN_ID_EKSTRA
import com.krispy.kelompok1_jdih.data.model.DokumenModel
import com.krispy.kelompok1_jdih.data.model.ResponseModel
import com.krispy.kelompok1_jdih.databinding.ActivityDokumenDetailBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DokumenDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDokumenDetailBinding
    private lateinit var dokumen: DokumenModel.Data
    private val api by lazy { ApiRetrofit().endpoint }

    var status_id : Int = 0
    var filePath : String = ""

    companion object {
        private const val REQUEST_EDIT_DOKUMEN = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDokumenDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupData()
        setupListener()
    }

    private fun setupData(){
        intent.getSerializableExtra(DOKUMEN_DATA_EXTRA)?.let {
            dokumen = it as DokumenModel.Data
            displayData(dokumen)
        } ?: run {
            Toast.makeText(this, "Failed to retrieve class data", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun displayData(dokumen : DokumenModel.Data) {
        with(binding){
            tvTipe.text = dokumen.tipe
            tvJudulDokumen.text = dokumen.judul
            tvNomor.text = dokumen.nomor
            tvSumber.text = dokumen.sumber
            tvPtd.text = dokumen.penandatanganan
            tvPenetapan.text = dokumen.tempat_penetapan
            tvTgl.text = dokumen.tgl_penetapan
            tvStatus.text = dokumen.status
            tvFile.text = dokumen.file
        }

        status_id = dokumen.status_id
        setupStatus()
        filePath = dokumen.url_file
    }

    private fun setupStatus(){
        if (status_id != 0) {
            binding.tvStatus.setBackgroundResource(R.color.blue)
        }
    }

    private fun setupListener() {
        with(binding){
            btnBack.setOnClickListener { finish() }
            btnQr.setOnClickListener { setupQr() }
            btnShare.setOnClickListener { setupShare()}
            btnDownload.setOnClickListener( {setupDownload()})

            // Check login state and show/hide More button
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
                    editDokumen()
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun editDokumen() {
        val intent = Intent(this, DokumenTambahActivity::class.java).apply {
            putExtra(DOKUMEN_ID_EKSTRA, dokumen.id)
            putExtra(DOKUMEN_DATA_EXTRA, dokumen)
        }
        startActivityForResult(intent, REQUEST_EDIT_DOKUMEN)
    }

    private fun showConfirmationDialog() {
        AlertDialog.Builder(this).apply {
            setTitle("Konfirmasi")
            setMessage("Apakah Anda yakin ingin menghapus?")
            setPositiveButton("Ya") { _, _ -> deleteDokumen() }
            setNegativeButton("Tidak", null)
            create()
            show()
        }
    }

    private fun deleteDokumen() {
        val deleteCall = api.delete_dokumen(dokumen.id)

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

    private fun setupDownload() {
        // Implementation for setupDownload
    }

    private fun setupShare(){
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "Check out this document: $filePath")
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    private fun setupQr() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bs_share, null)
        bottomSheetDialog.setContentView(view)

        val qr = view.findViewById<ImageView>(R.id.iv_qr)
        generateQrCode(qr, filePath)
        bottomSheetDialog.show()
    }

    private fun generateQrCode(imageView: ImageView, url: String) {
        val size = 512 // dimensions of the QR code
        val qrCodeWriter = QRCodeWriter()
        try {
            val bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, size, size)
            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
            for (x in 0 until size) {
                for (y in 0 until size) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
                }
            }
            imageView.setImageBitmap(bitmap)
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to generate QR code", Toast.LENGTH_SHORT).show()
        }
    }
}
