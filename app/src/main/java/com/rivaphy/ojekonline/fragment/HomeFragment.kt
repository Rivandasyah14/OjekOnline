package com.rivaphy.ojekonline.fragment


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

import com.rivaphy.ojekonline.R
import com.rivaphy.ojekonline.maps.GPSTrack
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.fragment_home.*
import java.lang.Exception
import java.lang.StringBuilder
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    var latAwal: Double? = null
    var lonAwal: Double? = null
    var map: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    //menginisialisasi dari mapsview

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync { this }
    }

    //menampilkan lokasi user berdasarkan gps device user
    private fun showGps() {
        val gps = context?.let { GPSTrack(it) }
        if (gps?.canGetLocation()!!) {
            latAwal = gps.latitude
            lonAwal = gps.longitude

            showMainMarker(latAwal ?: 0.0, lonAwal ?: 0.0, "My Locations")

            val name = showName(latAwal ?: 0.0, lonAwal ?: 0.0)

            homeAwal.text = name

        } else gps.showSettingGPS()
    }

    /*EOCODER = menerjemahkan dari kordinat menjadi lokasi
     buat nerjemahin latitude sama longitude jadi lokasi*/
    private fun showName(lat: Double, lon: Double): String {
        var name = ""
        val geocoder = Geocoder(context, Locale.getDefault())

        try {
            val addresses = geocoder
                .getFromLocation(lat, lon, 1)

            if (addresses.size > 0) {
                val fetchedAdress = addresses.get(0)
                val strAddress = StringBuilder()

                for (i in 0..fetchedAdress.maxAddressLineIndex) {
                    name = strAddress.append(
                        fetchedAdress
                            .getAddressLine(i)
                    ).append("").toString()
                }
            }
        } catch (e: Exception) {

        }
        return name
    }

    //menampilkan lokasi menggunakan marker
    //marker yang origin
    private fun showMainMarker(lat: Double, lon: Double, msg: String) {
        val res = context?.resources
        val marker1 = BitmapFactory
            .decodeResource(res, R.drawable.gmaps_red)
        val smallmarker = Bitmap
            .createScaledBitmap(marker1, 80, 120, false)

        val coordinate = LatLng(lat, lon)

        //membuat pin baru di android
        map?.addMarker(
            MarkerOptions().position(coordinate)
                .title(msg).icon(
                    BitmapDescriptorFactory
                        .fromBitmap(smallmarker)
                )
        )

        //mengatur zoom camera
        map?.animateCamera(
            CameraUpdateFactory
                .newLatLngZoom(coordinate, 16f)
        )

        //setting biar posisi marker selalu di tengah
        map?.moveCamera(CameraUpdateFactory.newLatLng(coordinate))
    }

    //marker destination
    fun showMarker(lat: Double, lon: Double, msg: String) {
        val coordinat = LatLng(lat, lon)

        map?.addMarker(MarkerOptions().position(coordinat).title(msg))
        map?.animateCamera(
            CameraUpdateFactory
                .newLatLngZoom(coordinat, 16f)
        )
        map?.moveCamera(CameraUpdateFactory.newLatLng(coordinat))
    }

}
