package com.esc.doglet.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collect
import com.esc.doglet.R
import com.esc.doglet.databinding.MapFragmentBinding
import com.esc.doglet.viewmodels.MapViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "myT"

@AndroidEntryPoint
class MapFragment : Fragment(R.layout.map_fragment), OnMapReadyCallback{

    private val viewModel: MapViewModel by viewModels()
    private lateinit var binding : MapFragmentBinding
    private lateinit var map: GoogleMap

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = MapFragmentBinding.bind(view)
        setupMap(savedInstanceState)
        setMapObservers()
    }

    private fun setupMap(savedInstanceState: Bundle?) {
        binding.mainMapView.onCreate(savedInstanceState)
        binding.mainMapView.getMapAsync(this)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.isMyLocationEnabled = true
    }

    private fun setMapObservers() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.eventFlow.collect{ event ->
                when (event) {
                    is MapViewModel.Events.LocationEvent -> {
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(event.latLng,12f))
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.mainMapView.onResume()
    }
    override fun onPause() {
        super.onPause()
        binding.mainMapView.onPause()
    }
    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: ")
        binding.mainMapView.onStart()
    }
    override fun onStop() {
        super.onStop()
        binding.mainMapView.onStop()
    }
    override fun onLowMemory() {
        super.onLowMemory()
        binding.mainMapView.onLowMemory()
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mainMapView.onSaveInstanceState(outState)
    }
}