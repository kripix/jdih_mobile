package com.krispy.kelompok1_jdih.ui.home.dokumen

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.krispy.kelompok1_jdih.data.api.ApiRetrofit
import com.krispy.kelompok1_jdih.data.model.DOKUMEN_DATA_EXTRA
import com.krispy.kelompok1_jdih.data.model.DokumenModel
import com.krispy.kelompok1_jdih.data.model.ResponseModel
import com.krispy.kelompok1_jdih.databinding.ActivityDokumenTambahBinding
import com.krispy.kelompok1_jdih.ui.home.berita.BeritaTambahActivity.Companion.BERITA_DATA_UPDATED
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class DokumenTambahActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDokumenTambahBinding
    private var dokumen: DokumenModel.Data? = null

    private lateinit var spinner: Spinner
    private lateinit var rgStatus: RadioGroup
    private lateinit var rbBerlaku: RadioButton
    private lateinit var rbTidakBerlaku: RadioButton
    private lateinit var rgSifat: RadioGroup
    private lateinit var rbPublic: RadioButton
    private lateinit var rbPrivate: RadioButton

    private lateinit var judul: String
    private lateinit var nomor: String
    private lateinit var sumber: String
    private lateinit var penandatanganan: String
    private lateinit var tempatPenetapan: String
    private lateinit var tglPenetapan: String
    private lateinit var urlFile: String
    private lateinit var namaFile:String
    private var statusId: Int = 0
    private var sifatId: Int = 0
    private var tipe_id: Int = 0

    private var selectedDate: String? = null
    private var selectedFile: Uri? = null

    private val api by lazy { ApiRetrofit().endpoint }

    companion object {
        const val REQUEST_EDIT_DOKUMEN = 133
        const val DOKUMEN_DATA_UPDATED = "dokumen_data_updated"
        const val REQUEST_PICK_PDF_FILE = 134
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDokumenTambahBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupData()
        setupListener()
    }

    private fun setupData() {
        dokumen = intent.getSerializableExtra(DOKUMEN_DATA_EXTRA) as? DokumenModel.Data
        dokumen?.let {
            displayData(it)
        }
    }

    private fun displayData(dokumen: DokumenModel.Data) {
        with(binding) {
            etJudul.setText(dokumen.judul)
            etNomor.setText(dokumen.nomor)
            etSumber.setText(dokumen.sumber)
            etPenandatanganan.setText(dokumen.penandatanganan)
            etTempatPenetapan.setText(dokumen.tempat_penetapan)
            tvTglPenetapan.text = dokumen.tgl_penetapan
            tvFile.text = dokumen.file
            titleBar.text = "Edit Dokumen"
            selectedDate = dokumen.tgl_penetapan
            namaFile = dokumen.file
            tipe_id = dokumen.tipe_id - 1
            if (dokumen.status_id == 1) rbBerlaku.isChecked = true else rbTidakBerlaku.isChecked = true
            if (dokumen.sifat_id == 1) rbPublic.isChecked = true else rbPrivate.isChecked = true
        }
    }

    private fun setupListener() {
        with(binding) {
            btnBack.setOnClickListener { finish() }
            btnTgl.setOnClickListener { showDatePicker() }
            btnFile.setOnClickListener { showFilePicker() }
            btnSimpan.setOnClickListener { saveData() }
        }

        spinner = binding.spTipe
        spinnerSetup()

        rgStatus = binding.rgStatus
        rbBerlaku = binding.rbBerlaku
        rbTidakBerlaku = binding.rbTidakBerlaku
        rgSifat = binding.rgSifat
        rbPublic = binding.rbPublic
        rbPrivate = binding.rbPrivate
    }

    private fun spinnerSetup() {
        lifecycleScope.launch {
            try {
                val dataList = api.getTipe().tipe
                val adapter = ArrayAdapter(
                    this@DokumenTambahActivity,
                    android.R.layout.simple_spinner_item,
                    dataList.map { it.tipe_dokumen }
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter

                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: android.view.View, position: Int, id: Long) {
                        tipe_id = dataList[position].id
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // Handle case where nothing is selected if needed
                    }
                }
                spinner.setSelection(tipe_id)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@DokumenTambahActivity, "Failed to load data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(this)
        datePickerDialog.setOnDateSetListener { _, year, month, dayOfMonth ->
            selectedDate = "$year-${month + 1}-$dayOfMonth" // Simpan tanggal dalam format YYYY-MM-DD
            binding.tvTglPenetapan.text = "$dayOfMonth/${month + 1}/$year" // Tampilkan tanggal di TextView
        }
        datePickerDialog.show()
    }

    private fun showFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/pdf"
        startActivityForResult(intent, REQUEST_PICK_PDF_FILE)
    }

    private fun getFileFromUri(uri: Uri): File? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val tempFile = File.createTempFile("upload", ".pdf", cacheDir)
            inputStream?.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }



    private fun saveData() {
        judul = binding.etJudul.text.toString().trim()
        nomor = binding.etNomor.text.toString().trim()
        sumber = binding.etSumber.text.toString().trim()
        penandatanganan = binding.etPenandatanganan.text.toString().trim()
        tempatPenetapan = binding.etTempatPenetapan.text.toString().trim()
        tglPenetapan = selectedDate ?: binding.tvTglPenetapan.text.toString().trim()
        statusId = if (rbBerlaku.isChecked) 1 else 0
        sifatId = if (rbPublic.isChecked) 1 else 0
        urlFile = selectedFile?.toString() ?: namaFile

        if (judul.isEmpty() || tglPenetapan.isEmpty() || tempatPenetapan.isEmpty()) {
            Toast.makeText(this, "Data tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        val dokumenId = dokumen?.id
        if (dokumenId != null) {
            showConfirmationDialog(true) // Update existing dokumen
        } else {
            showConfirmationDialog(false) // Create new dokumen
        }
    }


    private fun showConfirmationDialog(isUpdate: Boolean) {
        val message = if (isUpdate) "Apakah Anda yakin ingin mengupdate berita ini?" else "Apakah Anda yakin ingin menyimpan berita ini?"

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Konfirmasi")
            .setMessage(message)
            .setPositiveButton("Ya") { _, _ ->
                if (isUpdate) {
                    updateDokumen()
                } else {
                    createDokumen()
                }
            }
            .setNegativeButton("Tidak", null)
            .show()
    }

    private fun updateDokumen(){
        val isLocalFile = urlFile.startsWith("/storage") || urlFile.startsWith("/data")
        var filePart: MultipartBody.Part? = null
        if (isLocalFile) {
            if (urlFile.isNotEmpty() && File(urlFile).exists()) {
                val file = File(urlFile)
                val requestFile = RequestBody.create("application/pdf".toMediaTypeOrNull(), file)
                filePart = MultipartBody.Part.createFormData("foto", file.name, requestFile)
            } else {
                Toast.makeText(this, "File gambar tidak ditemukan: $urlFile", Toast.LENGTH_SHORT).show()
                return
            }
        }

        Log.d("SaveChanges", "After checking file path: $urlFile")

        val call = api.update_dokumen(
            RequestBody.create("text/plain".toMediaTypeOrNull(), dokumen?.id.toString()),
            RequestBody.create("text/plain".toMediaTypeOrNull(), judul),
            RequestBody.create("text/plain".toMediaTypeOrNull(), nomor),
            RequestBody.create("text/plain".toMediaTypeOrNull(), sumber),
            RequestBody.create("text/plain".toMediaTypeOrNull(), penandatanganan),
            RequestBody.create("text/plain".toMediaTypeOrNull(), tempatPenetapan),
            RequestBody.create("text/plain".toMediaTypeOrNull(), tglPenetapan),
            RequestBody.create("text/plain".toMediaTypeOrNull(), tipe_id.toString()),
            RequestBody.create("text/plain".toMediaTypeOrNull(), statusId.toString()),
            RequestBody.create("text/plain".toMediaTypeOrNull(), sifatId.toString()),
            RequestBody.create("text/plain".toMediaTypeOrNull(), urlFile),
            filePart
        )

        call.enqueue(object: Callback<ResponseModel> {
            override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@DokumenTambahActivity, "Data saved successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent()
                    intent.putExtra(DOKUMEN_DATA_EXTRA, dokumen) // dokumen di sini adalah dokumen yang telah diperbarui
                    intent.putExtra(DOKUMEN_DATA_UPDATED, true)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                } else {
                    Toast.makeText(this@DokumenTambahActivity, "Failed to save data", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                t.printStackTrace()
                Toast.makeText(this@DokumenTambahActivity, "Failed to save data", Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun createDokumen(){

    }



    private fun uploadData() {
        val file = selectedFile?.let { getFileFromUri(it) }
        if (file == null) {
            Toast.makeText(this, "Failed to get file", Toast.LENGTH_SHORT).show()
            return
        }

        val judulBody = RequestBody.create("text/plain".toMediaTypeOrNull(), judul)
        val nomorBody = RequestBody.create("text/plain".toMediaTypeOrNull(), nomor)
        val sumberBody = RequestBody.create("text/plain".toMediaTypeOrNull(), sumber)
        val penandatangananBody = RequestBody.create("text/plain".toMediaTypeOrNull(), penandatanganan)
        val tempatPenetapanBody = RequestBody.create("text/plain".toMediaTypeOrNull(), tempatPenetapan)
        val tglPenetapanBody = RequestBody.create("text/plain".toMediaTypeOrNull(), tglPenetapan)
        val tipeIdBody = RequestBody.create("text/plain".toMediaTypeOrNull(), tipe_id.toString())
        val statusIdBody = RequestBody.create("text/plain".toMediaTypeOrNull(), statusId.toString())
        val sifatIdBody = RequestBody.create("text/plain".toMediaTypeOrNull(), sifatId.toString())
        val fileBody = RequestBody.create("application/pdf".toMediaTypeOrNull(), file)
        val multipartBody = MultipartBody.Part.createFormData("dokumen", file.name, fileBody)

        api.create_dokumen(
                judulBody, nomorBody, sumberBody, penandatangananBody, tempatPenetapanBody, tglPenetapanBody,
                tipeIdBody, statusIdBody, sifatIdBody, multipartBody
        ).enqueue(object: Callback<ResponseModel> {
            override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@DokumenTambahActivity, "Data saved successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent()
                    intent.putExtra(DOKUMEN_DATA_EXTRA, dokumen) // dokumen di sini adalah dokumen yang telah diperbarui
                    intent.putExtra(DOKUMEN_DATA_UPDATED, true)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                } else {
                    Toast.makeText(this@DokumenTambahActivity, "Failed to save data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                t.printStackTrace()
                Toast.makeText(this@DokumenTambahActivity, "Failed to save data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_EDIT_DOKUMEN && resultCode == RESULT_OK) {
            if (data?.getBooleanExtra(DOKUMEN_DATA_UPDATED, false) == true) {
                setupData()
            }
        }
        if (requestCode == REQUEST_PICK_PDF_FILE && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                selectedFile = uri
                binding.tvFile.text = "Dokumen terpilih"
            }
        }
    }
}
