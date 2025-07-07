package xyz.teogramm.oasth

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import xyz.teogramm.oasth.base.BusLine
import xyz.teogramm.oasth.base.BusMasterLine
import xyz.teogramm.oasth.base.BusRoute
import xyz.teogramm.oasth.base.BusStop
import xyz.teogramm.oasth.base.schedules.BusCalendar
import xyz.teogramm.oasth.base.schedules.BusSchedule
import xyz.teogramm.oasth.processing.LineProcessor.Companion.createLines
import xyz.teogramm.oasth.processing.LineProcessor.Companion.getRoutesForLineIds
import xyz.teogramm.oasth.processing.MasterLineProcessor.Companion.createMasterLines
import xyz.teogramm.oasth.processing.MasterLineProcessor.Companion.getLinesForMasterLineIds
import xyz.teogramm.oasth.processing.RouteStopProcessor.Companion.createRoutes
import xyz.teogramm.oasth.processing.RouteStopProcessor.Companion.createStops
import xyz.teogramm.oasth.processing.RouteStopProcessor.Companion.getStopIdsForRoutes
import xyz.teogramm.oasth.processing.ScheduleProcessor
import xyz.teogramm.oasth.util.OasthAPIFetcher.Companion.fetchEntriesFromEndpoint

/**
 * Class used for fetching static data about the OASTH network.
 *
 * By default caching is enabled. The first time the data is downloaded it is stored in the object. This behaviour can
 * be disabled by setting the [cachingEnabled] parameter on the constructor to `false`.
 *
 * @see OasthData
 */
class Oasth(private val cachingEnabled: Boolean = true) {

    companion object {
        const val API_URL = "https://old.oasth.gr/el/api"
    }

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val dataFetched = false
    private var data = OasthData(emptyMap(), emptyMap(), emptyMap(), emptyMap(), emptyMap())

    /**
     * Get the data. The first time this method is run the data is fetched from the Internet. Subsequent calls return
     * the data fetched during the first run, if caching is enabled, else they download it again.
     *
     * @return OasthData object
     * @see OasthData
     */
    fun fetchData(): OasthData {
        return runBlocking {
            if (dataFetched && cachingEnabled) {
                return@runBlocking data
            }
            val calendars = coroutineScope.async { getCalendars() }
            val schedules = coroutineScope.async { getSchedules(calendars.await()) }

            val stops = coroutineScope.async { getStops() }
            // Route entries are used twice so fetch them here
            val routeEntries = coroutineScope.async {
                fetchEntriesFromEndpoint("getRoutes")
            }
            val routes = coroutineScope.async { getRoutes(stops, routeEntries) }
            val lines = coroutineScope.async { getLines(routeEntries, routes, schedules) }
            val masterLines = withContext(coroutineScope.coroutineContext) { getMasterLines(lines) }

            data = OasthData(masterLines, lines.await(), routes.await(), stops.await(), calendars.await())
            return@runBlocking data
        }
    }

    /**
     * Get stop data from the API and process it
     */
    private suspend fun getStops(): Map<Int, BusStop> {
        val stopEntries = coroutineScope.async {
            fetchEntriesFromEndpoint("getStopsB")
        }
        return createStops(stopEntries.await())
    }

    /**
     * Get route data from the API and process it using the given stop data.
     */
    private suspend fun getRoutes(stops: Deferred<Map<Int, BusStop>>, routeEntries: Deferred<List<String>>): Map<Int, BusRoute> {
        val routeStopEntries = coroutineScope.async {
            fetchEntriesFromEndpoint("getRouteStops")
        }
        val routeStopIds = getStopIdsForRoutes(routeStopEntries.await())
        return createRoutes(stops.await(), routeEntries.await(), routeStopIds)
    }

    /**
     * Get line data from the API and process it
     */
    private suspend fun getLines(
        routeEntries: Deferred<List<String>>,
        routes: Deferred<Map<Int, BusRoute>>,
        schedules: Deferred<Map<Int, Set<BusSchedule>>>
    ): Map<Int, BusLine> {
        val lineEntries = coroutineScope.async {
            fetchEntriesFromEndpoint("getLines")
        }
        val lineRoutes = getRoutesForLineIds(routeEntries.await(), routes.await())
        return createLines(lineEntries.await(), lineRoutes, schedules.await())
    }

    private suspend fun getMasterLines(lines: Deferred<Map<Int, BusLine>>): Map<Int, BusMasterLine> {
        val masterLineDetailsEntries = coroutineScope.async {
            fetchEntriesFromEndpoint("getMasterlinesDetails")
        }
        val masterLinesEntries = coroutineScope.async {
            fetchEntriesFromEndpoint("getMasterlines")
        }
        val linesForMasterLineIds = getLinesForMasterLineIds(masterLineDetailsEntries.await(), lines.await())
        return createMasterLines(masterLinesEntries.await(), linesForMasterLineIds)
    }

    private suspend fun getCalendars(): Map<Int, BusCalendar> {
        val calendarEntries = coroutineScope.async {
            fetchEntriesFromEndpoint("getSched_cats")
        }
        return ScheduleProcessor.createCalendars(calendarEntries.await())
    }

    private suspend fun getSchedules(calendars: Map<Int, BusCalendar>): Map<Int, Set<BusSchedule>> {
        val scheduleEntries = coroutineScope.async {
            fetchEntriesFromEndpoint("getSched_entries")
        }
        return ScheduleProcessor.createSchedules(scheduleEntries.await(), calendars)
    }
}
