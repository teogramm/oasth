package xyz.teogramm.oasth.base

import xyz.teogramm.oasth.base.schedules.BusSchedule

/**
 * A BusLine has multiple routes and an operation schedule.
 */
class BusLine(
    val internalId: Int,
    val number: String,
    val nameEL: String,
    val nameEN: String,
    val routes: List<BusRoute>,
    val schedules: Set<BusSchedule>
){

    fun getCircularRoutes(): List<BusRoute>{
        return getRoutesByType(RouteTypes.CIRCULAR)
    }

    fun getInboundRoutes(): List<BusRoute>{
        return getRoutesByType(RouteTypes.INBOUND)
    }

    fun getOutboundRoutes(): List<BusRoute>{
        return getRoutesByType(RouteTypes.OUTBOUND)
    }

    private fun getRoutesByType(type: RouteTypes): List<BusRoute>{
        return routes.filter { route ->
            route.type == type
        }
    }

    override fun toString(): String {
        return "Line $number - $nameEN"
    }
}
