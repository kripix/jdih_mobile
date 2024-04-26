package com.krispy.kelompok1_jdih.room


import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DokumenDAO {
    @Insert
    suspend fun addDokumen(dokumen: Dokumen)
    @Update
    suspend fun updateDokumen(dokumen: Dokumen)
    @Delete
    suspend fun deleteDokumen(dokumen: Dokumen)

    @Query("SELECT * FROM dokumen ORDER BY judul ASC")
    suspend fun getDokumens() : List<Dokumen>

    @Query("SELECT * FROM dokumen WHERE id =:idDokumen")
    suspend fun getDokumen(idDokumen: Int) : List<Dokumen>

    @Query("SELECT * FROM dokumen WHERE tipe =:judulDokumen")
    suspend fun getDokumenByJudul(judulDokumen: String) : List<Dokumen>

    @Query("SELECT judul FROM dokumen WHERE judul LIKE '%' || :judulDokumen || '%' ORDER BY judul ASC LIMIT 5")
    fun getDokumenSuggestions(judulDokumen: String): Flow<List<String>>

    @Query("SELECT * FROM dokumen WHERE tipe =:tipeDokumen AND status=:statusDokumen")
    suspend fun getDokumenByFilter(tipeDokumen: String, statusDokumen:String) : List<Dokumen>
//
//    @Query("SELECT COUNT(*) FROM dokumen WHERE tipe = 'PUU'")
//    suspend fun countDokumenPeraturan() : List<Dokumen>
//    @Query("SELECT COUNT(*) FROM dokumen WHERE tipe = 'Naskah Akademik'")
//    suspend fun countDokumenMonografi() : List<Dokumen>
//    @Query("SELECT COUNT(*) FROM dokumen WHERE tipe = 'Artikel'")
//    suspend fun countDokumenArtikel() : List<Dokumen>
//    @Query("SELECT COUNT(*) FROM dokumen WHERE tipe = 'Putusan'")
//    suspend fun countDokumenPutusan() : List<Dokumen>
}