package com.krispy.kelompok1_jdih.ui.home.dokumen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.krispy.kelompok1_jdih.R
import com.krispy.kelompok1_jdih.databinding.FragmentDocsBinding
import com.krispy.kelompok1_jdih.data.room.DokumenDB
import com.krispy.kelompok1_jdih.data.adapter.item_docs

//import com.krispy.kelompok1_jdih.room.NoteDAO  // Import NoteDAO
//import com.krispy.kelompok1_jdih.room.AppDatabase



// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DocsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DocsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding: FragmentDocsBinding

    val db by lazy { DokumenDB(this.requireContext()) }
    lateinit var doksAdapter : item_docs

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
        // Inflate the layout for this fragment
        binding = FragmentDocsBinding.inflate(inflater, container, false)

        binding.filterDocs.setOnClickListener {
            val dialog = BottomSheetDialog(requireContext())
            val view = LayoutInflater.from(requireContext()).inflate(R.layout.filter_docs, null)
            val btnClose = view.findViewById<Button>(R.id.idBtnDismiss)
            btnClose.setOnClickListener {
                dialog.dismiss()
            }
            dialog.setCancelable(false)
            dialog.setContentView(view)
            dialog.show()
        }

        binding.rvDocs.layoutManager = LinearLayoutManager(this.requireContext())

        return binding.root
    }


}