package com.rangr.map.components.sheets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import com.rangr.map.MapViewModel
import com.rangr.map.models.SheetType

@Composable
fun BottomSheetContent(model: MapViewModel) {
    val sheetType = model.sheetType.observeAsState()

    when (sheetType.value) {
        SheetType.MapTypeSelection, null -> MapTypeBottomSheet(model)
        SheetType.LocationDetail -> LocationDetailBottomSheet(model)
        SheetType.WaypointCreation -> WaypointCreationBottomSheet(model)
        SheetType.WaypointDetail -> WaypointDetailBottomSheet(model)
        SheetType.GoToLocation -> GoToLocationBottomSheet(model)
    }
}

