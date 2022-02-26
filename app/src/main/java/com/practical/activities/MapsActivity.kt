package com.practical.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.practical.R
import com.practical.databinding.ActivityMapsBinding
import com.practical.utils.Utils
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var currentLocation: Location? = null
    private val permissionCode = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationProviderClient =  LocationServices.getFusedLocationProviderClient(this@MapsActivity)

        fetchLocation()
    }

    private fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), permissionCode)
            return
        }
        val task = fusedLocationProviderClient.lastLocation
        task.addOnSuccessListener {
            if (it != null) {
                currentLocation = it
                val mapFragment = supportFragmentManager
                    .findFragmentById(R.id.map) as SupportMapFragment
                mapFragment.getMapAsync(this)
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isZoomGesturesEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mMap.isMyLocationEnabled = true

        val currentLocation = currentLocation?.latitude?.let { lat -> currentLocation?.longitude?.let { long ->
            LatLng(lat,
                long
            )
        } }
        currentLocation?.let { mMap.addMarker(MarkerOptions().position(it).title("Current Location")) }
        currentLocation?.let { mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 18f)) }

        setMapClickListener()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionCode && grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fetchLocation()
        }
    }

    private fun setMapClickListener() {
        mMap.setOnMapClickListener { latLng ->
            val snippet = String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Long: %2$.5f",
                latLng.latitude,
                latLng.longitude
            )
            mMap.clear()
            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                        .title(getString(R.string.str_selected_location))
                    .snippet(snippet)

            )
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))

            val geocoder: Geocoder = Geocoder(this, Locale.getDefault())

            val addresses: List<Address> = geocoder.getFromLocation(
                latLng.latitude,
                latLng.longitude,
                1
            )

            val address: String? = addresses[0].getAddressLine(0)
            Handler(Looper.getMainLooper()).postDelayed({ openBottomSheet(address,latLng) },1200)
        }
    }

    private fun openBottomSheet(address: String?, latLng: LatLng) {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.layout_bottom_sheet, null)
        val btnClose = view.findViewById<Button>(R.id.btnNext)
        val txtLocation = view.findViewById<TextView>(R.id.txtLocation)
        val txtLatLng = view.findViewById<TextView>(R.id.txtLatLng)

        txtLocation.text = address
        txtLatLng.text = String.format(
            Locale.getDefault(),
            "Lat: %1$.5f, Long: %2$.5f",
            latLng.latitude,
            latLng.longitude
        )
        btnClose.setOnClickListener {
            dialog.dismiss()
            openDetailsActivity(address)

        }
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
    }

    private fun openDetailsActivity(address: String?) {
        val intent = Intent(this@MapsActivity, DetailsActivity::class.java)
        intent.putExtra(Utils.ADDRESS,address)
        startActivity(intent)
        finish()
    }
}