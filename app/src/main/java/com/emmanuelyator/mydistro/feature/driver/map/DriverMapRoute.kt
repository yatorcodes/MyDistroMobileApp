package com.emmanuelyator.mydistro.feature.driver.map

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emmanuelyator.mydistro.core.model.StopStatus
import com.emmanuelyator.mydistro.core.model.Trip
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import com.emmanuelyator.mydistro.R
import com.mapbox.maps.extension.compose.annotation.generated.PolylineAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation

@Composable
fun DriverMapRoute(
    viewModel: DriverMapViewModel = hiltViewModel()
) {
    val activeTrip by viewModel.activeTrip.collectAsStateWithLifecycle()
    val routePoints by viewModel.routePoints.collectAsStateWithLifecycle()
    
    val mapboxToken = stringResource(id = R.string.mapbox_access_token)
    
    DriverMapScreen(
        activeTrip = activeTrip,
        routePoints = routePoints,
        onFetchRoute = { waypoints ->
            viewModel.fetchRoute(waypoints, mapboxToken)
        }
    )
}

@Composable
fun DriverMapScreen(
    activeTrip: Trip?,
    routePoints: List<Point>,
    onFetchRoute: (List<Point>) -> Unit
) {
    // Determine the next stop
    val nextStop = activeTrip?.stops?.firstOrNull { it.status == StopStatus.IN_PROGRESS || it.status == StopStatus.PENDING }
    val mapCenter = nextStop?.location?.let { Point.fromLngLat(it.longitude, it.latitude) } 
        ?: activeTrip?.origin?.let { Point.fromLngLat(it.longitude, it.latitude) }
        ?: Point.fromLngLat(36.8219, -1.2921) // Nairobi default

    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            center(mapCenter)
            zoom(11.0)
            pitch(0.0)
        }
    }

    val context = LocalContext.current
    
    LaunchedEffect(activeTrip) {
        if (activeTrip != null) {
            val waypoints = mutableListOf<Point>()
            waypoints.add(Point.fromLngLat(activeTrip.origin.longitude, activeTrip.origin.latitude))
            activeTrip.stops.forEach { stop ->
                waypoints.add(Point.fromLngLat(stop.location.longitude, stop.location.latitude))
            }
            onFetchRoute(waypoints)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        MapboxMap(
            modifier = Modifier.fillMaxSize(),
            mapViewportState = mapViewportState
        ) {
            if (routePoints.isNotEmpty()) {
                PolylineAnnotation(
                    points = routePoints,
                    lineColorInt = Color.Blue.toArgb(),
                    lineWidth = 6.0
                )
            }
        }

        // Overlay Trip Info
        if (activeTrip != null) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Active Trip: ${activeTrip.tripNumber}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    if (nextStop != null) {
                        Text(
                            text = "Next Stop: ${nextStop.location.name}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = nextStop.location.address,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = {
                                // Launch external maps for turn-by-turn navigation
                                val uri = "google.navigation:q=${nextStop.location.latitude},${nextStop.location.longitude}"
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                                intent.setPackage("com.google.android.apps.maps")
                                if (intent.resolveActivity(context.packageManager) != null) {
                                    context.startActivity(intent)
                                } else {
                                    // Fallback if Google Maps is not installed
                                    val fallbackUri = "geo:${nextStop.location.latitude},${nextStop.location.longitude}?q=${nextStop.location.latitude},${nextStop.location.longitude}"
                                    val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse(fallbackUri))
                                    context.startActivity(fallbackIntent)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Filled.Navigation, contentDescription = null)
                            Spacer(Modifier.padding(4.dp))
                            Text("Navigate to Stop")
                        }
                    } else {
                        Text("All stops completed!")
                    }
                }
            }
        } else {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 8.dp
            ) {
                Text(
                    text = "No active trips right now.",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
