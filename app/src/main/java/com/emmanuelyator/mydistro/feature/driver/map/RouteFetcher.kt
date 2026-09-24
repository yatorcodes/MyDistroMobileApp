package com.emmanuelyator.mydistro.feature.driver.map

import com.mapbox.geojson.Point
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

object RouteFetcher {
    suspend fun fetchRoute(
        waypoints: List<Point>,
        accessToken: String
    ): List<Point>? = withContext(Dispatchers.IO) {
        if (waypoints.size < 2) return@withContext null
        
        try {
            val coordinatesStr = waypoints.joinToString(";") { "${it.longitude()},${it.latitude()}" }
            val url = "https://api.mapbox.com/directions/v5/mapbox/driving/" +
                "$coordinatesStr" +
                "?geometries=geojson&access_token=$accessToken"

            val response = URL(url).readText()
            val json = JSONObject(response)
            val routes = json.getJSONArray("routes")
            if (routes.length() > 0) {
                val route = routes.getJSONObject(0)
                val geometry = route.getJSONObject("geometry")
                val coordinates = geometry.getJSONArray("coordinates")
                
                val points = mutableListOf<Point>()
                for (i in 0 until coordinates.length()) {
                    val coord = coordinates.getJSONArray(i)
                    points.add(Point.fromLngLat(coord.getDouble(0), coord.getDouble(1)))
                }
                return@withContext points
            }
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
