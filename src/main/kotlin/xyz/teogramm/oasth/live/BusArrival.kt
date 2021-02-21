package xyz.teogramm.oasth.live

/**
 * Contains information about an estimated arrival time of a bus at a bus stop.
 *
 * @property routeCode Internal ID of the route this bus is on
 * @property vehicleCode Code of the vehicle that is performing this route
 * @property estimatedTime Estimated time until arrival of the bus to the stop, in minutes
 */
data class BusArrival(val routeCode: Int, val vehicleCode: String, val estimatedTime: Int) {
}