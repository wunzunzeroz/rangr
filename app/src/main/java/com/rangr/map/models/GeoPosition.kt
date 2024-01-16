package com.rangr.map.models

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

    companion object {
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