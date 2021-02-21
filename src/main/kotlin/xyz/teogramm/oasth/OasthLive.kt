package xyz.teogramm.oasth

import xyz.teogramm.oasth.base.BusRoute
import xyz.teogramm.oasth.base.BusStop
import xyz.teogramm.oasth.live.BusArrival
import xyz.teogramm.oasth.live.BusLocation
import xyz.teogramm.oasth.live.Coordinates
import xyz.teogramm.oasth.util.OasthAPIFetcher.Companion.fetchFromSimpleJSONEndpoint
import java.time.LocalDateTime

/**
 * Class containing static methods to access live data endpoints of the API.
 */
class OasthLive {
    companion object{
        /**
         * Gets list of points that can be used for a detailed representation of a route.
         *
         * @param routeID Internal ID of the route
         * @return Ordered list of [Coordinates] of points used to draw the route.
         */
        @JvmStatic
        fun getRoutePoints(routeID: Int): List<Coordinates> {
            val rawData = fetchFromSimpleJSONEndpoint("getRouteDetailPerRoute",routeID.toString())
            // Returned data contains a list with each point as a Map
            // "x" key of each map is longitude and "y" key is latitude
            // Convert each Map to a coordinates object
            return rawData.mapNotNull {
                // Check if the map has the required keys
                if(it.keys.containsAll(listOf("x", "y"))) {
                    Coordinates(it["x"]!!.toDouble(), it["y"]!!.toDouble())
                }else{
                    null
                }
            }
        }

        /**
         * Does the same thing as [getRoutePoints], but uses the ID of the given [BusRoute] object
         */
        @JvmStatic
        fun getRoutePoints(route: BusRoute): List<Coordinates> {
            return getRoutePoints(route.internalId)
        }

        /**
         * Get information about estimated bus arrival times to a stop.
         *
         * If the returned list is empty, no buses are due to arrive at the stop or an invalid
         * stop ID was given.
         * @param stopID Internal ID of the stop
         * @return List of [BusArrival] objects, sorted by increasing arrival time.
         */
        @JvmStatic
        fun getStopArrivals(stopID: Int): List<BusArrival> {
            val rawData = fetchFromSimpleJSONEndpoint("getStopArrivals", stopID.toString())
            // RawData returns a map for each object in the response
            // Each object must have the following 3 keys in order to be considered valid
            val requiredKeys = listOf("route_code", "veh_code", "btime2")
            return rawData.mapNotNull {
                // If the entry has all the required keys, create the BusArrival object, else
                // consider it invalid and don't include it in the response.
                if(it.keys.containsAll(requiredKeys)){
                    BusArrival(it["route_code"]!!.toInt(), it["veh_code"]!!, it["btime2"]!!.toInt())
                }else{
                    null
                }
            }
        }

        /**
         * Does the same thing as [getStopArrivals], but uses the ID of the given [BusStop].
         */
        @JvmStatic
        fun getStopArrivals(stop: BusStop): List<BusArrival> {
            return getStopArrivals(stop.internalId)
        }

        /**
         * Gets the location of all the buses on the given route.
         *
         * If the returned list is empty, no buses are on the route or the given route ID is invalid.
         * @return List of [BusLocation] objects.
         */
        @JvmStatic
        fun getBusLocations(routeID: Int): List<BusLocation> {
            val rawData = fetchFromSimpleJSONEndpoint("getBusLocation", routeID.toString())
            val requiredKeys = listOf("VEH_NO", "CS_DATE", "CS_LAT", "CS_LNG", "ROUTE_CODE")
            return rawData.mapNotNull {
                // If the entry has all the required keys, create the BusLocation object, else
                // consider it invalid and don't include it in the response.
                if(it.keys.containsAll(requiredKeys)){
                    val locationCoordinates = Coordinates(it["CS_LNG"]!!.toDouble(), it["CS_LAT"]!!.toDouble())
                    // Replace the space in the returned timestamp with a T, in order to enable parsing with
                    // LocalDateTime.parse
                    val time = LocalDateTime.parse(it["CS_DATE"]!!.replace(" ","T"))
                    BusLocation(it["VEH_NO"]!!, time,locationCoordinates, it["ROUTE_CODE"]!!.toInt())
                }else{
                    null
                }
            }
        }

        /**
         * Does the same thing as [getBusLocations], bus uses the ID of the given [BusRoute]
         */
        @JvmStatic
        fun getBusLocations(route: BusRoute): List<BusLocation> {
            return getBusLocations(route.internalId)
        }
    }
}