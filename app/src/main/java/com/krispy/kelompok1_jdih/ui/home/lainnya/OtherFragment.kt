package com.krispy.kelompok1_jdih.ui.home.lainnya

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.krispy.kelompok1_jdih.R
import com.krispy.kelompok1_jdih.databinding.FragmentOtherBinding
import com.krispy.kelompok1_jdih.data.util.SharedPrefManager
import com.krispy.kelompok1_jdih.ui.admin.MainActivityAdm

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OtherFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OtherFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding: FragmentOtherBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private lateinit var sharedPrefManager: SharedPrefManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOtherBinding.inflate(inflater, container, false)

        binding.button3.setOnClickListener {
            val myIg = "https://jdihn.go.id/"
            val intentIg = Intent(Intent.ACTION_VIEW, Uri.parse(myIg))
            startActivity(intentIg)
        }

        binding.btnShowLogin.setOnClickListener {
            val dialog = BottomSheetDialog(requireContext())
            val view = LayoutInflater.from(requireContext()).inflate(R.layout.bs_login, null)
            val btnClose = view.findViewById<Button>(R.id.btn_close)
            val btnLogin = view.findViewById<Button>(R.id.btn_login)
            val uName = view.findViewById<EditText>(R.id.ed_user_name)
            val pWord = view.findViewById<EditText>(R.id.ed_user_pass)
            val togglePass = view.findViewById<Button>(R.id.cb_toggle_pass)
            sharedPrefManager = SharedPrefManager(requireContext())

            togglePass.setOnClickListener {
                if (pWord.inputType == 129) {
                    pWord.inputType = 143
                } else {
                    pWord.inputType = 129
                    }
            }
            btnClose.setOnClickListener {
                dialog.dismiss()
            }
            dialog.setCancelable(false)
            dialog.setContentView(view)
            dialog.show()

            btnLogin.setOnClickListener{
                val username = uName.text.toString().trim()
                val password = pWord.text.toString().trim()

                if (isValidLogin(username, password)) {
                    sharedPrefManager.setLoggedIn(true)  // Set login state
                    sharedPrefManager.setUsername(username) // Optional: Store username
                    val intent = Intent(requireActivity(), MainActivityAdm::class.java)
                    startActivity(intent)
                    Toast.makeText(requireContext(), "Login berhasil", Toast.LENGTH_SHORT).show()
                    requireActivity().finish()  // Optionally, finish the LoginActivity
                } else if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    Toast.makeText(context, "Harap Masukkan Username dan Password", Toast.LENGTH_SHORT).show()
                }
                else {
                    Toast.makeText(context, "Username atau Password Salah", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return binding.root
    }

    private fun isValidLogin(username: String, password: String): Boolean {
        // Implement your validation logic here (e.g., check against a database)
        return username == "admin" && password == "admin123" // Replace with your logic
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OtherFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}