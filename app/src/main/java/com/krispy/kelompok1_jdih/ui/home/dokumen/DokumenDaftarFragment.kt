package com.krispy.kelompok1_jdih.ui.home.dokumen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.krispy.kelompok1_jdih.data.adapter.DokumenAdapter
import com.krispy.kelompok1_jdih.data.api.ApiRetrofit
import com.krispy.kelompok1_jdih.data.listener.DokumenClickListener
import com.krispy.kelompok1_jdih.data.model.DOKUMEN_DATA_EXTRA
import com.krispy.kelompok1_jdih.data.model.DOKUMEN_ID_EKSTRA
import com.krispy.kelompok1_jdih.data.model.DokumenModel
import com.krispy.kelompok1_jdih.databinding.FragmentDokumenListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DokumenDaftarFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DokumenDaftarFragment : Fragment(), DokumenClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentDokumenListBinding
    private lateinit var dokumenAdapter: DokumenAdapter

    private val api by lazy { ApiRetrofit().endpoint }
    private var userId: Int = 0

    private var originalDokumenList: List<DokumenModel.Data> = listOf()

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
        binding = FragmentDokumenListBinding.inflate(inflater, container, false)
        dokumenAdapter = DokumenAdapter(arrayListOf(), this)
        binding.rvDokumen.layoutManager = LinearLayoutManager(context)
        binding.rvDokumen.adapter = dokumenAdapter
        setupListener()
        return binding.root
    }

    override fun onStart(){
        super.onStart()
        getDokumen()
    }

    private fun setupListener(){
        binding.searchDocs.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { searchClasses(it) }
                return true
            }
        })

        binding.btnFilter.setOnClickListener{
            if (binding.filter.visibility == View.GONE) {
                binding.filter.visibility = View.VISIBLE
            } else {
                binding.filter.visibility = View.GONE
            }
        }
    }

    private fun getDokumen() {
        api.get_dokumen(userId).enqueue(object : Callback<DokumenModel> {
            override fun onResponse(call: Call<DokumenModel>, response: Response<DokumenModel>) {
                if (response.isSuccessful) {
                    response.body()?.dokumen?.let { listData ->
                        originalDokumenList = listData
                        dokumenAdapter.setData(listData)
                        Log.d("HomeFragment", "Dokumen data: ${listData}")
                    } ?: run {
                        Log.e("HomeFragment", "Empty Dokumen response")
                    }
                } else {
                    Log.e("HomeFragment", "Error response: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<DokumenModel>, t: Throwable) {
                Log.e("HomeFragment", "Failed to fetch Dokumen: ${t.message}")
            }
        })
    }

    override fun onClick(dokumen: DokumenModel.Data) {
        val intent = Intent(requireContext(), DokumenDetailActivity::class.java).apply {
            putExtra(DOKUMEN_ID_EKSTRA, dokumen.id)
            putExtra(DOKUMEN_DATA_EXTRA, dokumen)
        }
        startActivity(intent)
    }

    private fun searchClasses(query: String) {
        val filteredList = if (query.isEmpty()) originalDokumenList else originalDokumenList.filter { dokumen ->
            dokumen.judul.contains(query, true)
        }
        dokumenAdapter.setData(filteredList)
    }


}