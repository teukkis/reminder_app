package com.example.remindr

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.remindr.database.Reminder

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.*

const val LOCATION_REQUEST_CODE = 123
const val GEOFENCE_LOCATION_REQUEST_CODE = 321


class MapsFragment : Fragment() {

    private lateinit var navController: NavController
    private lateinit var previousLocation: String
    private lateinit var clickedReminder: Reminder


    private val callback = OnMapReadyCallback { googleMap ->

        val homeLatLng = LatLng(61.498938, 23.775095)
        val zoomLevel = 15f

        googleMap.uiSettings.isZoomControlsEnabled = true

        googleMap.addMarker(MarkerOptions().position(homeLatLng))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(homeLatLng, zoomLevel))

        setMapLongClick(googleMap)


        if (!isLocationPermissionGranted()) {
            val permissions = mutableListOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                permissions.add((Manifest.permission.ACCESS_BACKGROUND_LOCATION))
            }
            activity?.let {
                ActivityCompat.requestPermissions(
                        it,
                        permissions.toTypedArray(),
                        LOCATION_REQUEST_CODE
                )
            }
        }

    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)


        navController = Navigation.findNavController(view)
        previousLocation = arguments?.getString("previous_location").toString()


    }


    // Called when user makes a long press gesture on the map.
    private fun setMapLongClick(map: GoogleMap) {
        map.clear()
        map.setOnMapLongClickListener { latLng ->

            val latitude = String.format(
                    Locale.getDefault(),
                    "Lat: %1$.3f",
                    latLng.latitude
            )

            val longitude = String.format(
                    Locale.getDefault(),
                    "Long: %1$.3f",
                    latLng.longitude
            )

            println(latLng.latitude.toFloat())
            if (previousLocation == "edit") {
                clickedReminder = arguments?.getParcelable("reminder")!!

                val bundle = bundleOf(
                        "location_x" to latitude,
                        "location_y" to longitude,
                        "reminder" to clickedReminder
                )
                navController.navigate(R.id.action_mapsFragment_to_editReminderFragment, bundle)

            } else {
                val bundle = bundleOf(
                        "location_x" to latitude,
                        "location_y" to longitude
                )
                navController.navigate(R.id.action_mapsFragment_to_fullReminderFragment, bundle)
            }


        }
    }

    private fun isLocationPermissionGranted() : Boolean {
        val fineLocationPermission = activity?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) }
        val coarseLocationPermission = activity?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_COARSE_LOCATION) }

        if (fineLocationPermission == PackageManager.PERMISSION_GRANTED && coarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            return true
        }

        return false
    }
}