package com.krispy.kelompok1_jdih.data.model

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("result")
    val result: List<UserModel>,

    @SerializedName("message")
    val message: String?
)
