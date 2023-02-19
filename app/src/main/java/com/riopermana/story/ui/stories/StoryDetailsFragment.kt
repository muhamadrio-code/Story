package com.riopermana.story.ui.stories

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import androidx.core.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.google.android.gms.maps.*
import com.riopermana.story.R
import com.riopermana.story.databinding.FragmentStoryDetailsBinding
import java.util.*

class StoryDetailsFragment : Fragment() {

    private var _binding: FragmentStoryDetailsBinding? = null
    private val binding: FragmentStoryDetailsBinding get() = _binding!!

    private val viewModel: StoryDetailsVideModel by viewModels()
    private val args: StoryDetailsFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setStory(args.story)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoryDetailsBinding.inflate(inflater, container, false)
        setupListener()
        observeStory()
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupListener() {
        binding.ibActionBack.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    private fun observeStory() {
        viewModel.observableStory.observe(viewLifecycleOwner) { story ->
            binding.apply {
                with(story) {
                    setDetailPhoto(photoUrl)
                    tvDetailName.text = name
                    tvDetailDescription.isVisible = description.isNotEmpty().also {
                        if (it) tvDetailDescription.text = description
                    }

                }
            }
        }
    }

    private fun setDetailPhoto(photoUrl: String) {
        binding.ivDetailPhoto.apply {
            load(photoUrl) {
                error(R.drawable.ic_broken_image)
                target(
                    onStart = {
                        showProgressBar(true)
                    },
                    onError = { errorDrawable ->
                        setImageDrawable(errorDrawable)
                        showProgressBar(false)
                    },
                    onSuccess = { result: Drawable ->
                        setImageDrawable(result)
                        showProgressBar(false)
                    }
                )
            }
        }
    }

    private fun showProgressBar(visible: Boolean) {
        binding.linearProgress.isVisible = visible
    }

    override fun onDestroy() {
        super.onDestroy()
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, true)
        _binding = null
    }
}