package com.krispy.kelompok1_jdih.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

val USER_ID_EKSTRA = "user_id"
val USER_DATA_EKSTRA = "user_data"

data class UserModel(
    @SerializedName("id")
    val id: Int,

    @SerializedName("username")
    val username: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("nama_lengkap")
    val nama_lengkap: String,

    @SerializedName("url_foto")
    val url_foto: String
)
