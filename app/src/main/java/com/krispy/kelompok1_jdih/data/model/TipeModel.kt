package com.krispy.kelompok1_jdih.data.model

import java.io.Serializable

data class TipeModel (
    val tipe: List<Data>
) : Serializable {
    data class Data(
        val id: Int,
        val tipe_dokumen: String
    ) : Serializable
}

//data class TipeModel (
//    val id: Int,
//    val tipe_dokumen: String
//) : Serializable