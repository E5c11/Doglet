package com.esc.doglet.ui.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.esc.doglet.R
import com.esc.doglet.databinding.HomeFragmentBinding
import com.esc.doglet.utils.PermissionsCheck
import com.esc.doglet.viewmodels.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.home_fragment) {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var _binding : HomeFragmentBinding
    private val requestCode = 1;

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = HomeFragmentBinding.bind(view)
        requestLocation()
        setButtonOnClick()
        //setHasOptionsMenu(true)
    }

    private fun setButtonOnClick() {
        _binding.findDogBt.setOnClickListener {
            if (PermissionsCheck.hasLocationPermission(requireContext())) {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToMapFragment())
            } else askPermission()
        }
        _binding.loginBtn.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLogin())
        }
        _binding.reviewBtn.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToDogReviewFragment())
        }
    }

    private fun requestLocation() {
        if (PermissionsCheck.hasLocationPermission(requireContext())) return
        else askPermission()
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            this.requestCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED
                    && !PermissionsCheck.hasLocationPermission(requireContext())) {
                    askPermission()
                }
                return
            }
        }
    }
    private fun askPermission() {
        ActivityCompat.requestPermissions(requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), requestCode)
    }
}