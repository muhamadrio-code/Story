package com.riopermana.story.ui.stories

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import coil.load
import com.riopermana.story.databinding.FragmentNewStoryBinding
import com.riopermana.story.ui.utils.createCustomTempFile
import com.riopermana.story.utils.showToast

class NewStoryFragment : Fragment() {

    private lateinit var binding: FragmentNewStoryBinding
    private var imageUri : Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewStoryBinding.inflate(inflater, container, false)
        binding.addImageGallery.setOnClickListener {
            startGallery()
        }
        binding.openCamera.setOnClickListener {
            checkCameraPermissionOrOpenCamera()
        }

        binding.buttonAdd.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.ibActionBack.setOnClickListener {
            findNavController().popBackStack()
        }
        return binding.root
    }

    private fun startGallery() {
        val mime = "image/*"
        launcherIntentGallery.launch(mime)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            binding.ivStoryImage.load(uri)
        }
    }

    private val cameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            openCamera()
        } else {
            requireContext().showToast("Camera permission has been denied")
        }
    }

    private fun checkCameraPermissionOrOpenCamera() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            cameraPermission.launch(Manifest.permission.CAMERA)
        } else {
            openCamera()
        }
    }

    private fun createUri(): Uri {
        val file = createCustomTempFile(requireContext())
        return FileProvider.getUriForFile(
            requireActivity().applicationContext,
            "com.riopermana.story.fileProvider",
            file
        )
    }

    private fun openCamera() {
        imageUri = createUri()
        launcherIntentCameraX.launch(imageUri)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            binding.ivStoryImage.load(imageUri)
        }
    }

}