package com.krispy.kelompok1_jdih.data.helper


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



class DBOpenHelper (context: Context): SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION){
    override fun onCreate(db: SQLiteDatabase?) {
        val tDocs = "CREATE TABLE docs (id INTEGER PRIMARY KEY AUTOINCREMENT, tipe TEXT, judul TEXT, nomor TEXT, status TEXT, file BLOB )"
        val tNews = "CREATE TABLE news (id INTEGER PRIMARY KEY AUTOINCREMENT, tipe TEXT, isi TEXT, gambar BLOB )"
        val tUser = "CREATE TABLE user (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, password TEXT, nama TEXT, email TEXT, foto BLOB )"
        db?.execSQL(tDocs)
        db?.execSQL(tNews)
        db?.execSQL(tUser)
    }
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS docs")
        db?.execSQL("DROP TABLE IF EXISTS news")
        db?.execSQL("DROP TABLE IF EXISTS user")
        onCreate(db)
    }
    companion object{
        const val DB_NAME = "test.db"
        const val DB_VERSION = 1
    }



}