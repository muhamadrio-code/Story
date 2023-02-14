package com.riopermana.story.ui.stories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.riopermana.story.databinding.FragmentStoryDetailsBinding

class StoryDetailsFragment : Fragment() {

    private var _binding: FragmentStoryDetailsBinding? = null
    private val binding: FragmentStoryDetailsBinding get() = _binding!!
    private val viewModel: StoryDetailsVideModel by viewModels()
    private val storyArgs: StoryDetailsFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setStory(storyArgs.story)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoryDetailsBinding.inflate(inflater, container, false)
        subscribeObserver()
        setupListener()

        return binding.root
    }

    private fun setupListener() {
        binding.ibActionBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun subscribeObserver() {
        viewModel.observableStory.observe(viewLifecycleOwner) { story ->
            binding.ivDetailPhoto.load(story.photoUrl)
            binding.tvDetailName.text = story.name
            binding.tvDetailDescription.text = story.description
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}