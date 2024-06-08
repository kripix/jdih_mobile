package com.krispy.kelompok1_jdih.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.krispy.kelompok1_jdih.R
import com.krispy.kelompok1_jdih.data.room.Dokumen

class item_docs (var Dokumen: ArrayList<Dokumen>, var listener: OnAdapterListener) :
    RecyclerView.Adapter<item_docs.DoksViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoksViewHolder {
        return DoksViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_dokumen,
                    parent,
                    false
                )
        )
    }
    override fun getItemCount() = Dokumen.size
    override fun onBindViewHolder(holder: DoksViewHolder, position: Int) {
        val dokumen = Dokumen[position]
        holder.view.findViewById<TextView>(R.id.id_judul).text = dokumen.judul
        holder.view.findViewById<TextView>(R.id.id_status).text = dokumen.status
        holder.view.findViewById<TextView>(R.id.id_jenis).text = dokumen.tipe
    }

    class DoksViewHolder(val view: View) : RecyclerView.ViewHolder(view)
    fun setData(newList: List<Dokumen>) {
        Dokumen.clear()
        Dokumen.addAll(newList)
    }
    interface OnAdapterListener {
        fun onClick(dokumen: Dokumen)
    }
}