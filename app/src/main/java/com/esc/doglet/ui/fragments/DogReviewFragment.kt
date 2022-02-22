package com.esc.doglet.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.esc.doglet.R
import com.esc.doglet.databinding.DogReviewFragmentBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DogReviewFragment: Fragment(R.layout.dog_review_fragment), OnMapReadyCallback {

    private lateinit var binding: DogReviewFragmentBinding
    private lateinit var map: GoogleMap

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DogReviewFragmentBinding.bind(view)
        binding.dogMapView.onCreate(savedInstanceState)
        binding.dogMapView.getMapAsync(this)
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.isMyLocationEnabled = true
    }

}