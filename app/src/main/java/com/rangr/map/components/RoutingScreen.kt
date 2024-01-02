package com.rangr.map.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.rangr.map.MapViewModel
import com.rangr.map.models.Route


@Composable
fun RoutingScreen(model: MapViewModel) {
    val route by model.route.observeAsState(Route.empty())

    val chartProducer = model.routeProfile

    Box(
        modifier = Modifier
            .background(color = Color.Black)
            .fillMaxWidth()
    ) {
        Column {
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(8.dp)
                ) {
//                    Text("ACTIVE ROUTE", color = Color.White)
                    Text("DISTANCE: ${route.distance} m", color = Color(0xFFFF4F00))
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(onClick = {
                    model.clearRoute()
                }) {
                    Text("Clear Route")
                }
            }
        }
        Chart(
            chart = lineChart(spacing = 1.dp),
            chartModelProducer = chartProducer,
            startAxis = rememberStartAxis(),
            bottomAxis = rememberBottomAxis(),
        )
    }

}

