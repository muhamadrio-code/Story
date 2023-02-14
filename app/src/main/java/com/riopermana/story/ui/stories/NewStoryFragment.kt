package com.riopermana.story.ui.stories

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.riopermana.story.R
import com.riopermana.story.data.local.DataStoreUtil
import com.riopermana.story.data.local.PreferencesKeys
import com.riopermana.story.data.local.sessionDataStore
import com.riopermana.story.data.local.settingsDataStore
import com.riopermana.story.databinding.FragmentNewStoryBinding
import com.riopermana.story.ui.dialogs.LoadingDialog
import com.riopermana.story.ui.utils.*
import com.riopermana.story.utils.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class NewStoryFragment : Fragment() {

    private var _binding: FragmentNewStoryBinding? = null
    private val binding: FragmentNewStoryBinding get() = _binding!!

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest

    private val viewModel: NewStoryViewModel by viewModels()
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewStoryBinding.inflate(inflater, container, false)
        loadingDialog = LoadingDialog(requireContext())
        setupListener()
        subscribeObserver()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        return binding.root
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY, 1000L
        ).build()

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(requireActivity())
        client.checkLocationSettings(builder.build())
            .addOnSuccessListener {
                getMyLastLocation()
            }
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    runCatching {
                        resolutionLauncher.launch(
                            IntentSenderRequest.Builder(exception.resolution).build()
                        )
                    }
                }
            }
    }

    private val resolutionLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            when (result.resultCode) {
                RESULT_OK -> {
                    binding.toggleLocation.isChecked = true
                    getMyLastLocation()
                }
                RESULT_CANCELED -> binding.toggleLocation.isChecked = false
            }
        }

    @SuppressLint("MissingPermission")
    private fun getMyLastLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                viewModel.setLastLocation(location)
            }
        }
    }

    private fun setupListener() {
        binding.addImageGallery.setOnClickListener {
            startGallery()
        }
        binding.openCamera.setOnClickListener {
            checkCameraPermissionOrOpenCamera()
        }
        binding.toggleLocation.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                DataStoreUtil.savePrefSettings(
                    requireContext(),
                    PreferencesKeys.LOCATION_TOGGLE,
                    isChecked
                )
            }

            if (isChecked) {
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            } else {
                viewModel.setLastLocation(null)
            }
        }
        binding.buttonAdd.setOnClickListener {
            if (viewModel.currentUri == null) {
                val dialog = AlertDialog.Builder(requireContext())
                    .setMessage(R.string.upload_warning)
                    .setNegativeButton(R.string.action_close) { dialog, _ ->
                        dialog.dismiss()
                    }
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

        lifecycleScope.launch(Dispatchers.Main.immediate) {
            val isChecked = requireContext().settingsDataStore.data.firstOrNull()
                ?.get(PreferencesKeys.LOCATION_TOGGLE)
            binding.toggleLocation.isChecked = isChecked ?: false
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
                binding.ivStoryImage.load(uri) {
                    placeholder(R.drawable.ic_image)
                }
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

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            permissions[Manifest.permission.CAMERA]?.let { isGranted ->
                if (isGranted) {
                    openCamera()
                } else {
                    requireContext().showToast(R.string.camera_permission_denied)
                }
            }

            permissions[Manifest.permission.ACCESS_COARSE_LOCATION]?.let { isGranted ->
                if (isGranted) {
                    createLocationRequest()
                } else {
                    requireContext().showToast("Permission has been denied")
                }
            }
        }


    private fun checkCameraPermissionOrOpenCamera() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(
                arrayOf(Manifest.permission.CAMERA)
            )
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
        launcherIntentCamera.launch(tempUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        viewModel.onPostTakePicture(isSuccess)
    }

    private fun uploadImage() {
        viewModel.currentUri?.let { uri ->
            val file = reduceFileImage(uriToFile(uri, requireContext()))
            val description =
                binding.edDescription.text.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )
            lifecycleScope.launch {
                val auth = requireContext().sessionDataStore.data.firstOrNull()
                    ?.get(PreferencesKeys.TOKEN_KEY)
                auth?.let {
                    viewModel.uploadFile(imageMultipart, description, auth)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}