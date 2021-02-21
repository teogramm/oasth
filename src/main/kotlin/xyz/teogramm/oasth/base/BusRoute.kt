package xyz.teogramm.oasth.base

/**
 * A route is a sequence of stops.
 */
data class BusRoute(val internalId: Int, val nameEL: String, val nameEN: String, val Stops: List<BusStop>, val type: RouteTypes)
