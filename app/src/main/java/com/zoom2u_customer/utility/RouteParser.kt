package com.zoom2u_customer.utility

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import org.json.JSONObject
import java.util.*
import java.util.concurrent.Executors

class RouteParser {
    companion object {
        fun parserTask(context: Context, map: GoogleMap?, data: String) {
            val executor = Executors.newSingleThreadExecutor()
            val handler = Handler(Looper.getMainLooper())
            executor.execute {

                val jObject: JSONObject
                var routes: List<List<HashMap<String, String>>>? = null
                try {
                    jObject = JSONObject(data)
                    val parser = DirectionJsonParser()
                    routes = parser.parse(jObject)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }




                handler.post {
                    var points: ArrayList<LatLng>
                    var lineOptions: PolylineOptions? = null
                    var distance = ""
                    var duration = ""
                    if (routes?.isEmpty() == true) {
                        Toast.makeText(context, "No Points", Toast.LENGTH_SHORT).show()
                    }
                    for (i in routes?.indices!!) {
                        points = ArrayList()
                        if (lineOptions != null) lineOptions = null
                        lineOptions = PolylineOptions()

                        // Fetching i-th route
                        val path: List<HashMap<String, String>> = routes[i]
                        // Fetching all the points in i-th route
                        for (j in path.indices) {
                            val point = path[j]
                            if (j == 0) { // Get distance from the list
                                distance = point["distance"].toString()
                                continue
                            } else if (j == 1) { // Get duration from the list
                                duration = point["duration"].toString()
                                continue
                            }
                            val lat = point["lat"]!!.toDouble()
                            val lng = point["lng"]!!.toDouble()
                            val position = LatLng(lat, lng)
                            points.add(position)
                        }

                        // Adding all the points in the route to LineOptions
                        lineOptions.addAll(points)
                        lineOptions.width(8F)
                        lineOptions.color(Color.parseColor("#374350"))
                    }

                    // Drawing polyline in the Google Map for the i-th route
                    if (lineOptions != null) {
                        map?.addPolyline(lineOptions)
                    }
                }
            }
        }
    }
}