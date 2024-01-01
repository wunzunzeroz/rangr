package com.rangr.map

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.mapbox.maps.MapView
import com.rangr.R
import com.rangr.map.components.RoutingScreen
import com.rangr.map.components.ViewingScreen
import com.rangr.nav.MapState

class MapActivity : ComponentActivity() {
    private val model: MapViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mapView = MapView(this)

        model.initialise(mapView)
        model.setTapIcon(loadTapMarker())

        setContent { MainScreen() }
    }

    override fun onDestroy() {
        super.onDestroy()
        model.onDestroy()
    }

    @Composable
    fun MainScreen() {
        val state by model.mapState.observeAsState()

        when (state) {
            MapState.Viewing -> ViewingScreen(model)
            MapState.Routing -> RoutingScreen(model)
            null -> ViewingScreen(model)
        }
    }

    private fun loadTapMarker(): Bitmap {
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.tap_marker)
        return Bitmap.createScaledBitmap(bitmap, 50, 50, false)
    }

}