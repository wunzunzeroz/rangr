package com.rangr.map.components

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.rangr.map.MapViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ViewingScreen2(model: MapViewModel) {
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheetLayout(
        sheetState = sheetState, sheetContent = { MapTypeSheet(model) },
        sheetBackgroundColor = Color.Black,
        sheetContentColor = Color(0xFFFF4F00),
    ) {
        Box {
            MapViewContainer(model)
            Column(modifier = Modifier.padding(8.dp)) {
                Spacer(modifier = Modifier.height(32.dp))
                MapActionButton(
                    icon = Icons.Filled.Layers,
                    onClick = {
                        coroutineScope.launch { sheetState.show() }
                    },
                    contentDescription = "Select map style",
                )
                Spacer(modifier = Modifier.height(8.dp))
                LocateUserButton(model)
                Spacer(modifier = Modifier.height(8.dp))
                ToggleRotationButton(model)
            }

        }
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ViewingScreen(model: MapViewModel) {
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheetLayout(
        sheetState = sheetState, sheetContent = { MapTypeSheet(model) },
        sheetBackgroundColor = Color.Black,
        sheetContentColor = Color(0xFFFF4F00),
    ) {
        Box {
            MapViewContainer(model)
            Column(modifier = Modifier.padding(8.dp)) {
                Spacer(modifier = Modifier.height(32.dp))
                MapActionButton(
                    icon = Icons.Filled.Layers,
                    onClick = {
                        coroutineScope.launch { sheetState.show() }
                    },
                    contentDescription = "Select map style",
                )
                Spacer(modifier = Modifier.height(8.dp))
                LocateUserButton(model)
                Spacer(modifier = Modifier.height(8.dp))
                ToggleRotationButton(model)
            }

        }
    }

}

@Composable
fun LocateUserButton(model: MapViewModel) {
    var buttonClicked by remember { mutableStateOf(false) }

    MapActionButton(
        icon = Icons.Filled.MyLocation, onClick = { buttonClicked = true }, contentDescription = "My location"
    )

    if (buttonClicked) {
        LaunchedEffect(Unit) {
            model.scrollToUserLocation()
            buttonClicked = false
        }
    }
}

@Composable
fun ToggleRotationButton(model: MapViewModel) {
    val rotationEnabled = model.mapRotationEnabled.observeAsState(false)

    val ctx = LocalContext.current
    val toastText = "Map rotation ${if (rotationEnabled.value) "enabled" else "disabled"}"

    MapActionButton(
        icon = Icons.Filled.Refresh, onClick = {
            model.toggleMapRotation()
            Toast.makeText(ctx, toastText, Toast.LENGTH_SHORT).show()
        }, contentDescription = "Toggle rotation"
    )

}
