package com.rangr.map.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import com.rangr.map.MapViewModel
import com.rangr.map.models.SheetType

@Composable
fun BottomSheetContent(model: MapViewModel) {
    val sheetType = model.sheetType.observeAsState()

    when (sheetType.value) {
        SheetType.LocationDetail -> LocationDetailsBottomSheet(model)
        SheetType.MapTypeSelection -> MapTypeBottomSheet(model)
        null -> MapTypeBottomSheet(model)
    }
}