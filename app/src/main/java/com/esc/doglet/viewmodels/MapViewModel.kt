package com.esc.doglet.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val locationRequest: LocationRequest,
    private val application: Application
): ViewModel(){

    private val eventChannel = Channel<Events>()
    val eventFlow = eventChannel.receiveAsFlow()

    init {
        sendLocationRequest()
        fetchDogs()
    }

    @SuppressLint("MissingPermission")
    private fun sendLocationRequest() {
        val locationCallback: LocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) { locationResult ?: return
                if (locationResult.locations.isNotEmpty()) {
                    val latestLocationIndex = locationResult.locations.size - 1
                    viewModelScope.launch {
                        eventChannel.send(Events.LocationEvent(LatLng(
                            locationResult.locations[latestLocationIndex].latitude,
                            locationResult.locations[latestLocationIndex].longitude)))
                    }
                    LocationServices.getFusedLocationProviderClient(application)
                        .removeLocationUpdates(this)
                }
            }
        }
        LocationServices.getFusedLocationProviderClient(application)
            .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    private fun fetchDogs() {

    }

    sealed class Events {
        data class LocationEvent(val latLng: LatLng): Events()
    }
}