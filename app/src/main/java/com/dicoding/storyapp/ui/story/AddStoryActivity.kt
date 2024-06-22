package com.dicoding.storyapp.ui.story

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.DataStoreManager
import com.dicoding.storyapp.data.api.ApiConfig
import com.dicoding.storyapp.data.response.ErrorResponse
import com.dicoding.storyapp.databinding.ActivityAddStoryBinding
import com.dicoding.storyapp.ui.main.MainActivity
import com.dicoding.storyapp.utils.*
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding

    private var currentImageUri: Uri? = null
    private val REQUEST_CODE_CAMERA = 100
    private val MAXIMAL_SIZE = 1000000

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

        binding.etDescription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                checkFieldsForEmptyValues()
            }
        })
    }

    fun File.reduceFileImage(): File {
        val file = this
        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality = 100
        var streamLength: Int
        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > MAXIMAL_SIZE)
        bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
        return file
    }

    private fun uploadStory() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(this, uri).reduceFileImage()
            val description = binding.etDescription.text.toString()
            showLoading(true, binding.progressBar)

            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )

            val dataStoreManager = DataStoreManager(this)
            lifecycleScope.launch {
                try {
                    val token = dataStoreManager.tokenFlow.first() ?: ""
                    val apiService = ApiConfig.getApiService(token = token)
                    val errorResponse = apiService.addStories(requestBody, multipartBody)
                    errorResponse.message?.let { showToast(this@AddStoryActivity, it) }
                    showLoading(false, binding.progressBar)
                    val intent = Intent(this@AddStoryActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                } catch (e: HttpException) {
                    val errorBody = e.response()?.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                    errorResponse.message?.let { showToast(this@AddStoryActivity, it) }
                    showLoading(false, binding.progressBar)
                }
            }
        } ?: showToast(this, "Please select an image")
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
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
            showToast(this, "Camera permission is required to use the camera.")
        }
    }

    private fun startCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_CODE_CAMERA)
        } catch (e: Exception) {
            showToast(this, "Error: ${e.message}")
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
                    checkFieldsForEmptyValues()
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
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
            showToast(this, "No media selected")
        }
    }

    private fun showImage(uri: Uri?) {
        uri?.let {
            binding.ivAddStoryImage.setImageURI(it)
        }
    }

    private fun checkFieldsForEmptyValues() {
        val description = binding.etDescription.text.toString()
        binding.btnUpload.isEnabled = currentImageUri != null && description.isNotEmpty()
    }
}