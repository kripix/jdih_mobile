package com.krispy.kelompok1_jdih.data.api

import com.krispy.kelompok1_jdih.data.model.BeritaModel
import com.krispy.kelompok1_jdih.data.model.DokumenModel
import com.krispy.kelompok1_jdih.data.model.ResponseModel
import com.krispy.kelompok1_jdih.data.model.TipeModel
import com.krispy.kelompok1_jdih.data.model.UserModel
import com.krispy.kelompok1_jdih.data.model.UserResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiEndpoint {


    @GET("get_user.php")
    fun getUser(
        @Query("username") username: String,
        @Query("password") password: String
    ): Call<UserResponse>

    @GET("get_dokumen.php")
    fun get_dokumen (@Query("userId") userId : Int) : Call<DokumenModel>

    @GET("get_berita.php")
    fun get_berita(
        @Query("userId") userId : Int
    ): Call<BeritaModel>

    @FormUrlEncoded
    @POST("create_dokumen.php")
    fun create_dokumen(
        @Field("judul") judul : String,
        @Field("nomor") nomor : String,
        @Field("sumber") sumber : String,
        @Field("penandatanganan") penandatanganan : String,
        @Field("tempat_penetapan") tempat_penetapan : String,
        @Field("tgl_penetapan") tgl_penetapan : String,
        @Field("tipe_id") tipe_id : Int,
        @Field("status_id") status_id : Int,
        @Field("sifat_id") sifat_id : Int,
        @Field("url_file") url_file : String
    ): Call<ResponseModel>

    @FormUrlEncoded
    @POST("create_berita.php")
    fun create_berita(
        @Field("judul") judul : String,
        @Field("isi") isi : String,
        @Field("url_cover") url_cover : String
    ) : Call<ResponseModel>

    @FormUrlEncoded
    @POST("update_dokumen.php")
    fun update_dokumen(
        @Field("id") id : Int,
        @Field("judul") judul : String,
        @Field("nomor") nomor : String,
        @Field("sumber") sumber : String,
        @Field("penandatanganan") penandatanganan : String,
        @Field("tempat_penetapan") tempat_penetapan : String,
        @Field("tgl_penetapan") tgl_penetapan : String,
        @Field("tipe_id") tipe_id : Int,
        @Field("status_id") status_id : Int,
        @Field("sifat_id") sifat_id : Int,
        @Field("url_file") url_file : String
    ) : Call<ResponseModel>

    @FormUrlEncoded
    @POST("update_berita.php")
    fun update_berita(
        @Field("id") id : Int,
        @Field("judul") judul : String,
        @Field("isi") isi : String,
        @Field("url_cover") url_cover : String
    ) : Call<ResponseModel>

    @FormUrlEncoded
    @POST("update_user.php")
    fun update_profile(
        @Field("id") id : Int,
        @Field("nama_lengkap") nama_lengkap : String,
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("url_foto") url_profile : String
    ) : Call<ResponseModel>

    @FormUrlEncoded
    @POST("delete_dokumen.php")
    fun delete_dokumen(
        @Field("id") id : Int
    ) : Call<ResponseModel>

    @FormUrlEncoded
    @POST("delete_berita.php")
    fun delete_berita(
        @Field("id") id : Int
    ) : Call<ResponseModel>


    @GET("get_tipe.php")
    suspend fun getTipe(): TipeModel


    @GET
    suspend fun downloadFile(@Url fileUrl: String): Response<DokumenModel>


}