package xyz.teogramm.oasth.live

import java.time.LocalDateTime

/**
 * Represents the location of a bus on the map.
 * @param vehicleCode Code of the bus.
 * @param time Time the location was reported.
 * @param coordinates Coordinates of the bus location.
 * @param routeID Internal ID of the route the bus is performing.
 */
data class BusLocation(val vehicleCode: String, val time: LocalDateTime, val coordinates: Coordinates, val routeID: Int) {
}