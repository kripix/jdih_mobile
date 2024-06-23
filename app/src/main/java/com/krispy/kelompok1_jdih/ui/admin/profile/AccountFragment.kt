package com.krispy.kelompok1_jdih.ui.admin.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import com.krispy.kelompok1_jdih.ui.home.MainActivity
import com.krispy.kelompok1_jdih.R
import com.krispy.kelompok1_jdih.data.api.ApiRetrofit
import com.krispy.kelompok1_jdih.data.helper.MediaHelper
import com.krispy.kelompok1_jdih.data.model.ResponseModel
import com.krispy.kelompok1_jdih.data.model.UserResponse
import com.krispy.kelompok1_jdih.data.util.SharedPrefManager
import com.krispy.kelompok1_jdih.databinding.FragmentProfileBinding
import com.krispy.kelompok1_jdih.ui.admin.MainActivityAdm
import com.krispy.kelompok1_jdih.ui.home.berita.BeritaTambahActivity.Companion.BERITA_DATA_UPDATED
import com.krispy.kelompok1_jdih.ui.home.berita.BeritaTambahActivity.Companion.REQUEST_EDIT_BERITA
import com.squareup.picasso.Picasso
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AccountFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AccountFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding

    private val api by lazy { ApiRetrofit().endpoint }
    private var userId: Int = -1
    private lateinit var username: String
    private lateinit var password: String
    private lateinit var nama_lengkap: String
    private lateinit var url_foto: String
    private var isEditing = false
    private lateinit var mediaHelper: MediaHelper



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        mediaHelper = MediaHelper(requireActivity())
        initData()
        binding.idMinimenu.setOnClickListener {
            showPopupMenu(binding.idMinimenu)
        }
        binding.btnSave.setOnClickListener {
            saveChanges()
        }

        return binding.root
    }

    private fun initData() {
        val sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getInt("user_id", -1)
        username = sharedPreferences.getString("username", "") ?: ""
        password = sharedPreferences.getString("password", "") ?: ""
        nama_lengkap = sharedPreferences.getString("nama_lengkap", "") ?: ""
        url_foto = sharedPreferences.getString("url_foto", "") ?: ""


        Log.d("CekUser","$userId, $username, $password, $nama_lengkap, $url_foto")
        binding.idUname.text = username
        binding.idPass.text = password
        binding.idNama.text = nama_lengkap
        Picasso.get().load(url_foto).into(binding.idProfile)

        api.getUser(username,password).enqueue(object : Callback<com.krispy.kelompok1_jdih.data.model.UserResponse> {
            override fun onResponse(p0: Call<UserResponse>, p1: Response<UserResponse>) {
                val userResponse = p1.body()!!
                if (userResponse.result.isNotEmpty()) {
                    val user = userResponse.result[0]
                    nama_lengkap = user.nama_lengkap
                    url_foto = user.url_foto
                    sharedPreferences.edit().putString("nama_lengkap", nama_lengkap).apply()
                    sharedPreferences.edit().putString("url_foto", url_foto).apply()
                    binding.idNama.text = nama_lengkap
                    Picasso.get().load(url_foto).into(binding.idProfile)

                    Log.d("CekUser","$nama_lengkap, $url_foto")
                } else {

                }

            }

            override fun onFailure(p0: Call<UserResponse>, p1: Throwable) {

            }


        })
    }

    private fun toggleEditMode() {
        isEditing = !isEditing
        with(binding) {
            if (isEditing) {
                idNama.visibility = View.GONE
                etNama.visibility = View.VISIBLE
                etNama.setText(idNama.text)

                idUname.visibility = View.GONE
                etUname.visibility = View.VISIBLE
                etUname.setText(idUname.text)

                idPass.visibility = View.GONE
                etPass.visibility = View.VISIBLE
                etPass.setText(idPass.text)

                idProfile.setOnClickListener {
                    intentGallery()
                }

                btnSave.visibility = View.VISIBLE
            } else {
                idNama.visibility = View.VISIBLE
                etNama.visibility = View.GONE

                idUname.visibility = View.VISIBLE
                etUname.visibility = View.GONE

                idPass.visibility = View.VISIBLE
                etPass.visibility = View.GONE

                btnSave.visibility = View.GONE

                idProfile.setOnClickListener {
                }
            }
        }
    }

    private fun intentGallery() {
        val options = arrayOf<CharSequence>("Ambil Foto", "Pilih dari Galeri", "Batal")

        val builder = AlertDialog.Builder(requireContext())
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

    private fun saveChanges() {
        val newName = binding.etNama.text.toString()
        val newUsername = binding.etUname.text.toString()
        val newPassword = binding.etPass.text.toString()

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Konfirmasi")
        builder.setMessage("Apakah Anda yakin ingin menyimpan perubahan?")
        builder.setPositiveButton("Ya") { dialog, which ->
            Log.d("SaveChanges", "Before checking file path: $url_foto")

            // Determine if url_foto is a local file path or a remote URL
            val isLocalFile = url_foto.startsWith("/storage") || url_foto.startsWith("/data")

            // Prepare file part (foto) only if it's a local file path
            var fotoPart: MultipartBody.Part? = null
            if (isLocalFile) {
                if (url_foto.isNotEmpty() && File(url_foto).exists()) {
                    val file = File(url_foto)
                    val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                    fotoPart = MultipartBody.Part.createFormData("foto", file.name, requestFile)
                } else {
                    Toast.makeText(requireContext(), "File gambar tidak ditemukan: $url_foto", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
            }

            Log.d("SaveChanges", "After checking file path: $url_foto")

            val call = api.updateProfile(
                RequestBody.create("text/plain".toMediaTypeOrNull(), userId.toString()),
                RequestBody.create("text/plain".toMediaTypeOrNull(), newName),
                RequestBody.create("text/plain".toMediaTypeOrNull(), newUsername),
                RequestBody.create("text/plain".toMediaTypeOrNull(), newPassword),
                RequestBody.create("text/plain".toMediaTypeOrNull(), url_foto),
                fotoPart // MultipartBody.Part for foto, can be null
            )

            call.enqueue(object : Callback<ResponseModel> {
                override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Profile berhasil diperbarui", Toast.LENGTH_SHORT).show()
                        val sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                        with(sharedPreferences.edit()) {
                            putString("nama_lengkap", newName)
                            putString("username", newUsername)
                            putString("password", newPassword)
                            putString("url_foto", url_foto)
                            apply()
                        }
                        initData()
                        toggleEditMode()
                    } else {
                        Toast.makeText(requireContext(), "Gagal memperbarui profile", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                    Toast.makeText(requireContext(), "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
                    Log.e("AccountFragment", "Error updating profile", t)
                }
            })
        }
        builder.setNegativeButton("Tidak") { dialog, which ->
            dialog.dismiss()
        }
        builder.show()
    }





    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            mediaHelper.getRcGallery() -> {
                if (resultCode == RESULT_OK && data != null) {
                    val selectedImage: Uri? = data.data
                    if (selectedImage != null) {
                        val filePath = mediaHelper.getRealPathFromURI(selectedImage)
                        if (filePath != null) {
                            url_foto = filePath
                            Picasso.get().load(File(url_foto)).into(binding.idProfile)
                        }
                    }
                }
            }
            mediaHelper.getRcCamera() -> {
                if (resultCode == RESULT_OK && data != null) {
                    val imageBitmap = data.extras?.get("data") as Bitmap
                    binding.idProfile.setImageBitmap(imageBitmap)
                    url_foto = mediaHelper.saveImageToStorage(imageBitmap)
                }
            }
        }
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.pop_up_account, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.edit_profile -> {
                    toggleEditMode()
                    true
                }

                R.id.logout -> {
                    val sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                    sharedPreferences.edit().putBoolean("is_logged_in", false).apply()
                    val intent = Intent(requireActivity(), MainActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AccountFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
