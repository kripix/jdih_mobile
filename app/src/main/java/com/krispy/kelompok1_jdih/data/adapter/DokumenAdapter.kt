package com.krispy.kelompok1_jdih.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.krispy.kelompok1_jdih.R
import com.krispy.kelompok1_jdih.data.room.Dokumen

class DokumenAdapter (
    var dokumen: ArrayList<Dokumen>,
    private var listener: OnAdapterListener
) :
    RecyclerView.Adapter<DokumenAdapter.DokumenViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DokumenViewHolder {
        return DokumenViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.adapter_dokumen,
                    parent,
                    false
                )
        )
    }


    override fun getItemCount() = dokumen.size
    override fun onBindViewHolder(holder: DokumenViewHolder, position: Int) {
        val dokumen = dokumen[position]
        holder.view.findViewById<TextView>(R.id.text_title).text = dokumen.judul

        holder.view.findViewById<TextView>(R.id.text_title).setOnClickListener {
            listener.onClick(dokumen)
        }
        holder.view.findViewById<ImageView>(R.id.icon_edit).setOnClickListener {
            listener.onUpdate(dokumen)
        }
        holder.view.findViewById<ImageView>(R.id.icon_delete).setOnClickListener {
            listener.onDelete(dokumen)
        }
    }
    class DokumenViewHolder(val view: View) : RecyclerView.ViewHolder(view)
    fun setData(newList: List<Dokumen>) {
        dokumen.clear()
        dokumen.addAll(newList)
    }
    interface OnAdapterListener {
        fun onClick(dokumen: Dokumen)
        fun onUpdate(dokumen: Dokumen)
        fun onDelete(dokumen: Dokumen)
    }



}