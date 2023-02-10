package com.riopermana.story.ui.stories

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import coil.load
import com.riopermana.story.databinding.FragmentNewStoryBinding
import com.riopermana.story.ui.utils.uriToFile

class NewStoryFragment : Fragment() {

    private lateinit var binding: FragmentNewStoryBinding

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

        }
        return binding.root
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, requireActivity())
            binding.ivStoryImage.load(selectedImg)
        }
    }
}