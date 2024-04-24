package com.krispy.kelompok1_jdih

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CursorAdapter
import android.widget.ListAdapter
import android.widget.Toast
import androidx.cursoradapter.widget.SimpleCursorAdapter

import com.krispy.kelompok1_jdih.databinding.FragmentHomeBinding
import java.util.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?


    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
//        db = this,parent.getDB()

        binding.btnListDocs.setOnClickListener{
            val intent = Intent(requireActivity(), MainActivity::class.java)
            intent.putExtra("list", "Docs")
            startActivity(intent)
        }

//        binding.listNewDocs.setOnItemClickListener{parent,view,p1,id->
//            val idDocs = this.DocumentList.getItem(p1)
//            val intent = Intent(requireActivity(), DetailDocsActivity::class.java)
//            intent.putExtra("this_docs" , "$idDocs")
//            startActivity(intent)
//        }

        binding.btnListNews.setOnClickListener{
            val intent = Intent(requireActivity(), NewsActivity::class.java)
            startActivity(intent)
        }

//        binding.listNews.setOnItemClickListener{parent,view,p1,id->
//            val idNews = this.DocumentList.getItem(p1)
//            val intent = Intent(requireActivity(), DetailNewsActivity::class.java)
//            intent.putExtra("this_news" , "$idNews")
//            startActivity(intent)
//        }



        return binding.root
    }

    private lateinit var adapter : ListAdapter
//    fun listDataDocs(){
//        val cursor : Cursor = db.query("docs", arrayOf("nomor","judul"), null, null, null, null, "tanggal asc")
//        binding.listNewDocs.adapter = adapter
//    }

//    override fun onStart() {
//        super.onStart()
//        listDataDocs()
//    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}