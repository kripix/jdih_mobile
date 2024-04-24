package com.krispy.kelompok1_jdih

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.krispy.kelompok1_jdih.R
import com.krispy.kelompok1_jdih.databinding.ActivityAddDocsBinding
import com.krispy.kelompok1_jdih.room.Dokumen
import com.krispy.kelompok1_jdih.room.DokumenDB
import com.krispy.kelompok1_jdih.room.constant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class AddDocsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddDocsBinding
    private val arrayType = arrayOf("PUU", "Naskah Akademik", "Artikel", "Putusan")
    private lateinit var adapterSpin: ArrayAdapter<String>

    private var selectedYear = 0
    private var month = 0
    private var day = 0
    private var hours = 0
    private var minutes = 0

    private val db by lazy { DokumenDB(this) }
    private var dokumenId = 0

    private var tipe = ""
    private var judul = ""
    private var tgl = ""
    private var status = ""
    private var file = ""

    private val REQUEST_FILE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddDocsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the spinner adapter
        adapterSpin = ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayType)
        binding.spinnerTipe.adapter = adapterSpin

        // Set listeners
        binding.spinnerTipe.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            }
        }

        // Set initial date
        val cal: Calendar = Calendar.getInstance()
        selectedYear = cal.get(Calendar.YEAR)
        month = cal.get(Calendar.MONTH)
        day = cal.get(Calendar.DAY_OF_MONTH)
        hours = cal.get(Calendar.HOUR)
        minutes = cal.get(Calendar.MINUTE)
        binding.tvTgl.text = "tanggal $day/${month + 1}/$selectedYear"

        // Set listener for date button
        binding.btnTgl.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(this,
                { _, year, monthOfYear, dayOfMonth ->
                    tgl = String.format("%02d/%02d/%d", year, monthOfYear + 1, dayOfMonth)
                    binding.tvTgl.text = tgl
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

            datePickerDialog.show()
        }

        // Set listener for radio group
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            status = when (checkedId) {
                R.id.radioButton -> "Berlaku"
                R.id.radioButton2 -> "Tidak Berlaku"
                else -> ""
            }
        }

        // Set listener for upload file button
        binding.btnUploadFile.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "application/pdf"
            startActivityForResult(intent, REQUEST_FILE)
            Toast.makeText(this, "Silahkan pilih file PDF", Toast.LENGTH_SHORT).show()
        }

        // Setup view and listeners
        setupView()
        setupListener()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_FILE -> {
                    data?.data?.let { uri ->
                        file = uri.toString()
                    }
                }
            }
        }
    }

    private fun setupView() {
        binding.btnSimpan.visibility = View.VISIBLE
        binding.btnUpdate.visibility = View.GONE
        when (intentType()) {
            constant.TYPE_READ -> {
                binding.btnSimpan.visibility = View.GONE
                binding.btnUpdate.visibility = View.GONE
                getDokumen()
            }
            constant.TYPE_UPDATE -> {
                binding.btnSimpan.visibility = View.GONE
                binding.btnUpdate.visibility = View.VISIBLE
                getDokumen()
            }
        }
    }

    private fun setupListener() {
        binding.btnSimpan.setOnClickListener {
            tipe = adapterSpin.getItem(binding.spinnerTipe.selectedItemPosition)!!
            judul = binding.editJudul.text.toString()
            CoroutineScope(Dispatchers.IO).launch {
                db.DokumenDAO().addDokumen(
                    Dokumen(0, tipe, judul, tgl, status, file)
                )
            }
            finish()
        }
        binding.btnUpdate.setOnClickListener {
            tipe = adapterSpin.getItem(binding.spinnerTipe.selectedItemPosition)!!
            judul = binding.editJudul.text.toString()
            CoroutineScope(Dispatchers.IO).launch {
                db.DokumenDAO().updateDokumen(
                    Dokumen(dokumenId, tipe, judul, tgl, status, file)
                )
            }
            finish()
        }

    }

    private fun getDokumen() {
        val dokumenId = intent.getIntExtra("idDokumen", 0)
        CoroutineScope(Dispatchers.IO).launch {
            val doks = db.DokumenDAO().getDokumen(dokumenId).get(0)
            runOnUiThread {
                binding.spinnerTipe.setSelection(arrayType.indexOf(doks.tipe))
                binding.editJudul.setText(doks.judul)
                binding.tvTgl.text = doks.tgl
                binding.radioGroup.check(
                    when (doks.status) {
                        "Berlaku" -> R.id.radioButton
                        "Tidak Berlaku" -> R.id.radioButton2
                        else -> R.id.radioButton
                    }
                )
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    private fun intentType(): Int {
        return intent.getIntExtra("intent_type", 0)
    }
}
