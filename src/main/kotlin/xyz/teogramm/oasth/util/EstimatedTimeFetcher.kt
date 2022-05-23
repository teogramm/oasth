package xyz.teogramm.oasth.util

import xyz.teogramm.oasth.Oasth
import xyz.teogramm.oasth.base.RouteTypes
import java.time.DayOfWeek
import java.time.LocalTime

class EstimatedTimeFetcher {
    companion object {
        /**
         * Fetch the estimated trip time between two stops of a line, on a given day and time.
         *
         * Trip time estimations require the user to specify an 1-hour interval, since the estimated time varies
         * depending on the time of day.
         * @param lineId: Internal ID of the line
         * @param direction: Direction the line
         * @param originStopPublicId: Public code of the origin stop
         * @param destinationStopPublicId: Public code of the destination stop
         * @param time: LocalTime object containing the start of the 1-hour time interval. Only the hour is used so
         *              16:38:00, will fetch estimated trip times between 16:00 and 17:00.
         * @return Estimated time of trip, null if the given parameters were invalid
         */
        fun fetchTripTimeEstimation(
            lineId: Int, direction: RouteTypes, originStopPublicId: String,
            destinationStopPublicId: String, dayOfWeek: DayOfWeek, time: LocalTime
        ): Int? {
            val directionString = getDirectionStringForRouteType(direction)
            val timeString = time.hour
            // In oasth days 1 = sunday 7 = saturday,
            // in java days 1 = monday 7 = sunday
            val dayInt = (dayOfWeek.value % 7) + 1
            // Url template
            // /en/routeinfo/estimation/direction/public_id_origin/public_id_destination/day/time/lineId/?a=1
            val requestUrl = "https://oasth.gr/en/routeinfo/estimation/${directionString}/" +
                             "${originStopPublicId}/${destinationStopPublicId}/${dayInt}/${timeString}/${lineId}/?a=1"
            val response = WebFetcher.getURL(requestUrl)
            return extractTimeFromResponse(response)
        }

        /**
         * @return Approximated trip time in the given response, null if approximated time was not found
         */
        private fun extractTimeFromResponse(response: String): Int? {
            val validResponseRegex = Regex("It is estimated that the bus route (.+) " +
                "executing the outward journey, (\\w+) at (\\d?\\d:\\d\\d) , " +
                "from bus stop (.+) to bus stop (.+) will cross the distance in (\\d+)' minutes approximately")
            return validResponseRegex.find(response)?.groupValues?.last()?.toInt()
        }

        /**
         * Convert a RouteType enum value into a string to be used in the trip time estimation request.
         *
         * The trip time estimation endpoint uses "a" for outward or circular routes of a line and "b" for inward
         * routes.
         * @return String to be used in the direction field of the trip estimation request
         */
        private fun getDirectionStringForRouteType(routeTypes: RouteTypes): String{
            val map = mapOf(
                RouteTypes.CIRCULAR to "a",
                RouteTypes.OUTBOUND to "a",
                RouteTypes.INBOUND to "b"
            )
            return map.getValue(routeTypes)
        }
    }
}