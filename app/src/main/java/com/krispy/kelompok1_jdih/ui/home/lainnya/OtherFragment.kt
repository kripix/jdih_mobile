package com.krispy.kelompok1_jdih.ui.home.lainnya

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.text.TextUtils.replace
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.google.android.gms.maps.MapFragment
import com.krispy.kelompok1_jdih.R
import com.krispy.kelompok1_jdih.data.api.ApiRetrofit
import com.krispy.kelompok1_jdih.data.model.UserModel
import com.krispy.kelompok1_jdih.data.model.UserResponse
import com.krispy.kelompok1_jdih.databinding.FragmentOtherBinding
import com.krispy.kelompok1_jdih.ui.admin.MainActivityAdm
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OtherFragment : Fragment() {

    private lateinit var binding: FragmentOtherBinding
    private lateinit var dialogin: Dialog
    private lateinit var btnClose: ImageButton
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tgglPassword: CheckBox
    private val api by lazy { ApiRetrofit().endpoint }
    private var lgn = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOtherBinding.inflate(inflater, container, false)
        setupListener()
        setupDialogin()
        checkLoginStatus()
        return binding.root
    }

    private fun setupListener() {
        with(binding) {
            btnJdihn.setOnClickListener {
                jdihn()
            }
            btnShowLogin.setOnClickListener {
                dialogin.show()
            }
            btnAlamat.setOnClickListener {
                if (lgn) {
                    parentFragmentManager.commit {
                        replace(R.id.container, MapsFragment())
                        addToBackStack("OtherFragment")  // Menambahkan nama fragment saat ini ke back stack
                    }
                } else {
                    parentFragmentManager.commit {
                        replace(R.id.main, MapsFragment())
                        addToBackStack("OtherFragment")  // Menambahkan nama fragment saat ini ke back stack
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Memastikan untuk menghapus login status ketika fragment di-resume
        checkLoginStatus()
    }


    private fun jdihn() {
        val myIg = "https://jdihn.go.id/"
        val intentIg = Intent(Intent.ACTION_VIEW, Uri.parse(myIg))
        startActivity(intentIg)
    }

    private fun setupDialogin() {
        dialogin = Dialog(requireContext()).apply {
            setContentView(R.layout.bs_login)
            window?.setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            setCancelable(true)
        }

        btnLogin = dialogin.findViewById(R.id.btn_login)
        etUsername = dialogin.findViewById(R.id.ed_user_name)
        etPassword = dialogin.findViewById(R.id.ed_user_pass)
        btnClose = dialogin.findViewById(R.id.btn_close)
        btnClose.setOnClickListener {
            dialogin.dismiss()
        }
        btnLogin.setOnClickListener {
            auth()
        }

        tgglPassword = dialogin.findViewById(R.id.cb_toggle_pass)
        tgglPassword.setOnCheckedChangeListener { _, isChecked ->
            etPassword.inputType = if (isChecked) InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            else InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
    }

    private fun checkLoginStatus() {
        val sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)
        if (isLoggedIn) {
            binding.frameLogin.visibility = View.GONE
            lgn = true
        } else {
            binding.frameLogin.visibility = View.VISIBLE
            lgn = false
        }
    }

    private fun auth() {
        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(context, "Harap Masukkan Username dan Password", Toast.LENGTH_SHORT).show()
            return
        }

        api.getUser(username, password).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val userResponse = response.body()!!
                    if (userResponse.result.isNotEmpty()) {
                        val user = userResponse.result[0]
                        Toast.makeText(context, "Login berhasil", Toast.LENGTH_SHORT).show()
                        // Save login state
                        val sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                        sharedPreferences.edit().putBoolean("is_logged_in", true).apply()
                        sharedPreferences.edit().putInt("user_id", user.id).apply()
                        sharedPreferences.edit().putString("username", user.username).apply()
                        sharedPreferences.edit().putString("password", user.password).apply()

                        // Redirect to admin activity
                        val intent = Intent(requireActivity(), MainActivityAdm::class.java)
                        startActivity(intent)
                        dialogin.dismiss()
                    } else {
                        Toast.makeText(context, userResponse.message ?: "Login failed", Toast.LENGTH_SHORT).show()
                        Log.d("Login", "Login failed for Username: $username")
                    }
                } else {
                    Toast.makeText(context, "Login failed: Unexpected response", Toast.LENGTH_SHORT).show()
                    Log.d("Login", "Unexpected response")
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Toast.makeText(context, "Login failed: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.d("Login", "Error: ${t.message}")
            }
        })
    }
}
