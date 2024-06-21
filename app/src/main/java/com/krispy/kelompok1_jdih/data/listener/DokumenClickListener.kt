package com.krispy.kelompok1_jdih.data.listener

import com.krispy.kelompok1_jdih.data.model.DokumenModel

interface DokumenClickListener {
    fun onClick(dokumen: DokumenModel.Data)
}