package com.example.util

import android.location.Location

object LocationVerifier {
    fun calculateDistanceMeters(
        startLatitude: Double,
        startLongitude: Double,
        endLatitude: Double,
        endLongitude: Double
    ): Float {
        val results = FloatArray(1)
        Location.distanceBetween(
            startLatitude, startLongitude,
            endLatitude, endLongitude,
            results
        )
        return results[0]
    }

    fun isLocationValid(distance: Float, allowedRadiusMeters: Int): Boolean {
        return distance <= allowedRadiusMeters
    }
}
