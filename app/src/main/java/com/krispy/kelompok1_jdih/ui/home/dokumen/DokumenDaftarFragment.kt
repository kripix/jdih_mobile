package com.krispy.kelompok1_jdih.ui.home.dokumen

import android.content.Context
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

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class DokumenDaftarFragment : Fragment(), DokumenClickListener {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentDokumenListBinding
    private lateinit var dokumenAdapter: DokumenAdapter
    private val api by lazy { ApiRetrofit().endpoint }
    private var userId: Int = 0

    private var originalDokumenList: List<DokumenModel.Data> = listOf()
    private var currentSearchQuery: String = ""
    private var currentFilterType: String? = null

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
        binding = FragmentDokumenListBinding.inflate(inflater, container, false)
        initData()
        initRecyclerView()
        setupListener()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        getDokumen()
    }

    private fun initRecyclerView() {
        dokumenAdapter = DokumenAdapter(arrayListOf(), this)
        binding.rvDokumen.layoutManager = LinearLayoutManager(context)
        binding.rvDokumen.adapter = dokumenAdapter
    }

    private fun setupListener() {
        // Search View
        binding.searchDocs.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                currentSearchQuery = newText.orEmpty()
                updateDisplayedDokumen()
                return true
            }
        })

        // Filter Button
        binding.btnFilter.setOnClickListener {
            binding.filter.visibility = if (binding.filter.visibility == View.GONE) View.VISIBLE else View.GONE
            if (binding.filter.visibility == View.GONE) {
                currentFilterType = null
                updateDisplayedDokumen()
            }
        }

        // Filter Type Buttons
        binding.btnPeraturan.setOnClickListener { filterByType("Peraturan") }
        binding.btnMonografi.setOnClickListener { filterByType("Monografi") }
        binding.btnPutusan.setOnClickListener { filterByType("Putusan") }
        binding.btnArtikel.setOnClickListener { filterByType("Artikel") }
    }

    private fun filterByType(type: String) {
        currentFilterType = type
        updateDisplayedDokumen()
    }

    private fun updateDisplayedDokumen() {
        val filteredList = originalDokumenList.filter { dokumen ->
            val matchesType = currentFilterType?.let { dokumen.tipe.equals(it, true) } ?: true
            val matchesQuery = dokumen.judul.contains(currentSearchQuery, true)
            matchesType && matchesQuery
        }
        dokumenAdapter.setData(filteredList)
    }

    private fun initData() {
        val sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getInt("user_id", -1)
        Log.d("UserID", "userId: $userId")
    }

    private fun getDokumen() {
        api.get_dokumen(userId).enqueue(object : Callback<DokumenModel> {
            override fun onResponse(call: Call<DokumenModel>, response: Response<DokumenModel>) {
                if (response.isSuccessful) {
                    response.body()?.dokumen?.let { listData ->
                        originalDokumenList = listData
                        updateDisplayedDokumen()
                        Log.d("DokumenDaftarFragment", "Dokumen data: $listData")
                    } ?: run {
                        Log.e("DokumenDaftarFragment", "Empty Dokumen response")
                    }
                } else {
                    Log.e("DokumenDaftarFragment", "Error response: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<DokumenModel>, t: Throwable) {
                Log.e("DokumenDaftarFragment", "Failed to fetch Dokumen: ${t.message}")
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
}
