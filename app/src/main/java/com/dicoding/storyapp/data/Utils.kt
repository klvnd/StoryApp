package com.dicoding.storyapp.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import java.io.ByteArrayOutputStream
import java.io.File

fun uriToFile(context: Context, uri: Uri): File {
    val file = File(context.cacheDir, "temp_image.jpg")
    context.contentResolver.openInputStream(uri)?.use { inputStream ->
        file.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
    }
    return file
}

fun getImageUri(context: Context, image: Bitmap): Uri? {
    val bytes = ByteArrayOutputStream()
    image.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    val path = MediaStore.Images.Media.insertImage(context.contentResolver, image, "Title", null)
    return Uri.parse(path)
}

fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun showLoading(isLoading: Boolean, view: View) {
    view.visibility = if (isLoading) View.VISIBLE else View.GONE
}
