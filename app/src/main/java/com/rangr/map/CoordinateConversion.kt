package com.rangr.map

import com.rangr.map.models.GridRef
import com.rangr.util.Utils
import org.locationtech.proj4j.BasicCoordinateTransform
import org.locationtech.proj4j.CRSFactory
import org.locationtech.proj4j.CoordinateReferenceSystem
import org.locationtech.proj4j.ProjCoordinate
import kotlin.math.absoluteValue

class CoordinateConversion {
    companion object {
        fun LatLngDecimalToMinutes(lat: Double, lng: Double): DegreesMinutes {
            val latitude = convert(lat, true)
            val longitude = convert(lng, false)

            return DegreesMinutes(latitude, longitude)
        }

        private fun convert(input: Double, isLatitude: Boolean): String {
            val decimal = input.absoluteValue % 1
            val degrees = (input.absoluteValue - decimal).toInt()
            val minutes = decimal * 60

            val direction = if (isLatitude) {
                if (input >= 0) "N" else "S"
            } else {
                if (input >= 0) "E" else "W"
            }

            return "$degrees° ${Utils.RoundNumberToDp(minutes, 4)}' $direction"
        }

        fun LatLngToGridRef(lat: Double, lng: Double): GridRef? {
            val crsFactory = CRSFactory()

            // Define WGS84 and NZTM coordinate reference systems
            val wgs84Crs: CoordinateReferenceSystem = crsFactory.createFromName("EPSG:4326")
            val nztmCrs: CoordinateReferenceSystem = crsFactory.createFromName("EPSG:2193")

            // Create a coordinate transformer
            val ct = BasicCoordinateTransform(wgs84Crs, nztmCrs)

            // Create a coordinate object for the source coordinates
            val wgs84Coord = ProjCoordinate(lng, lat)

            // Create a coordinate object to hold the result
            val nztmCoord = ProjCoordinate()

            // Perform the transformation
            ct.transform(wgs84Coord, nztmCoord)

            return GridRef(nztmCoord.x.toInt(), nztmCoord.y.toInt())
        }
    }
}