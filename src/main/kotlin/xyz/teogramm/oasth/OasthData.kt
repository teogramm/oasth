package xyz.teogramm.oasth

import xyz.teogramm.oasth.base.BusLine
import xyz.teogramm.oasth.base.BusMasterLine
import xyz.teogramm.oasth.base.BusRoute
import xyz.teogramm.oasth.base.BusStop
import xyz.teogramm.oasth.base.schedules.BusCalendar

/**
 * Contains static data about the OASTH network.
 *
 * Each field contains a Map matching each object's internal id to the corresponding object. This is done in order
 * to make transferring the data to a database or another format easier.
 *
 * Fields:
 * - masterLines: Contains essentially all information about the network, since it contains all lines, along with routes
 *  and schedules.
 * - lines, routes, stops, calendars: contain all data as fetched from the API. May contain data that is not connected.
 * For example, the stops map may contain stops that are not part of any route.
 *
 * A good rule of thumb is: use the individual maps if you want to have a complete set of data irrespectively of
 * whether that data is in use or not.
 *
 * A good example is if you want to map all stops, there are some stops that are served by lines only active during
 * the summer. If the data is fetched in any other season the respective line and route are not included, but the stop
 * is still included. As such the stop cannot be found using the masterLines map, but exists in the stops map.
 */
class OasthData(
    val masterLines: Map<Int, BusMasterLine>,
    val lines: Map<Int, BusLine>,
    val routes: Map<Int, BusRoute>,
    val stops: Map<Int, BusStop>,
    val calendars: Map<Int, BusCalendar>
)
