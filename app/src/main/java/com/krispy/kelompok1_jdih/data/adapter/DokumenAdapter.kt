package com.krispy.kelompok1_jdih.data.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.krispy.kelompok1_jdih.data.listener.DokumenClickListener
import com.krispy.kelompok1_jdih.data.model.DokumenModel
import com.krispy.kelompok1_jdih.databinding.ItemDokumenBinding

class DokumenAdapter(
    private val list: ArrayList<DokumenModel.Data>,
    private val listener: DokumenClickListener
) : RecyclerView.Adapter<DokumenAdapter.ViewHolder>() {

    class ViewHolder(
        private val binding: ItemDokumenBinding,
        private val listener: DokumenClickListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: DokumenModel.Data) {
            with(binding) {
                tvTgl.text = data.tgl_penetapan
                tvStatus.text = data.status
                tvTipe.text = data.tipe
                tvJudul.text = data.judul
                itemDokumen.setOnClickListener {
                    listener.onClick(data)
                }
            }
            Log.d("DokumenAdapter", "Data: $data")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = ItemDokumenBinding.inflate(from, parent, false)
        return ViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size

    fun setData(data: List<DokumenModel.Data>) {
        list.clear()
        list.addAll(data)
        Log.d("CekAdapter", "Data set: ${list.size} items")
        notifyDataSetChanged()
    }
}
