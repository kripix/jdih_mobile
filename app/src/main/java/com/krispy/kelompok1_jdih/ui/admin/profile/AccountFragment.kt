package com.krispy.kelompok1_jdih.ui.admin.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import com.krispy.kelompok1_jdih.ui.home.MainActivity
import com.krispy.kelompok1_jdih.R
import com.krispy.kelompok1_jdih.data.api.ApiRetrofit
import com.krispy.kelompok1_jdih.data.model.ResponseModel
import com.krispy.kelompok1_jdih.data.util.SharedPrefManager
import com.krispy.kelompok1_jdih.databinding.FragmentProfileBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding : FragmentProfileBinding
    private var isEditing = false
    private val api by lazy { ApiRetrofit().endpoint }

    private var userId: Int = -1
    lateinit var username: String
    lateinit var password: String
    lateinit var nama_lengkap: String
    lateinit var url_foto: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        initData()
        binding.idMinimenu.setOnClickListener {
            showPopupMenu(binding.idMinimenu)
        }
        binding.btnSave.setOnClickListener{
            saveChanges()
        }

        return binding.root
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

    private fun initData() {
        val sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getInt("user_id", -1)
        username = sharedPreferences.getString("username", "") ?: ""
        password = sharedPreferences.getString("password", "") ?: ""
        nama_lengkap = sharedPreferences.getString("nama_lengkap", "") ?: ""
        url_foto = sharedPreferences.getString("url_foto", "") ?: ""
        Log.d("AccountFragment", "userId: $userId")
        Log.d("AccountFragment", "username: $username")
        Log.d("AccountFragment", "password: $password")
        Log.d("AccountFragment", "nama_lengkap: $nama_lengkap")
        Log.d("AccountFragment", "url_foto: $url_foto")
        binding.idNama.text = nama_lengkap
        binding.idUname.text = username
        binding.idPass.text = password
    }



    private fun toggleEditMode() {
        isEditing = !isEditing
        with(binding){
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

                btnSave.visibility = View.VISIBLE
            } else {
                // Show TextViews and hide EditTexts
                idNama.visibility = View.VISIBLE
                etNama.visibility = View.GONE

                idUname.visibility = View.VISIBLE
                etUname.visibility = View.GONE

                idPass.visibility = View.VISIBLE
                etPass.visibility = View.GONE

                btnSave.visibility = View.GONE
            }
        }
    }

    private fun saveChanges() {
        val newName = binding.etNama.text.toString()
        val newUsername = binding.etUname.text.toString()
        val newPassword = binding.etPass.text.toString()
        val url_foto = "Nanika.jpg"
        val user_id = 1

        // Assuming you have a Retrofit interface setup
        val call = api.update_profile(user_id, newName, newUsername, newPassword, url_foto)
        call.enqueue(object : Callback<ResponseModel> {
            override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                // Handle response here
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Profile berhasil diperbarui", Toast.LENGTH_SHORT).show()
                    val sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                    sharedPreferences.edit().putString("nama_lengkap", newName).apply()
                    sharedPreferences.edit().putString("username", newUsername).apply()
                    sharedPreferences.edit().putString("password", newPassword).apply()
                    sharedPreferences.edit().putString("url_foto", url_foto).apply()
                    initData()
                    toggleEditMode()
                } else {
                    // Show error message
                }
            }

            override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                // Handle failure
            }
        })
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AccountFragment.
         */
        // TODO: Rename and change types and number of parameters
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