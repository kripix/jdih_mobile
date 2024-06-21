package com.krispy.kelompok1_jdih.ui.home.dokumen

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

    private var tipe_id: Int = 0
    private var fileUri: Uri? = null

    private val api by lazy { ApiRetrofit().endpoint }

    companion object {
        const val REQUEST_EDIT_DOKUMEN = 133
        const val DOKUMEN_DATA_UPDATED = "dokumen_data_updated"
        const val REQUEST_FILE_PICKER = 134
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDokumenTambahBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupData()
        setupListener()
    }

    private fun setupListener() {
        with(binding) {
            btnBack.setOnClickListener { finish() }
            btnSimpan.setOnClickListener { saveDokumen() }
            btnFile.setOnClickListener { filePicker() }
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

    private fun filePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/pdf"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(Intent.createChooser(intent, "Select a file"), REQUEST_FILE_PICKER)
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

            tipe_id = dokumen.tipe_id - 1
            if (dokumen.status_id == 1) rbBerlaku.isChecked = true else rbTidakBerlaku.isChecked = true
            if (dokumen.sifat_id == 1) rbPublic.isChecked = true else rbPrivate.isChecked = true
        }
    }

    private fun saveDokumen() {
        val judul = binding.etJudul.text.toString().trim()
        val nomor = binding.etNomor.text.toString().trim()
        val sumber = binding.etSumber.text.toString().trim()
        val penandatanganan = binding.etPenandatanganan.text.toString().trim()
        val tempatPenetapan = binding.etTempatPenetapan.text.toString().trim()
        val tglPenetapan = binding.tvTglPenetapan.text.toString().trim()
        val statusId = if (rbBerlaku.isChecked) 1 else 0
        val sifatId = if (rbPublic.isChecked) 1 else 0

        if (judul.isEmpty()) {
            Toast.makeText(this, "Judul tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        fileUri?.let { uri ->
            uploadFile(uri) { fileUrl ->
                saveDokumenToServer(judul, nomor, sumber, penandatanganan, tempatPenetapan, tglPenetapan, statusId, sifatId, fileUrl)
            }
        } ?: run {
            saveDokumenToServer(judul, nomor, sumber, penandatanganan, tempatPenetapan, tglPenetapan, statusId, sifatId, "")
        }
    }

    private fun uploadFile(fileUri: Uri, callback: (String) -> Unit) {
        callback("uploaded_file_url") // Replace this with actual upload logic
    }

    private fun saveDokumenToServer(
        judul: String,
        nomor: String,
        sumber: String,
        penandatanganan: String,
        tempatPenetapan: String,
        tglPenetapan: String,
        statusId: Int,
        sifatId: Int,
        fileUrl: String
    ) {
        val call: Call<ResponseModel> = dokumen?.let {
            api.update_dokumen(it.id, judul, nomor, sumber, penandatanganan, tempatPenetapan, tglPenetapan,
                tipe_id, statusId, sifatId, fileUrl)
        } ?: run {
            api.create_dokumen(judul, nomor, sumber, penandatanganan, tempatPenetapan, tglPenetapan, tipe_id, statusId, sifatId, fileUrl)
        }

        call.enqueue(object : Callback<ResponseModel> {
            override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                if (response.isSuccessful) {
                    response.body()?.message?.let { message ->
                        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK, Intent().apply { putExtra(DOKUMEN_DATA_UPDATED, true) })
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
        if (requestCode == REQUEST_EDIT_DOKUMEN && resultCode == RESULT_OK) {
            if (data?.getBooleanExtra(DOKUMEN_DATA_UPDATED, false) == true) {
                setupData()
            }
        } else if (requestCode == REQUEST_FILE_PICKER && resultCode == Activity.RESULT_OK) {
            data?.data?.let {
                fileUri = it
                binding.tvFile.text = it.path
            }
        }
    }
}