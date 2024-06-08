package com.krispy.kelompok1_jdih.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Dokumen(
    @PrimaryKey(autoGenerate = true)
    val id : Int,
    val tipe: String,
    val judul: String,
    val tgl: String,
    val status: String,
    val file: String
)