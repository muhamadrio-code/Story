package com.riopermana.story.ui.stories

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.*
import androidx.core.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.google.android.gms.maps.*
import com.riopermana.story.R
import com.riopermana.story.databinding.FragmentStoryDetailsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
        requireActivity().window.statusBarColor = Color.TRANSPARENT
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(requireView()
        ) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(0,insets.top,0,0)
            WindowInsetsCompat.CONSUMED

        }
        setupListener()
        observeStory()
    }

    private suspend fun getAddressName(lat: Double, lon: Double): Address? {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        return withContext(Dispatchers.Default) {
            runCatching {
                val list = geocoder.getFromLocation(lat, lon, 1)
                if (list != null && list.size != 0) {
                    list[0]
                } else return@runCatching null
            }.getOrNull()
        }
    }

    private fun showUi(visible: Boolean) {
        with(binding) {
            addressContainer.isVisible = visible && (!tvFeaturedName.text.isNullOrEmpty() || !tvLocalityName.text.isNullOrEmpty())
            header.isVisible = visible
            tvDetailDescription.isVisible = visible
        }
    }

    private val isUiVisible: Boolean
        get() = with(binding) {
            header.isVisible && tvDetailDescription.isVisible
        }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupListener() {
        binding.ivDetailPhoto.setOnClickListener {
            showUi(!isUiVisible)
        }

        binding.addressContainer.setOnClickListener {
            navigateToMaps()
        }
    }


    private fun observeStory() {
        viewModel.observableStory.observe(viewLifecycleOwner) { story ->
            binding.apply {
                with(story) {
                    setDetailPhoto(photoUrl)
                    tvDetailName.text = name
                    tvDetailDescription.text = description
                    if (lat != null && lon != null) {
                        lifecycleScope.launch {
                            val address = getAddressName(lat, lon)
                            addressContainer.isVisible = address != null
                            address?.let {
                                tvFeaturedName.isVisible = address.featureName != null
                                address.featureName?.let {
                                    tvFeaturedName.text = it
                                }
                                tvLocalityName.isVisible = address.locality != null
                                address.locality?.let {
                                    tvLocalityName.text = it
                                }
                            }
                        }
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

    private fun navigateToMaps() {
        val action =
            StoryDetailsFragmentDirections.actionStoryDetailsFragmentToMapsFragment(args.story)
        findNavController().navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, true)
        _binding = null
    }
}