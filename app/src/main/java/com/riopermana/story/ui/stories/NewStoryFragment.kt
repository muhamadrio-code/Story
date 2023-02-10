package com.riopermana.story.ui.stories

import android.Manifest
import android.app.AlertDialog
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
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.riopermana.story.R
import com.riopermana.story.databinding.FragmentNewStoryBinding
import com.riopermana.story.ui.dialogs.LoadingDialog
import com.riopermana.story.ui.utils.*
import com.riopermana.story.utils.showToast
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class NewStoryFragment : Fragment() {

    private lateinit var binding: FragmentNewStoryBinding
    private val viewModel: NewStoryViewModel by viewModels()
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewStoryBinding.inflate(inflater, container, false)
        loadingDialog = LoadingDialog(requireContext())
        subscribeObserver()
        setupListener()
        return binding.root
    }

    private fun setupListener() {
        binding.addImageGallery.setOnClickListener {
            startGallery()
        }
        binding.openCamera.setOnClickListener {
            checkCameraPermissionOrOpenCamera()
        }

        binding.buttonAdd.setOnClickListener {
            if (viewModel.currentUri == null) {
                val dialog = AlertDialog.Builder(requireContext())
                    .setMessage(R.string.upload_warning)
                    .create()
                dialog.show()
            } else {
                uploadImage()
            }
        }
        binding.ibActionBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun subscribeObserver() {
        viewModel.observableUploadResponse.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                requireContext().showToast(R.string.upload_success)
                findNavController().popBackStack()
            }
        }

        viewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UiState.OnError -> {
                    hideLoading()
                    showError(uiState.message)
                }
                is UiState.OnLoading -> {
                    showLoading()
                }
                is UiState.OnPostLoading -> {
                    hideLoading()
                }
            }
        }

        viewModel.observableUri.observe(viewLifecycleOwner) { uri ->
            uri?.let {
                binding.ivStoryImage.load(uri)
            }
        }
    }

    private fun showError(errorMessageRes: ErrorMessageRes) {
        requireContext().showToast(errorMessageRes.resId)
    }

    private fun showLoading() {
        loadingDialog.show()
    }

    private fun hideLoading() {
        loadingDialog.dismiss()
    }

    private fun startGallery() {
        val mime = "image/*"
        launcherIntentGallery.launch(mime)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            viewModel.setCurrentUri(it)
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
        val tempUri = createUri()
        viewModel.setTempUri(tempUri)
        launcherIntentCameraX.launch(tempUri)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        viewModel.onPostTakePicture(isSuccess)
    }

    private fun uploadImage() {
        viewModel.currentUri?.let { uri ->
            val file = reduceFileImage(uriToFile(uri, requireContext()))
            val description =
                "Ini adalah deksripsi gambar".toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            viewModel.uploadFile(imageMultipart, description)
        }
    }

}