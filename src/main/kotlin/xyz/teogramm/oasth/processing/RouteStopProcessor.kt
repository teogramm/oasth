package xyz.teogramm.oasth.processing

import xyz.teogramm.oasth.base.BusRoute
import xyz.teogramm.oasth.base.BusStop
import xyz.teogramm.oasth.base.RouteTypes

/**
 * RouteStopProcessor is responsible for performing operations on route and stop entries.
 */
internal class RouteStopProcessor {
    companion object {
        /**
         * Creates BusRoutes and adds stops to them from the given data.
         *
         * If a routeId does not have an entry in the routeStopIds map, the created route is created with no stops.
         * Each stopId referenced in routeStopIds must have a corresponding BusStop object in the stops map.
         *
         * Route entries have the form:
         * `route_id (int), line_id (int), nameEL (str), nameEN (str), type (int), distance (int or double unused)`
         *
         * @param stops Map matching a stop's internal id to a BusStop object.
         * @param routeEntries String list with route data as fetched from the API
         * @param routeStopIds HashMap matching each route ID to a list with the stop IDs
         */
        fun createRoutes(stops: Map<Int, BusStop>, routeEntries: List<String>, routeStopIds: Map<Int, List<Int>>):
            Map<Int, BusRoute> {
                // Create the routes using the route entries and the calculated stops
                val routes = HashMap<Int, BusRoute>(routeEntries.size)
                routeEntries.forEach { routeEntry ->
                    val values = routeEntry.split(",")
                    val routeId = values[0].toInt()
                    val nameEL = values[2]
                    val nameEN = values[3]
                    // Convert type to enum
                    val type = RouteTypes.getById(values[4].toInt()) ?: throw IllegalArgumentException("Route type value $values[4] is not valid")
                    // Get stop IDs for this route, if they do not exist use an empty list
                    val thisRouteStopIds = routeStopIds[routeId] ?: emptyList()
                    // Match each stopId to the corresponding BusRoute
                    val matchedStops = thisRouteStopIds.map {
                        stops[it] ?: throw IllegalArgumentException("Stop with ID $it is referenced, but is not in the given stops Map.")
                    }
                    routes[routeId] = BusRoute(routeId, nameEL, nameEN, matchedStops, type)
                }
                return routes
            }

        /**
         * Processes entries matching each route to the corresponding stops.
         *
         * Entries have the form:
         * `entry_id,route_id,stop_id,stop_index`
         * @param routeStopEntries String list with data assigning stops to each route
         * @return HashMap matching each route ID to a list with the stop IDs in correct order (as they are on the route)
         */
        fun getStopIdsForRoutes(routeStopEntries: List<String>): Map<Int, List<Int>> {
            // For each route id create a list containing index, stopId pairs that
            // will be sorted afterwards
            val routeStopsUnordered = HashMap<Int, MutableList<Pair<Int, Int>>>()
            routeStopEntries.forEach { routeStopEntry ->
                val values = routeStopEntry.split(",")
                val routeId = values[1].toInt()
                val stopId = values[2].toInt()
                val index = values[3].toInt()
                // Get the stop list for the given route, if it exists. Else create a new empty list.
                val routeStopList = routeStopsUnordered.getOrPut(routeId, { mutableListOf() })
                routeStopList.add(Pair(index, stopId))
            }
            // Create a new map that matches each route to an ordered list of stopIds
            val routeStopsOrdered = HashMap<Int, List<Int>>(routeStopsUnordered.keys.size)
            routeStopsUnordered.forEach { route ->
                val stopListUnordered = route.value
                // Sort the list based on the index of each stop, then remove the index and turn the list into
                // a simple list of integers by keeping only the stop id of each pair.
                val stopListOrdered = stopListUnordered.sortedBy { it.first }.map { it.second }
                routeStopsOrdered[route.key] = stopListOrdered
            }
            return routeStopsOrdered
        }

        /**
         * Creates BusStop objects from given entries.
         *
         * Each entry has the form:
         * `stop_id (int), public_id (int stored as string), nameEL (str), nameEN (str), addressEL (str unused),
         *  addressEN (str unused),heading (int), longitude (double), latitude (double), type (int), accessible
         *  (int unused), destinationsEL (str unused), destinationsEN (str unused)`
         *
         * @param stopEntries List of stop entry strings
         * @return Map matching each stop's internal id to a BusStop object
         */
        fun createStops(stopEntries: List<String>): Map<Int, BusStop> {
            val stops = HashMap<Int, BusStop>(stopEntries.size)
            // Create a BusStop object for each entry and store it in the stops Map
            for (entry in stopEntries) {
                val values = entry.split(",")
                val internalId = values[0].toInt()
                val publicId = values[1]
                val nameEL = values[2]
                val nameEN = values[3]
                val heading = values[6].toInt()
                val longitude = values[7].toDouble()
                val latitude = values[8].toDouble()
                val tempStop = BusStop(internalId, publicId, nameEL, nameEN, heading, longitude, latitude)
                stops[internalId] = tempStop
            }
            return stops
        }
    }
}
