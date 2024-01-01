package com.rangr.map.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rangr.map.MapViewModel
import kotlinx.coroutines.launch


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
            MapActionButton(icon = Icons.Filled.MyLocation, onClick = {model.scrollToUserLocation()}, contentDescription = "My location")
            Spacer(modifier = Modifier.height(8.dp))
            MapActionButton(icon = Icons.Filled.Refresh, onClick = {model.toggleMapRotation()}, contentDescription = "Toggle rotation")
        }
    }

}
