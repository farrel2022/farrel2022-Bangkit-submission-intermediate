package com.farrelfeno.substoryappintermediate.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.farrelfeno.substoryappintermediate.result.Result
import com.farrelfeno.substoryappintermediate.databinding.ActivityAddStoryBinding
import com.farrelfeno.substoryappintermediate.factory.ViewModelFactory
import com.farrelfeno.substoryappintermediate.pref.UserPreference
import com.farrelfeno.substoryappintermediate.pref.dataStore
import com.farrelfeno.substoryappintermediate.ui.model.AddStoryViewModel
import com.farrelfeno.substoryappintermediate.utils.reduceFileImage
import com.farrelfeno.substoryappintermediate.utils.uriToFile


class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding

    private val viewModel: AddStoryViewModel by viewModels {
        ViewModelFactory.getInstance(
            applicationContext,
            UserPreference.getInstance(dataStore)
        )
    }

    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            buttonCamera.setOnClickListener { startCamera() }
            buttonGallery.setOnClickListener { startGallery() }
            buttonAdd.setOnClickListener { setupButton() }
        }
    }

    override fun onResume() {
        super.onResume()
        checkSession()
    }

    private fun checkSession() {
        viewModel.tokenAvailable().observe(this) {
            if (it == "null") {
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }

    private fun startCamera() {
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        } else {
            val intent = Intent(this, CaptureStory::class.java)
            launcherIntentCamera.launch(intent)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CaptureStory.CAMERA_RESULT) {
            currentImageUri = it.data?.getStringExtra(CaptureStory.EXTRA_CAMERA_IMAGE)?.toUri()
            showImage()
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.ivStory.setImageURI(it)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a picture")
        launcherGallery.launch(chooser)
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val selectedImage: Uri = it.data?.data as Uri
            if (selectedImage != null) {
                currentImageUri = selectedImage
                showImage()
            }
        }
    }

    private fun setupButton() {
        viewModel.tokenAvailable().observe(this) {
            if (it == "null") {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            } else {
                uploadImage("Bearer $it")
            }
        }
    }

    private fun uploadImage(token: String) {
        Log.d("Upload", "Starting Image Upload")
        if (binding.edAddDescription.text.isNullOrEmpty()) {
            Toast.makeText(this, "Description cannot be empty", Toast.LENGTH_SHORT).show()
        } else {
            if (currentImageUri != null) {
                binding.progressBar.visibility = View.VISIBLE
                currentImageUri?.let { uri ->
                    val imageFile = uriToFile(uri, this).reduceFileImage()
                    val description = binding.edAddDescription.text.toString()
                    val result = viewModel.postStory(token, imageFile, description)
                    result.observe(this) {
                        when (it) {
                            is Result.Loading -> {
                                binding.progressBar.visibility = View.VISIBLE
                            }

                            is Result.Error -> {
                                binding.progressBar.visibility = View.INVISIBLE
                                val error = it.error
                                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                            }

                            is Result.Success -> {
                                binding.progressBar.visibility = View.INVISIBLE
                                Toast.makeText(this, "Story uploaded", Toast.LENGTH_SHORT)
                                    .show()
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }

                            else -> {}
                        }
                    }
                }
            }
        }
    }

}