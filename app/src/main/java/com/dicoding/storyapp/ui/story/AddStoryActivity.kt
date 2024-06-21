package com.dicoding.storyapp.ui.story

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.dicoding.storyapp.R
import com.dicoding.storyapp.databinding.ActivityAddStoryBinding
import java.io.ByteArrayOutputStream

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding

    private var currentImageUri: Uri? = null
    val REQUEST_CODE_CAMERA = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.custom_action_bar)

        val titleTextView = supportActionBar?.customView?.findViewById<TextView>(R.id.action_bar_title)
        titleTextView?.text = "Add Story"

        binding.btnGallery.setOnClickListener {
            startGallery()
        }

        binding.btnCamera.setOnClickListener {
            checkCameraPermission()
        }

        binding.btnUpload.setOnClickListener {
            uploadStory()
        }
    }

    private fun uploadStory() {
        showToast("Upload clicked")
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startCamera()
        } else {
            showToast("Camera permission is required to use the camera.")
        }
    }

    private fun startCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        try {
            startActivityForResult(takePictureIntent, REQUEST_CODE_CAMERA)
        }catch (e: Exception) {
            showToast("Error: ${e.message}")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_CAMERA && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as? Bitmap
            imageBitmap?.let {
                val uri = getImageUri(this, it)
                uri?.let {
                    currentImageUri = it
                    showImage(it)
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            inImage,
            "Title",
            null
        )
        return Uri.parse(path)
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage(uri)
        } else {
            showToast("No media selected")
        }
    }

    private fun showImage(uri: Uri?) {
        uri?.let {
            binding.ivAddStoryImage.setImageURI(it)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
