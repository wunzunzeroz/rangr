package com.rangr.map

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.unit.dp
import com.mapbox.maps.MapView
import com.rangr.R.drawable
import com.rangr.map.components.LocationPermissionHelper
import com.rangr.map.components.RoutingScreen
import com.rangr.map.components.TestScreen
import com.rangr.map.components.ViewingScreen
import com.rangr.map.components.sheets.BottomSheetContent
import com.rangr.map.models.MapState
import com.rangr.map.models.WaypointIconType
import com.rangr.map.models.WaypointMarkerFactory
import com.rangr.ui.theme.RangrDark
import com.rangr.ui.theme.RangrOrange
import java.lang.ref.WeakReference

class MapActivity : ComponentActivity() {
    private val model: MapViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mapView = MapView(this)
        val mapController = MapboxService(mapView)

        val waypointMarkerFactory = WaypointMarkerFactory(getMarkerMap())
        model.addMarkerFactory(waypointMarkerFactory)
        model.setTapIcon(blueMarker())
        model.setRouteIcon(orangeMarker())
        model.setWaypointIcon(greenMarker())

        val locationPermissionHelper = LocationPermissionHelper(WeakReference(this))
        locationPermissionHelper.checkPermissions {
            model.initialise(mapController)
        }


        setContent { MainScreen(model) }
    }

    private fun getMarkerMap(): Map<WaypointIconType, Bitmap> {
        return mapOf(
            WaypointIconType.Flag to getBitmap(drawable.flag),
            WaypointIconType.Marker to getBitmap(drawable.marker),
            WaypointIconType.Pin to getBitmap(drawable.flag),
            WaypointIconType.Cross to getBitmap(drawable.cross),

            WaypointIconType.Circle to getBitmap(drawable.flag),
            WaypointIconType.Triangle to getBitmap(drawable.triangle),
            WaypointIconType.Square to getBitmap(drawable.flag),
            WaypointIconType.Star to getBitmap(drawable.star),

            WaypointIconType.QuestionMark to getBitmap(drawable.question_mark),
            WaypointIconType.ExclamationPoint to getBitmap(drawable.exclamation_mark),
            WaypointIconType.CheckMark to getBitmap(drawable.check_mark),
            WaypointIconType.CrossMark to getBitmap(drawable.cross_mark),

            WaypointIconType.Car to getBitmap(drawable.car),
            WaypointIconType.Boat to getBitmap(drawable.boat),
            WaypointIconType.Plane to getBitmap(drawable.plane),
            WaypointIconType.Helicopter to getBitmap(drawable.helicopter),

            WaypointIconType.Forest to getBitmap(drawable.forest),
            WaypointIconType.Mountain to getBitmap(drawable.mountains),
            WaypointIconType.Water to getBitmap(drawable.water),
            WaypointIconType.Beach to getBitmap(drawable.beach),

            WaypointIconType.Fire to getBitmap(drawable.fire),
            WaypointIconType.Anchor to getBitmap(drawable.anchor),
            WaypointIconType.Lifering to getBitmap(drawable.lifering),
            WaypointIconType.Target to getBitmap(drawable.target),

            WaypointIconType.Tent to getBitmap(drawable.tent),
            WaypointIconType.House to getBitmap(drawable.house),
            WaypointIconType.Building to getBitmap(drawable.building),
            WaypointIconType.Castle to getBitmap(drawable.castle),

            WaypointIconType.Footprints to getBitmap(drawable.footsteps),
            WaypointIconType.Person to getBitmap(drawable.person),
            WaypointIconType.People to getBitmap(drawable.people),
            WaypointIconType.Skull to getBitmap(drawable.skull),

            WaypointIconType.Food to getBitmap(drawable.food),
            WaypointIconType.Drinks to getBitmap(drawable.drinks),
            WaypointIconType.Fuel to getBitmap(drawable.fuel),
            WaypointIconType.WaterSource to getBitmap(drawable.water_source),
        )
    }

    private fun getBitmap(drawable: Int): Bitmap {
        return resizeBitmap(BitmapFactory.decodeResource(resources, drawable))
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
            sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
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
        return resizeBitmap(BitmapFactory.decodeResource(resources, drawable.marker_orange))
    }

    private fun blueMarker(): Bitmap {
        return resizeBitmap(BitmapFactory.decodeResource(resources, drawable.marker_blue))
    }

    private fun greenMarker(): Bitmap {
        return resizeBitmap(BitmapFactory.decodeResource(resources, drawable.marker_green))
    }

    private fun resizeBitmap(bitmap: Bitmap): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, 70, 70, false)
    }
}