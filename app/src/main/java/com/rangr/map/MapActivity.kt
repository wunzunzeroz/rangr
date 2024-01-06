package com.rangr.map

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.graphics.Color
import com.mapbox.maps.MapView
import com.rangr.R
import com.rangr.map.components.*
import com.rangr.map.models.MapState
import com.rangr.ui.theme.RangrDark
import com.rangr.ui.theme.RangrOrange
import java.lang.ref.WeakReference

class MapActivity : ComponentActivity() {
    private val model: MapViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mapView = MapView(this)
        val mapController = MapboxService(mapView)

        val locationPermissionHelper = LocationPermissionHelper(WeakReference(this))
        locationPermissionHelper.checkPermissions {
            model.initialise(mapController)
        }

        model.setTapIcon(blueMarker())
        model.setRouteIcon(orangeMarker())

        setContent { MainScreen(model) }
    }

    override fun onDestroy() {
        super.onDestroy()
        model.onDestroy()
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun MainScreen(model: MapViewModel) {
        val mapState by model.mapState.observeAsState()
        val isBottomSheetVisible by model.isBottomSheetVisible.observeAsState(false)

        val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

        LaunchedEffect(isBottomSheetVisible) {
            if (isBottomSheetVisible) {
                sheetState.show()
            } else {
                sheetState.hide()
            }
        }
        LaunchedEffect(sheetState) {
            snapshotFlow { sheetState.isVisible }.collect { isVisible ->
                if (!isVisible) {
                    model.onBottomSheetDismissed()
                }
            }
        }

        ModalBottomSheetLayout(
            sheetState = sheetState,
            sheetContent = { BottomSheetContent(model) },
            sheetBackgroundColor = RangrDark,
            sheetContentColor = RangrOrange,
        ) {
            Box {
                when (mapState) {
                    MapState.Viewing, null -> ViewingScreen(model)
                    MapState.Routing -> RoutingScreen(model)
                    MapState.Test -> TestScreen(model)
                }
            }
        }

    }

    private fun orangeMarker(): Bitmap {
        return resizeBitmap(BitmapFactory.decodeResource(resources, R.drawable.marker_orange))
    }

    private fun blueMarker(): Bitmap {
        return resizeBitmap(BitmapFactory.decodeResource(resources, R.drawable.marker_blue))
    }

    private fun greenMarker(): Bitmap {
        return resizeBitmap(BitmapFactory.decodeResource(resources, R.drawable.marker_green))
    }

    private fun resizeBitmap(bitmap: Bitmap): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, 50, 50, false)
    }
}