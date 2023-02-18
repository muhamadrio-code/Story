package com.riopermana.story.ui.maps

import android.app.Application
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.riopermana.story.R
import com.riopermana.story.data.local.DataStoreUtil
import com.riopermana.story.databinding.FragmentMapsBinding
import com.riopermana.story.di.storyRepository
import com.riopermana.story.model.Story
import com.riopermana.story.ui.ViewModelFactory
import com.riopermana.story.utils.showToast

class MapsFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapsBinding? = null
    private val binding: FragmentMapsBinding get() = _binding!!

    private val viewModel: MapsViewModel by viewModels(
        factoryProducer = {
            ViewModelFactory((requireActivity().applicationContext as Application).storyRepository)
        }
    )

    private lateinit var mMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
        requireActivity().window.statusBarColor = Color.TRANSPARENT

        val mapFragment = SupportMapFragment.newInstance()
        childFragmentManager.beginTransaction().add(R.id.map, mapFragment).commit()
        mapFragment.getMapAsync(this)

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, true)
        _binding = null
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        setMapStyle()
        subscribeObserver()
    }

    private fun setMapStyle() {
        val styleJSON =
            when (requireContext().resources.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
                Configuration.UI_MODE_NIGHT_YES -> R.raw.maps_dark_style
                Configuration.UI_MODE_NIGHT_NO -> R.raw.maps_retro_style
                else -> R.raw.maps_retro_style
            }

        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), styleJSON))
    }

    private fun setupMarker(story: Story) {
        with(story) {
            if (lat == null || lon == null) return@with
            val position = LatLng(lat, lon)
            val markerOptions = MarkerOptions().position(position).title(name).snippet(description)
            val marker = mMap.addMarker(markerOptions)

            marker?.let {
                val markerIcon =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_pin_location)
                markerIcon?.let { icon ->
                    it.setIcon(BitmapDescriptorFactory.fromBitmap(icon.toBitmap()))
                }
            }
            marker?.showInfoWindow()
        }
    }

    private fun subscribeObserver() {
        viewModel.stories.observe(viewLifecycleOwner) { stories ->
            Log.d("TAG", "stories::${stories.size}")

            if (stories.isEmpty()) {
                requireContext().showToast(R.string.no_data)
                return@observe
            }

            val indoLatLng = LatLng(0.7893, 113.9213)
            stories.forEach {
                setupMarker(it)
            }
            val firstItemLatLng = LatLng(
                stories[0].lat ?: indoLatLng.latitude,
                stories[0].lon ?: indoLatLng.longitude
            )
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstItemLatLng, 5f))
        }

        lifecycleScope.launchWhenCreated {
            DataStoreUtil.getCurrentSession(requireContext()) { authKey ->
                authKey?.let {
                    viewModel.getStories(authKey)
                }
            }
        }
    }
}