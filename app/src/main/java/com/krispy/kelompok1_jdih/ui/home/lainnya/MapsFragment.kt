package com.krispy.kelompok1_jdih.ui.home.lainnya

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import com.krispy.kelompok1_jdih.R
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

class MapsFragment : Fragment() {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val dprdKotaKediri = LatLng(-7.816579, 112.011375)
    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap
        mMap.addMarker(MarkerOptions().position(dprdKotaKediri).title("Alamat DPRD Kota Kediri"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dprdKotaKediri, 12f))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        view.findViewById<ImageButton>(R.id.btn_mylocation).setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                getMyLocation()
            } else {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
            }
        }
    }

    private fun getMyLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val myLocation = LatLng(location.latitude, location.longitude)
                mMap.clear()
                mMap.addMarker(MarkerOptions().position(myLocation).title("My Location"))
                mMap.addMarker(MarkerOptions().position(dprdKotaKediri).title("Alamat DPRD Kota Kediri"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 12f))
                showRoute(myLocation, dprdKotaKediri)

                // Calculate distance
                val dprdLocation = Location("").apply {
                    latitude = dprdKotaKediri.latitude
                    longitude = dprdKotaKediri.longitude
                }
                val distance = location.distanceTo(dprdLocation) / 1000  // distance in km
                Toast.makeText(requireContext(), "Distance to DPRD Kota Kediri: %.2f km".format(distance), Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(requireContext(), "Tidak dapat dijangkau", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showRoute(origin: LatLng, destination: LatLng) {
        val originStr = "${origin.latitude},${origin.longitude}"
        val destinationStr = "${destination.latitude},${destination.longitude}"
        val url = "https://maps.googleapis.com/maps/api/directions/json?origin=$originStr&destination=$destinationStr&key=AIzaSyBX0FjabyBlF7YT8OAA02lN1qqDDv5C42E"
        Log.d("MapsFragment", "Origin: $originStr")
        Log.d("MapsFragment", "Destination: $destinationStr")
        Log.d("MapsFragment", "URL: $url")

        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                val body = response.body?.string()
                if (body != null) {
                    val json = JSONObject(body)
                    val routes = json.getJSONArray("routes")
                    if (routes.length() > 0) {
                        val points = routes.getJSONObject(0)
                            .getJSONObject("overview_polyline")
                            .getString("points")
                        val decodedPoints = PolyUtil.decode(points)

                        requireActivity().runOnUiThread {
                            mMap.addPolyline(PolylineOptions().addAll(decodedPoints).color(ContextCompat.getColor(requireContext(), R.color.purple)))
                        }
                    }
                }
            }
        })
    }
}
