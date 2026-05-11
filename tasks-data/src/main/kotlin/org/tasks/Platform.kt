package org.tasks

import java.util.Locale

fun Long.printTimestamp(): String = org.tasks.time.printTimestamp(this)

fun formatCoordinates(coordinates: Double, latitude: Boolean): String {
    val direction = if (latitude) {
        if (coordinates >= 0) "N" else "S"
    } else {
        if (coordinates >= 0) "E" else "W"
    }
    return String.format(Locale.US, "%.6f° %s", Math.abs(coordinates), direction)
}
