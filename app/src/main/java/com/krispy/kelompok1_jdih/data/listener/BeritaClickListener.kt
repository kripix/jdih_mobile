package com.krispy.kelompok1_jdih.data.listener

import com.krispy.kelompok1_jdih.data.model.BeritaModel

interface BeritaClickListener {
    fun onClick(berita: BeritaModel.Data)
}