package com.rangr.map.models

import com.mapbox.geojson.Point
import com.rangr.util.Utils
import org.locationtech.proj4j.BasicCoordinateTransform
import org.locationtech.proj4j.CRSFactory
import org.locationtech.proj4j.CoordinateReferenceSystem
import org.locationtech.proj4j.ProjCoordinate
import kotlin.math.absoluteValue

class GeoPosition(latitude: Double, longitude: Double) {
    val latLngDecimal: LatLngDecimal
    val gridReference: GridRef
    val latLngDegreesMinutes: LatLngDegreesMinutes

    init {
        latLngDecimal = LatLngDecimal(latitude, longitude)
        gridReference = latLngToGridRef(latLngDecimal)
        latLngDegreesMinutes = latLngDecimalToMinutes(latLngDecimal)

    }

    fun toPoint(): Point {
        return Point.fromLngLat(latLngDecimal.longitude, latLngDecimal.latitude)
    }

    companion object {
        fun fromGridRef(input: GridRef): GeoPosition {
            val latLng = gridRefToLatLng(input)
            println("GR->LL: Lat: ${latLng.latitude}, Lng: ${latLng.longitude}")

            return GeoPosition(latLng.latitude, latLng.longitude)
        }

        fun fromDegreesMinutes(latDeg: Int, latMin: Double, latDir: CardinalDirection,  lngDeg: Int, lngMin: Double, lngDir: CardinalDirection): GeoPosition {
            val latitude = convertToDecimal(latDeg, latMin, latDir)
            val longitude = convertToDecimal(lngDeg, lngMin, lngDir)

            return GeoPosition(latitude, longitude)
        }

        fun convertToDecimal(degrees: Int, minutes: Double, cardinalDirection: CardinalDirection): Double {
            val result = degrees + (minutes / 60.0)

            return if (cardinalDirection == CardinalDirection.S || cardinalDirection == CardinalDirection.W) {
                result * -1
            } else {
                result
            }
        }


        fun latLngDecimalToMinutes(input: LatLngDecimal): LatLngDegreesMinutes {
            val latitude = convertToDegreesMinutes(input.latitude, true)
            val longitude = convertToDegreesMinutes(input.longitude, false)

            return LatLngDegreesMinutes(latitude, longitude)
        }

        private fun convertToDegreesMinutes(input: Double, isLatitude: Boolean): DegreesMinutes {
            val decimal = input.absoluteValue % 1
            val degrees = (input.absoluteValue - decimal).toInt()
            val minutes = Utils.RoundNumberToDp(decimal * 60, 4)

            val direction = if (isLatitude) {
                if (input >= 0) CardinalDirection.N else CardinalDirection.S
            } else {
                if (input >= 0) CardinalDirection.E else CardinalDirection.W
            }

            return DegreesMinutes(degrees, minutes, direction)
        }

        fun gridRefToLatLng(input: GridRef): LatLngDecimal {
            val crsFactory = CRSFactory()

            // Define WGS84 and NZTM coordinate reference systems
            val wgs84Crs: CoordinateReferenceSystem = crsFactory.createFromName("EPSG:4326")
            val nztmCrs: CoordinateReferenceSystem = crsFactory.createFromName("EPSG:2193")

            // Create a coordinate transformer
            val ct = BasicCoordinateTransform(nztmCrs, wgs84Crs)

            // Create a coordinate object for the source coordinates
            val nztmCoord = ProjCoordinate(input.eastings.toDouble(), input.northings.toDouble())

            // Create a coordinate object to hold the result
            val wgs84Coord = ProjCoordinate()

            // Perform the transformation
            ct.transform(nztmCoord, wgs84Coord)

            return LatLngDecimal(wgs84Coord.y, wgs84Coord.x)
        }

        fun latLngToGridRef(input: LatLngDecimal): GridRef {
            val crsFactory = CRSFactory()

            // Define WGS84 and NZTM coordinate reference systems
            val wgs84Crs: CoordinateReferenceSystem = crsFactory.createFromName("EPSG:4326")
            val nztmCrs: CoordinateReferenceSystem = crsFactory.createFromName("EPSG:2193")

            // Create a coordinate transformer
            val ct = BasicCoordinateTransform(wgs84Crs, nztmCrs)

            // Create a coordinate object for the source coordinates
            val wgs84Coord = ProjCoordinate(input.longitude, input.latitude)

            // Create a coordinate object to hold the result
            val nztmCoord = ProjCoordinate()

            // Perform the transformation
            ct.transform(wgs84Coord, nztmCoord)

            return GridRef(nztmCoord.x.toInt(), nztmCoord.y.toInt())
        }
    }

}