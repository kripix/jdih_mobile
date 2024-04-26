package com.krispy.kelompok1_jdih

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.sidesheet.SideSheetDialog
import com.krispy.kelompok1_jdih.databinding.ActivityDetailDocsBinding
import com.krispy.kelompok1_jdih.databinding.FragmentDataDocsBinding
import com.krispy.kelompok1_jdih.room.Dokumen
import com.krispy.kelompok1_jdih.room.DokumenDB
import com.krispy.kelompok1_jdih.room.constant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DataDocsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DataDocsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var binding : FragmentDataDocsBinding
    val arrayJudul = arrayOf("Artikel A", "Artikel B", "Artikel B", "Putusan A", "Putusan B", "Naskah A", "Naskah B", "PUU A", "PUU B")

    val db by lazy { DokumenDB(this.requireContext()) }
    lateinit var dokumenAdapter : DokumenAdapter
    lateinit var adapterAuto: ArrayAdapter<String>


    // creating array adapter for listview
    lateinit var listAdapter: ArrayAdapter<String>

    // creating array list for listview
    lateinit var programmingLanguagesList: ArrayList<String>;

    // creating variable for searchview
    lateinit var searchView: SearchView
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
        binding = FragmentDataDocsBinding.inflate(inflater, container, false)
        setupRecyclerView()
        binding.filterDocs.setOnClickListener {
            val dialog = SideSheetDialog(requireContext())
            val view = LayoutInflater.from(requireContext()).inflate(R.layout.filter_docs, null)
            val btnClose = view.findViewById<Button>(R.id.idBtnDismiss)
            btnClose.setOnClickListener {
                dialog.dismiss()
            }
            dialog.setCancelable(false)
            dialog.setContentView(view)
            dialog.show()
        }

        val viewContext = binding.autoSearchDocs.context
        val adapterAuto = viewContext?.let {
            ArrayAdapter(it, android.R.layout.simple_list_item_1, arrayJudul)
        }
        if (adapterAuto != null) {
            binding.autoSearchDocs.setAdapter(adapterAuto)
        } else {
            // Handle the case where context is null (e.g., show an error message)
        }

        setupRecyclerView()
        return binding.root

    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData(){
        CoroutineScope(Dispatchers.IO).launch {
            dokumenAdapter.setData(db.DokumenDAO().getDokumens())
            withContext(Dispatchers.Main) {
                dokumenAdapter.notifyDataSetChanged()
            }
        }
    }


    private fun setupRecyclerView(){
        dokumenAdapter = DokumenAdapter(
            arrayListOf(),
            object : DokumenAdapter.OnAdapterListener {
                override fun onClick(dokumen: Dokumen) {
                    val intent = Intent(requireActivity(), DetailDocsActivity::class.java)
                    startActivity(intent)
                }
                override fun onUpdate(dokumen: Dokumen) {
                    intentEdit(constant.TYPE_UPDATE, dokumen.id)
                }
                override fun onDelete(dokumen: Dokumen) {
                    deleteAlert(dokumen)
                }
            })

        binding.rvDocsAdm.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = dokumenAdapter
        }

    }

    private fun intentEdit(intent_type: Int, idDokumen: Int) {
        val intent = Intent(requireActivity(), AddDocsActivity::class.java).putExtra("intent_type", intent_type).putExtra("idDokumen", idDokumen)
        startActivity(intent)

    }

    private fun deleteAlert(dokumen: Dokumen){
        val dialog = AlertDialog.Builder(requireContext())
        dialog.apply {
            setTitle("Konfirmasi Hapus")
            setMessage("Yakin hapus ${dokumen.judul}?")
            setNegativeButton("Batal") { dialogInterface, i ->
                dialogInterface.dismiss()
            }
            setPositiveButton("Hapus") { dialogInterface, i ->
                CoroutineScope(Dispatchers.IO).launch {
                    db.DokumenDAO().deleteDokumen(dokumen)
                    dialogInterface.dismiss()
                    loadData()
                }
            }
        }

        dialog.show()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DataDocsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DataDocsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}