package com.krispy.kelompok1_jdih.data.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.krispy.kelompok1_jdih.data.listener.BeritaClickListener
import com.krispy.kelompok1_jdih.data.model.BeritaModel
import com.krispy.kelompok1_jdih.databinding.ItemBeritaBinding
import com.squareup.picasso.Picasso

class BeritaAdapter(
    private val list: ArrayList<BeritaModel.Data>,
    private val listener: BeritaClickListener
) : RecyclerView.Adapter<BeritaAdapter.ViewHolder>() {

    class ViewHolder(
        private val binding: ItemBeritaBinding,
        private val listener: BeritaClickListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: BeritaModel.Data) {
            with(binding) {
                tvJudul.text = data.judul
//                tvTgl.text = data.tgl ?: "N/A"

                itemBerita.setOnClickListener {
                    listener.onClick(data)
                }
            }
            Picasso.get().load(data.url_cover).into(binding.ivCover)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = ItemBeritaBinding.inflate(from, parent, false)
        return ViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    fun setData(data: List<BeritaModel.Data>) {
        list.clear()
        list.addAll(data)
        Log.d("CekAdapter", "Data set: ${list.size} items")
        notifyDataSetChanged()
    }
}

