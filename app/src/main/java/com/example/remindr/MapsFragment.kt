package com.example.remindr

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

class MapsFragment : Fragment() {

    private lateinit var navController: NavController
    private lateinit var previousLocation: String
    private lateinit var clickedReminder: Reminder


    private val callback = OnMapReadyCallback { googleMap ->

        val homeLatLng = LatLng(61.498938, 23.775095)
        val zoomLevel = 15f

        googleMap.addMarker(MarkerOptions().position(homeLatLng))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(homeLatLng, zoomLevel))

        setMapLongClick(googleMap)

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

                var bundle = bundleOf(
                        "location_x" to latitude,
                        "location_y" to longitude,
                        "reminder" to clickedReminder
                )
                navController!!.navigate(R.id.action_mapsFragment_to_editReminderFragment, bundle)

            } else {
                var bundle = bundleOf(
                        "location_x" to latitude,
                        "location_y" to longitude
                )
                navController!!.navigate(R.id.action_mapsFragment_to_fullReminderFragment, bundle)
            }


        }
    }
}