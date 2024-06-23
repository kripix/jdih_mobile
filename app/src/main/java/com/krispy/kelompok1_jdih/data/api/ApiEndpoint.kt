package com.krispy.kelompok1_jdih.data.api

import com.krispy.kelompok1_jdih.data.model.BeritaModel
import com.krispy.kelompok1_jdih.data.model.DokumenModel
import com.krispy.kelompok1_jdih.data.model.ResponseModel
import com.krispy.kelompok1_jdih.data.model.TipeModel
import com.krispy.kelompok1_jdih.data.model.UploadResponse
import com.krispy.kelompok1_jdih.data.model.UserModel
import com.krispy.kelompok1_jdih.data.model.UserResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiEndpoint {

    @GET("get_user.php")
    fun getUser(
        @Query("username") username: String,
        @Query("password") password: String
    ): Call<UserResponse>



    @GET("get_berita.php")
    fun get_berita(
        @Query("userId") userId : Int
    ): Call<BeritaModel>
    @GET("get_dokumen.php")

    fun get_dokumen (
        @Query("user_id") user_id : Int
    ) : Call<DokumenModel>

    @Multipart
    @POST("create_dokumen.php")
    fun create_dokumen(
        @Part("judul") judul : RequestBody,
        @Part("nomor") nomor : RequestBody,
        @Part("sumber") sumber : RequestBody,
        @Part("penandatanganan") penandatanganan : RequestBody,
        @Part("tempat_penetapan") tempat_penetapan : RequestBody,
        @Part("tgl_penetapan") tgl_penetapan : RequestBody,
        @Part("tipe_id") tipe_id : RequestBody,
        @Part("status_id") status_id : RequestBody,
        @Part("sifat_id") sifat_id : RequestBody,
        @Part dokumen: MultipartBody.Part
    ): Call<ResponseModel>

    @Multipart
    @POST("update_dokumen.php")
    fun update_dokumen(
        @Part("id") id : RequestBody,
        @Part("judul") judul : RequestBody,
        @Part("nomor") nomor : RequestBody,
        @Part("sumber") sumber : RequestBody,
        @Part("penandatanganan") penandatanganan : RequestBody,
        @Part("tempat_penetapan") tempat_penetapan : RequestBody,
        @Part("tgl_penetapan") tgl_penetapan : RequestBody,
        @Part("tipe_id") tipe_id : RequestBody,
        @Part("status_id") status_id : RequestBody,
        @Part("sifat_id") sifat_id : RequestBody,
        @Part("url_cover") url_cover : RequestBody,
        @Part dokumen: MultipartBody.Part?
        ) : Call<ResponseModel>


    @Multipart
    @POST("create_berita.php")
    fun createBerita(
        @Part("judul") judul: RequestBody,
        @Part("isi") isi: RequestBody,
        @Part cover: MultipartBody.Part
    ): Call<ResponseModel>



    @Multipart
    @POST("update_berita.php")
    fun updateBerita(
        @Part("id") id: RequestBody,
        @Part("judul") judul: RequestBody,
        @Part("isi") isi: RequestBody,
        @Part("url_cover") url_cover: RequestBody,
        @Part cover: MultipartBody.Part?
    ): Call<ResponseModel>

    @Multipart
    @POST("update_user.php")
    fun updateProfile(
        @Part("id") id: RequestBody,
        @Part("nama_lengkap") nama_lengkap: RequestBody,
        @Part("username") username: RequestBody,
        @Part("password") password: RequestBody,
        @Part("url_foto") url_foto:  RequestBody,
        @Part foto: MultipartBody.Part?
    ): Call<ResponseModel>


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