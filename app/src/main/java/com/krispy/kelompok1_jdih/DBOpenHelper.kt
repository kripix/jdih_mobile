package com.krispy.kelompok1_jdih

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
class DBOpenHelper(context: Context): SQLiteOpenHelper(context, "DB", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        val tDokumen = "CREATE TABLE tDokumen(id INTEGER PRIMARY KEY AUTOINCREMENT, judul TEXT not null, tanggal DATE, jenis_id INTEGER, status_id INTEHER, tempat TEXT)"
        val tBerite = "CREATE TABLE tBerita(id INTEGER PRIMARY KEY AUTOINCREMENT, judul TEXT, Isi Text, tanggal DATE)"
        val tJenis = "CREATE TABLE tJenis(id INTEGER PRIMARY KEY AUTOINCREMENT, jenis TEXT)"
        val tStatus = "CREATE TABLE tStatus(id INTEGER PRIMARY KEY AUTOINCREMENT, status TEXT)"
        val tCount = "CREATE TABLE tCount(id INTEGER PRIMARY KEY AUTOINCREMENT, dokumen_id INTEGER, viewed INTEGER, downloaded INTEGER)"
        val insertJenis = "INSERT INTO tJenis(jenis) VALUES('Peraturan'),('Naskah Akademik'),('Artikel'),('Putusan')"
        db?.execSQL(tDokumen)
        db?.execSQL(tBerite)
        db?.execSQL(tJenis)
        db?.execSQL(tStatus)
        db?.execSQL(tCount)
        db?.execSQL(insertJenis)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    companion object {
        val DB_NAME = "DB"
        val DB_VERSION = 1

    }

}