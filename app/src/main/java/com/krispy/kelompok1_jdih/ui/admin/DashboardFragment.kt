package com.krispy.kelompok1_jdih.ui.admin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.krispy.kelompok1_jdih.R
import com.krispy.kelompok1_jdih.data.adapter.BeritaAdapter
import com.krispy.kelompok1_jdih.data.adapter.DokumenAdapter
import com.krispy.kelompok1_jdih.data.api.ApiRetrofit
import com.krispy.kelompok1_jdih.data.listener.BeritaClickListener
import com.krispy.kelompok1_jdih.data.listener.DokumenClickListener
import com.krispy.kelompok1_jdih.data.model.BERITA_DATA_EXTRA
import com.krispy.kelompok1_jdih.data.model.BERITA_ID_EXTRA
import com.krispy.kelompok1_jdih.data.model.BeritaModel
import com.krispy.kelompok1_jdih.data.model.DOKUMEN_DATA_EXTRA
import com.krispy.kelompok1_jdih.data.model.DOKUMEN_ID_EKSTRA
import com.krispy.kelompok1_jdih.data.model.DokumenModel
import com.krispy.kelompok1_jdih.databinding.FragmentAdminDashboardBinding
import com.krispy.kelompok1_jdih.ui.home.berita.BeritaDetailActivity
import com.krispy.kelompok1_jdih.ui.home.dokumen.DokumenDaftarFragment
import com.krispy.kelompok1_jdih.ui.home.dokumen.DokumenDetailActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DashboardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DashboardFragment : Fragment(), BeritaClickListener, DokumenClickListener {


    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding: FragmentAdminDashboardBinding

    private lateinit var dokumenAdapter: DokumenAdapter
    private lateinit var beritaAdapter: BeritaAdapter

    private var userId: Int = 0


    private var listener: OnFragmentInteractionListener? = null

    private val api by lazy { ApiRetrofit().endpoint }

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
        binding = FragmentAdminDashboardBinding.inflate(inflater, container, false)


        initAdapter()
        setupListener()
        return binding.root
    }

    override fun onStart(){
        super.onStart()
        getBerita()
        getDokumen()
    }

    fun initAdapter(){
        beritaAdapter = BeritaAdapter(arrayListOf(), this)
        dokumenAdapter = DokumenAdapter(arrayListOf(), this)
        binding.rvBerita.layoutManager = LinearLayoutManager(context)
        binding.rvDokumen.layoutManager = LinearLayoutManager(context)
        binding.rvBerita.adapter = beritaAdapter
        binding.rvDokumen.adapter = dokumenAdapter
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private fun setupListener() {
        with(binding) {
            btnListDocs.setOnClickListener {
                listener?.onFragmentInteraction(DokumenDaftarFragment())
            }
        }
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(fragment: Fragment)
    }

    private fun replaceFragment(fragment: Fragment, id: Int) {
        val fragmentManager = parentFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main, fragment)
        fragmentTransaction.commit()
    }

    private fun getBerita() {
        api.get_berita(userId).enqueue(object : Callback<BeritaModel> {
            override fun onResponse(call: Call<BeritaModel>, response: Response<BeritaModel>) {
                if (response.isSuccessful) {
                    response.body()?.berita?.let { listData ->
                        beritaAdapter.setData(listData)
                        Log.d("HomeFragment", "Berita data: ${listData}")
                    } ?: run {
                        Log.e("HomeFragment", "Empty Berita response")
                    }
                } else {
                    Log.e("HomeFragment", "Error response: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<BeritaModel>, t: Throwable) {
                Log.e("HomeFragment", "Failed to fetch Berita: ${t.message}")
            }
        })
    }

    private fun getDokumen() {
        api.get_dokumen(userId).enqueue(object : Callback<DokumenModel> {
            override fun onResponse(call: Call<DokumenModel>, response: Response<DokumenModel>) {
                if (response.isSuccessful) {
                    response.body()?.dokumen?.let { listData ->
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DashboardFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DashboardFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onClick(berita: BeritaModel.Data) {
        val intent = Intent(requireContext(), BeritaDetailActivity::class.java).apply {
            putExtra(BERITA_ID_EXTRA, berita.id)
            putExtra(BERITA_DATA_EXTRA, berita)
        }
        startActivity(intent)
    }

    override fun onClick(dokumen: DokumenModel.Data) {
        val intent = Intent(requireContext(), DokumenDetailActivity::class.java).apply {
            putExtra(DOKUMEN_ID_EKSTRA, dokumen.id)
            putExtra(DOKUMEN_DATA_EXTRA, dokumen)
        }
        startActivity(intent)
    }
}