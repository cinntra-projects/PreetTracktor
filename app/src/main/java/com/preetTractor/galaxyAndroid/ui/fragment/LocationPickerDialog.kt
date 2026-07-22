package com.preetTractor.galaxyAndroid.ui.fragment

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.preetTractor.galaxyAndroid.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL


class LocationPickerDialog : DialogFragment() {
    companion object {
        private const val TAG = "LocationPickerDialog"
        private var latLongList = ArrayList<LatLng>()


        fun newInstance(allILatLong: ArrayList<LatLng>): LocationPickerDialog {
            val dialog = LocationPickerDialog()
            latLongList = allILatLong
            return dialog
        }
    }

    lateinit var customView: View
    private var mapFragment: SupportMapFragment? = null
    private var googleMap: GoogleMap? = null
    private var imageButton: ImageButton? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        customView = inflater.inflate(R.layout.fragment_map_dialog, container, false)
        return customView
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity(), R.style.CustomAlertDialog)
            .create()
        builder.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val view = layoutInflater.inflate(R.layout.fragment_map_dialog, null)

        builder.setView(view)
        builder.setCanceledOnTouchOutside(false)
        builder.show()
        return builder
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageButton = view.findViewById(R.id.ivClose)
        imageButton?.setOnClickListener {
            Log.e(TAG, "clicked")
            dialog?.dismiss()
        }
    }


    override fun onStart() {
        super.onStart()
        imageButton?.setOnClickListener {
            dialog?.dismiss()
        }
    }

    /*override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mapFragment = childFragmentManager.findFragmentByTag("map") as SupportMapFragment?
        if (mapFragment == null) {
            Log.d(TAG, "SupportMapFragment.newInstance")
            mapFragment = SupportMapFragment.newInstance()
            childFragmentManager.beginTransaction().replace(R.id.map, mapFragment!!, "map").commit()
        }

        mapFragment?.let { mapFragment ->
            mapFragment.getMapAsync { map ->
                googleMap = map
                map.uiSettings.isZoomControlsEnabled = true
                map.addPolyline(
                    PolylineOptions().addAll(latLongList)
                        .width // below line is use to specify the width of poly line.
                            (5f) // below line is use to add color to our poly line.
                        .color(Color.RED) // below line is to make our poly line geodesic.
                        .geodesic(true)
                )
                latLongList[0]?.let { CameraUpdateFactory.newLatLngZoom(it, 13F) }
                    ?.let { map.moveCamera(it) }

            }
        }
    }*/


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mapFragment = childFragmentManager.findFragmentByTag("map") as SupportMapFragment?
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance()
            childFragmentManager.beginTransaction().replace(R.id.map, mapFragment!!, "map").commit()
        }

        mapFragment?.let { mapFragment ->
            mapFragment.getMapAsync { map ->
                googleMap = map
                map.uiSettings.isZoomControlsEnabled = true

                for (latLng in latLongList) {
                    googleMap?.addMarker(
                        MarkerOptions().position(latLng).title("Marker at ${latLng.latitude}, ${latLng.longitude}")
                    )
                }
                lifecycleScope.launch {
                    for (i in 0 until latLongList.size - 1) {
                        val origin = latLongList[i]
                        val destination = latLongList[i + 1]

                        val directionsUrl = getDirectionsUrl(origin, destination)

                        Log.e(TAG, "onActivityCreated: ${getDirectionsUrl(origin, destination)}")
                        val routeData = fetchRoute(directionsUrl)
                        val polylinePoints = parseDirections(routeData) // Implement this function to parse the response
                        addRoutePolyline(map, polylinePoints)
                    }

                    val boundsBuilder = LatLngBounds.Builder()
                    for (latLng in latLongList) {
                        boundsBuilder.include(latLng)
                    }
                    val bounds = boundsBuilder.build()
                    map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
                }
            }
        }
    }






    private fun getDirectionsUrl(origin: LatLng, destination: LatLng): String {
        // Building the URL to the web service
        val strOrigin = "origin=${origin.latitude},${origin.longitude}"
        val strDest = "destination=${destination.latitude},${destination.longitude}"
        val sensor = "sensor=false"
        val mode = "mode=driving"
        val key = resources.getString(R.string.google_maps_direction_key) // replace with your API key
        val parameters = "$strOrigin&$strDest&$sensor&$mode&key=$key"
        return "https://maps.googleapis.com/maps/api/directions/json?$parameters"
    }

    private suspend fun fetchRoute(url: String): String {
        return withContext(Dispatchers.IO) {
            val urlConnection = URL(url).openConnection() as HttpURLConnection
            try {
                val inputStream = urlConnection.inputStream
                inputStream.bufferedReader().use { it.readText() }
            } finally {
                urlConnection.disconnect()
            }
        }
    }


    private fun getArrowBitmapDescriptor(context: Context): BitmapDescriptor {
        val drawable = ContextCompat.getDrawable(context, R.drawable.arrow)!!
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }


 /*   private fun addRoutePolyline(googleMap: GoogleMap, polylinePoints: List<LatLng>) {
        // Create polyline with arrow pattern

      //  val arrow = PatternItem.CREATOR.newArray(30)
        val polylineOptions = PolylineOptions()
            .addAll(polylinePoints)
            .width(8f)

            .color(ContextCompat.getColor(requireContext(),R.color.colorPrimary))
           // .pattern(listOf(Dot()))  // Adding arrows to the polyline
          *//*  .pattern(listOf(Dash(1f))) *//* // Adding arrows to the polyline
            .pattern(PatternItem.CREATOR.newArray(30))
            .geodesic(true)

        googleMap.addPolyline(polylineOptions)
    }*/

    private fun addRoutePolyline(googleMap: GoogleMap, polylinePoints: List<LatLng>) {
        // Define the pattern for the polyline (e.g., dots or dashes)
        val pattern = listOf<PatternItem>(

            Dash(30f),       // Dash pattern
            Gap(10f)         // Gap after each dash
        )


        // Create the PolylineOptions object with the specified attributes
        val polylineOptions = PolylineOptions()
            .addAll(polylinePoints)
            .width(10f)
            .color(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            .pattern(pattern) // Apply the pattern to the polyline
            .geodesic(true)

        // Add the polyline to the map
        googleMap.addPolyline(polylineOptions)
    }


   /* private fun parseDirections(jsonData: String): List<LatLng> {
        val jsonObject = JSONObject(jsonData)
        val routes = jsonObject.getJSONArray("routes")

        val overviewPolyline = routes.getJSONObject(0).getJSONObject("overview_polyline")
        val encodedString = overviewPolyline.getString("points")
        return decodePolyline(encodedString)
    }*/
    private fun parseDirections(jsonData: String): List<LatLng> {
        return try {
            val jsonObject = JSONObject(jsonData)
            val routes = jsonObject.getJSONArray("routes")

            if (routes.length() == 0) {
                return emptyList()  // No routes found, return an empty list
            }

            val overviewPolyline = routes.getJSONObject(0).getJSONObject("overview_polyline")
            val encodedString = overviewPolyline.getString("points")
            decodePolyline(encodedString)  // Call decodePolyline function
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
            emptyList()  // Return empty list if IndexOutOfBoundsException occurs
        } catch (e: JSONException) {
            e.printStackTrace()
            emptyList()  // Return empty list if JSON is malformed or missing fields
        }
    }


    private fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val latLng = LatLng(lat / 1E5, lng / 1E5)
            poly.add(latLng)
        }

        return poly
    }




}