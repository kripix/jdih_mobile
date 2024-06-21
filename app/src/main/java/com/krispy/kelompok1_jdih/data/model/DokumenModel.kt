package com.krispy.kelompok1_jdih.data.model

import java.io.Serializable

val DOKUMEN_ID_EKSTRA = "dokumen_id"
val DOKUMEN_DATA_EXTRA = "dokumen_data"

data class DokumenModel (
    val dokumen: List<Data>
) : Serializable {
    data class Data (
        val id: Int,
        val tipe_id: Int,
        val tipe: String,
        val judul: String,
        val nomor: String,
        val sumber: String,
        val penandatanganan: String,
        val tempat_penetapan: String,
        val tgl_penetapan: String,
        val status: String,
        val file: String,
        val status_id: Int,
        val sifat_id: Int,
        val user_id: Int,
        val url_file: String
    ) : Serializable
}