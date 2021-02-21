package xyz.teogramm.oasth.processing

import xyz.teogramm.oasth.base.BusLine
import xyz.teogramm.oasth.base.BusRoute
import xyz.teogramm.oasth.base.schedules.BusSchedule

/**
 * LineProcessor is responsible for performing operations on line entries
 */
internal class LineProcessor {
    companion object {
        /**
         * Creates a Map matching line ids to their corresponding routes.
         *
         * For route entries format see: [RouteStopProcessor.Companion.createRoutes]
         *
         * @param routeEntries List with route entries
         * @param routes Map matching each routeId to a BusRoute object
         * @return Map matching each lineId to a List with the corresponding BusRoute objects
         */
        fun getRoutesForLineIds(routeEntries: List<String>, routes: Map<Int, BusRoute>): Map<Int, List<BusRoute>> {
            val lineRoutes = HashMap<Int, MutableList<BusRoute>>()
            routeEntries.forEach { entry ->
                val values = entry.split(",")
                val routeId = values[0].toInt()
                val lineId = values[1].toInt()
                // Get list of routes corresponding to this line
                val thisLineRouteList = lineRoutes.getOrPut(lineId, { mutableListOf() })
                val route = routes[routeId] ?: throw IllegalArgumentException("Route with $routeId referenced, but is not in routes Map.")
                thisLineRouteList.add(route)
            }
            // Convert mutable lists to lists
            return lineRoutes.mapValues {
                it.value.toList()
            }
        }

        /**
         * Create BusLine objects.
         *
         * If a route list for a line does not exist in the routesForLineIds Map, the line
         * is created with no routes.
         *
         * If a schedule set for a line does not exist in the schedulesForLineIds Map, the line is
         * created with no schedules.
         * @param lineEntries Unprocessed line entries
         * @param routesForLineIds Map matching each line id to a list of BusRoute objects
         * @param schedulesForLineIds Map matching each line id to a set of BusSchedule objects
         * @return Map matching each line id to a BusLine object
         */
        fun createLines(
            lineEntries: List<String>,
            routesForLineIds: Map<Int, List<BusRoute>>,
            schedulesForLineIds: Map<Int, Set<BusSchedule>>
        ): Map<Int, BusLine> {
            val lines = HashMap<Int, BusLine>()
            lineEntries.forEach { entry ->
                val values = entry.split(",")
                val internalId = values[0].toInt()
                val lineNumber = values[1]
                val nameEL = values[2]
                val nameEN = values[3]
                // If no routes exist for the line, assume an empty list
                val lineRoutes = routesForLineIds[internalId] ?: emptyList()
                // If no schedules exist for the line, assume an empty list
                val lineSchedules = schedulesForLineIds[internalId] ?: emptySet()
                lines[internalId] = BusLine(internalId, lineNumber, nameEL, nameEN, lineRoutes, lineSchedules)
            }
            return lines
        }
    }
}
