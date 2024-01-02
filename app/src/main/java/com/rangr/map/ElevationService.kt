package com.rangr.map

import com.mapbox.geojson.Point
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import org.json.JSONArray
import org.json.JSONObject

@Serializable
data class Data(val elevation: Double, val latitude: Double, val longitude: Double)

@Serializable
data class Response(val results: List<Data>)

class ElevationService {
    fun getElevationForPoint(point: Point): Double {
        TODO("Not done yet")
    }

    suspend fun addElevationToPoints(points: List<Point>): List<Point> {
        val client = HttpClient(CIO)

        return withContext(Dispatchers.IO) {
            try {
                val query = buildQuery(points)
                println(query)
                val url = "https://api.open-elevation.com/api/v1/lookup?locations=$query"

                val response = client.get(url)

                val json = JSONObject(response.bodyAsText())

                val parsed = parsePoints(json.toString())

                return@withContext parsed
            } finally {
                client.close()
            }
        }

    }

    private fun parsePoints(json: String): List<Point> {
        val data = Json.decodeFromString<Response>(json)

        return data.results.map { Point.fromLngLat(it.longitude, it.latitude, it.elevation) }
    }

    private fun buildQuery(points: List<Point>): String {
        return points.joinToString("|") { "${it.latitude()}, ${it.longitude()}" }
    }
}