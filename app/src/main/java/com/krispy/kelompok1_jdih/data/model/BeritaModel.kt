package com.krispy.kelompok1_jdih.data.model

import java.io.Serializable

val BERITA_ID_EXTRA = "berita_id"
val BERITA_DATA_EXTRA = "berita_data"

data class BeritaModel (
    val berita: List<Data>
) : Serializable {
    data class Data(
        val id: Int,
        val judul: String,
        val isi: String,
        val user_id: Int,
        val url_cover: String,
        val tgl: String
    ) : Serializable
}
