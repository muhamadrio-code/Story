package com.riopermana.story.ui.maps

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.riopermana.story.R
import com.riopermana.story.databinding.FragmentMapsBinding

class MapsFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapsBinding? = null
    private val binding: FragmentMapsBinding get() = _binding!!

    private val args: MapsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
        requireActivity().window.statusBarColor = Color.TRANSPARENT

        val mapFragment = SupportMapFragment.newInstance()
        parentFragmentManager.beginTransaction().add(R.id.map, mapFragment).commit()
        mapFragment.getMapAsync(this)

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, true)
        _binding = null
    }

    override fun onMapReady(googleMap: GoogleMap) {
        args.story.apply {
            if (lat == null || lon == null) return@apply
            val position = LatLng(lat, lon)
            val markerOptions = MarkerOptions().position(position).title(name).snippet(description)
            val marker = googleMap.addMarker(markerOptions)
            marker?.let {
                val markerIcon = ContextCompat.getDrawable(requireContext(),R.drawable.ic_pin_location)
                markerIcon?.let { icon ->
                    it.setIcon(BitmapDescriptorFactory.fromBitmap(icon.toBitmap()))
                }
                it.showInfoWindow()
            }

            val styleJSON = when (requireContext().resources.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
                Configuration.UI_MODE_NIGHT_YES -> R.raw.maps_dark_style
                Configuration.UI_MODE_NIGHT_NO -> R.raw.maps_retro_style
                else -> R.raw.maps_retro_style
            }

            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(),styleJSON))
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position,15f))
        }
    }
}