package com.krispy.kelompok1_jdih.ui.home.berita

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.krispy.kelompok1_jdih.R
import com.krispy.kelompok1_jdih.data.api.ApiRetrofit
import com.krispy.kelompok1_jdih.data.helper.MediaHelper
import com.krispy.kelompok1_jdih.data.model.BERITA_DATA_EXTRA
import com.krispy.kelompok1_jdih.data.model.BeritaModel
import com.krispy.kelompok1_jdih.data.model.ResponseModel
import com.krispy.kelompok1_jdih.databinding.ActivityBeritaTambahBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class BeritaTambahActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBeritaTambahBinding
    private var berita: BeritaModel.Data? = null
    private lateinit var mediaHelper: MediaHelper
    private val api by lazy { ApiRetrofit().endpoint }

    private lateinit var urlCover: String

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

        mediaHelper = MediaHelper(this)
        setupData()
        setupListener()
    }

    private fun setupListener() {
        with(binding) {
            btnBack.setOnClickListener { finish() }
            btnSimpan.setOnClickListener { saveBerita() }
            pvImg.setOnClickListener { intentGallery() }
        }
    }

    private fun intentGallery() {
        val options = arrayOf<CharSequence>("Ambil Foto", "Pilih dari Galeri", "Batal")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pilih Sumber Gambar")
        builder.setItems(options) { dialog, item ->
            when {
                options[item] == "Ambil Foto" -> {
                    val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(takePicture, mediaHelper.getRcCamera())
                }
                options[item] == "Pilih dari Galeri" -> {
                    val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(pickPhoto, mediaHelper.getRcGallery())
                }
                options[item] == "Batal" -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
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
            titleBar.text = "Edit Berita"
        }
        Picasso.get().load(berita.url_cover).into(binding.pvImg)
        urlCover = berita.url_cover // Set urlCover for potential update
    }

    private fun saveBerita() {
        val judul = binding.etJudul.text.toString().trim()
        val isi = binding.etIsi.text.toString().trim()

        if (judul.isEmpty() || isi.isEmpty()) {
            Toast.makeText(this, "Judul dan isi tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        val beritaId = berita?.id
        if (beritaId != null) {
            showConfirmationDialog(true) // Update existing berita
        } else {
            showConfirmationDialog(false) // Create new berita
        }
    }

    private fun showConfirmationDialog(isUpdate: Boolean) {
        val message = if (isUpdate) "Apakah Anda yakin ingin mengupdate berita ini?" else "Apakah Anda yakin ingin menyimpan berita ini?"

        AlertDialog.Builder(this)
            .setTitle("Konfirmasi")
            .setMessage(message)
            .setPositiveButton("Ya") { _, _ ->
                if (isUpdate) {
                    updateBerita()
                } else {
                    createBerita()
                }
            }
            .setNegativeButton("Tidak", null)
            .show()
    }

    private fun updateBerita() {
        val judul = binding.etJudul.text.toString().trim()
        val isi = binding.etIsi.text.toString().trim()

        val isLocalFile = urlCover.startsWith("/storage") || urlCover.startsWith("/data")

        // Prepare file part (foto) only if it's a local file path
        var coverPart: MultipartBody.Part? = null
        if (isLocalFile) {
            if (urlCover.isNotEmpty() && File(urlCover).exists()) {
                val file = File(urlCover)
                val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                coverPart = MultipartBody.Part.createFormData("foto", file.name, requestFile)
            } else {
                Toast.makeText(this, "File gambar tidak ditemukan: $urlCover", Toast.LENGTH_SHORT).show()
                return
            }
        }

        Log.d("SaveChanges", "After checking file path: $urlCover")

        val call = api.updateBerita(
            RequestBody.create("text/plain".toMediaTypeOrNull(), berita?.id.toString()),
            RequestBody.create("text/plain".toMediaTypeOrNull(), judul),
            RequestBody.create("text/plain".toMediaTypeOrNull(), isi),
            RequestBody.create("text/plain".toMediaTypeOrNull(), urlCover),
            coverPart
        )
            call.enqueue(object : Callback<ResponseModel> {
                override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@BeritaTambahActivity, "Berita berhasil diupdate", Toast.LENGTH_SHORT).show()
                        val resultIntent = Intent()
                        resultIntent.putExtra(BERITA_DATA_UPDATED, true)
                        setResult(Activity.RESULT_OK, resultIntent)
                        finish()
                    } else {
                        Toast.makeText(this@BeritaTambahActivity, "Gagal mengupdate berita", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                    Toast.makeText(this@BeritaTambahActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun createBerita() {
        val judul = binding.etJudul.text.toString().trim()
        val isi = binding.etIsi.text.toString().trim()
        val judulPart = RequestBody.create(MultipartBody.FORM, judul)
        val isiPart = RequestBody.create(MultipartBody.FORM, isi)

        val file = urlCover?.let { File(it) }
        val requestFile = file?.let { RequestBody.create("multipart/form-data".toMediaTypeOrNull(), it) }
        val filePart = requestFile?.let { MultipartBody.Part.createFormData("cover", file.name, it) }

        api.createBerita(judulPart, isiPart, filePart!!)
            .enqueue(object : Callback<ResponseModel> {
                override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@BeritaTambahActivity, "Berita berhasil disimpan", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@BeritaTambahActivity, "Gagal menyimpan berita", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                    Toast.makeText(this@BeritaTambahActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            mediaHelper.getRcGallery() -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val selectedImage: Uri? = data.data
                    if (selectedImage != null) {
                        val filePath = mediaHelper.getRealPathFromURI(selectedImage)
                        if (filePath != null) {
                            urlCover = filePath
                            binding.pvImg.setImageBitmap(BitmapFactory.decodeFile(filePath))
                        }
                    }
                }
            }
            mediaHelper.getRcCamera() -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val imageBitmap = data.extras?.get("data") as Bitmap
                    binding.pvImg.setImageBitmap(imageBitmap)
                    urlCover = mediaHelper.saveImageToStorage(imageBitmap)
                }
            }
        }
    }
}
