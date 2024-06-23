package com.krispy.kelompok1_jdih.data.helper

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MediaHelper(private val activity: Activity) {

    private val REQ_CODE_GALLERY = 100
    private val REQ_CODE_CAMERA = 101

    fun getRcGallery(): Int {
        return REQ_CODE_GALLERY
    }

    fun getRcCamera(): Int {
        return REQ_CODE_CAMERA
    }

    fun bitmapToString(bmp: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun getBitmapToString(uri: Uri?, imgV: ImageView): String {
        var bmp = MediaStore.Images.Media.getBitmap(activity.contentResolver, uri)
        val dim = 720
        if (bmp.height > bmp.width) {
            bmp = Bitmap.createScaledBitmap(bmp, (bmp.width * dim).div(bmp.height), dim, true)
        } else {
            bmp = Bitmap.createScaledBitmap(bmp, dim, (bmp.height * dim).div(bmp.width), true)
        }
        imgV.setImageBitmap(bmp)
        return bitmapToString(bmp)
    }

    fun getRealPathFromURI(contentUri: Uri): String? {
        val cursor = activity.contentResolver.query(contentUri, null, null, null, null)
        return if (cursor == null) {
            contentUri.path
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            val realPath = cursor.getString(idx)
            cursor.close()
            realPath
        }
    }

    fun saveImageToStorage(bitmap: Bitmap): String {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "JPEG_${timeStamp}.jpg"
        val path = activity.getExternalFilesDir(null)
        val file = File(path, fileName)

        try {
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return file.absolutePath
    }

    fun intentGallery() {
        val options = arrayOf<CharSequence>("Ambil Foto", "Pilih dari Galeri", "Batal")

        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Pilih Sumber Gambar")
        builder.setItems(options) { dialog, item ->
            when {
                options[item] == "Ambil Foto" -> {
                    dispatchTakePictureIntent()
                }
                options[item] == "Pilih dari Galeri" -> {
                    val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activity.startActivityForResult(pickPhoto, REQ_CODE_GALLERY)
                }
                options[item] == "Batal" -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }

    private var currentPhotoPath: String = ""

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = activity.getExternalFilesDir("images")
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(activity.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    ex.printStackTrace()
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        activity,
                        "${activity.packageName}.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    activity.startActivityForResult(takePictureIntent, REQ_CODE_CAMERA)
                }
            }
        }
    }
}
