package xyz.teogramm.oasth.processing

import xyz.teogramm.oasth.base.schedules.BusCalendar
import xyz.teogramm.oasth.base.schedules.BusSchedule
import java.time.DayOfWeek
import java.time.LocalTime

/**
 * ScheduleProcessor is responsible for performing operations on schedules.
 * If offers functions to create schedule categories and extract schedule
 * information from lines.
 *
 * # General Information
 * Times are stored for each line. This means that schedule information is the same for
 * all the routes a line follows.
 * A schedule represents a list of times and is connected with a calendar that indicates
 * when this schedule is active.
 * As there is no connection between different routes and departure times, each line must contain two
 * separate time sets for each calendar. One for the outbound direction and one for the return direction.
 * Circular routes only include
 */
internal class ScheduleProcessor {
    companion object {

        /**
         * Object used when extracting scheduling information.
         * It's used to store the departure times associated with a calendar for a specific line
         * while processing the schedule entries and before converting them into BusSchedule objects.
         */
        private class ScheduleTimes {
            val outboundTimes = HashSet<LocalTime>()
            val returnTimes = HashSet<LocalTime>()
        }

        /**
         * Create BusSchedule objects from the given entries.
         *
         * Given schedule entries have the form:
         * `(unused_int),calendar_id,(unused int),(unused int),line_id_1,(unused int),
         * departure_time_1,(unused string),line_id_2,(unused_int),departure_time_2,(unused int), (unused int)`
         *
         * @param scheduleEntries Entries with schedule information as list of Strings
         * @param calendars Map matching each calendar's id to a BusCalendar object
         * @return Map matching each lineId to a Set of BusSchedule objects
         */
        @JvmStatic
        fun createSchedules(scheduleEntries: List<String>, calendars: Map<Int, BusCalendar>): Map<Int, Set<BusSchedule>> {
            val schedulesMap = HashMap<Int, HashSet<BusSchedule>>()
            // Get the departure times for each map
            val lineCalendarTimes = extractDepartureTimes(scheduleEntries)
            lineCalendarTimes.forEach { (lineId, lineCalendars) ->
                // If given line does not have a set on the schedulesMap, create an empty set
                val schedulesSetForLine = schedulesMap.getOrPut(lineId, { hashSetOf() })
                // Create a BusSchedule object for each calendar associated with this line
                lineCalendars.forEach { (calendarId, calendarTimes) ->
                    val calendar = calendars[calendarId] ?: throw IllegalArgumentException(
                        "Calendar with id $calendarId " +
                            "referenced in schedule entries but is not in calendars map."
                    )
                    // Convert each set with times to a sorted list
                    val schedule = BusSchedule(
                        calendar, calendarTimes.outboundTimes.toList().sorted(),
                        calendarTimes.returnTimes.toList().sorted()
                    )
                    // Add the schedule to the schedules set for the line
                    schedulesSetForLine.add(schedule)
                }
            }
            return schedulesMap
        }

        /**
         * Gets departure times for each lineId-calendarId combination and stores them in a ScheduleTimes
         * object.
         *
         * For schedule entry format see [createSchedules]
         *
         * @param scheduleEntries List of schedule entries as Strings
         * @return Map matching each lineId to a Map matching each calendarId for that line to a ScheduleTimes
         * object containing the departure times for that lineId-calendarId combination.
         */
        private fun extractDepartureTimes(scheduleEntries: List<String>): Map<Int, Map<Int, ScheduleTimes>> {
            // This function is the worst
            // Maps each lineId to a Map that maps each calendarId to a ScheduleTimes object
            val lineCalendarTimesMap = HashMap<Int, HashMap<Int, ScheduleTimes>>()
            scheduleEntries.forEach { entry ->
                val values = entry.split(",")
                val calendarId = values[1].toInt()
                // Each entry contains two departure time entries
                // The first entry is the outgoing departure time
                // The second is the return time.
                // Any one of them might be "null"
                // Calendar id is the same for both entries
                // Check if the first entry is not null
                if (values[6] != "null") {
                    // Process first entry
                    val lineId1 = values[4].toInt()
                    val departureTime1 = LocalTime.parse(values[6])
                    // Get calendar map for line 1, or create one if one does not exist for the given lineId
                    val calendarsMap1 = lineCalendarTimesMap.getOrPut(lineId1, { hashMapOf() })
                    // Get the ScheduleTimes corresponding to the entry calendar. If a ScheduleTimes object does not
                    // exist for the given calendar create a new empty one.
                    val scheduleTimes1 = calendarsMap1.getOrPut(calendarId, { ScheduleTimes() })
                    // Since this is the first entry add the time to the outbound times list
                    scheduleTimes1.outboundTimes.add(departureTime1)
                }
                // Process second entry, if it exists
                if (values[10] != "null") {
                    // Do the same operations as before
                    val lineId2 = values[8].toInt()
                    val departureTime2 = LocalTime.parse(values[10])
                    val calendarsMap2 = lineCalendarTimesMap.getOrPut(lineId2, { hashMapOf() })
                    val scheduleTimes2 = calendarsMap2.getOrPut(calendarId, { ScheduleTimes() })
                    // Since this is the second time in the entry add it to the returnTimes set
                    scheduleTimes2.returnTimes.add(departureTime2)
                }
            }
            return lineCalendarTimesMap
        }

        /**
         * Creates BusCalendar objects from given entries.
         *
         * Each entry has the form:
         * `category_id,nameEL,nameEN,days_active,months_active`
         *
         * @param calendarEntries List of entry strings
         * @return Map matching each calendar's internal ID to a BusCalendar object
         */
        @JvmStatic
        fun createCalendars(calendarEntries: List<String>): Map<Int, BusCalendar> {
            val calendars = HashMap<Int, BusCalendar>()
            calendarEntries.forEach { entry ->
                val values = entry.split(",")
                val calendarId = values[0].toInt()
                val nameEL = values[1]
                val nameEN = values[2]
                val daysActive = extractDaysFromBitString(values[3])
                calendars[calendarId] = BusCalendar(calendarId, nameEL, nameEN, daysActive)
            }
            return calendars
        }

        /**
         * Get a 7 character bit string. Each bit represents a day from Sunday to Saturday, left
         * to right. When a bit is 1 it the corresponding day is regarded as active.
         * @param daysActive 7-character Bit String
         * @return Set of DayOfWeek objects for active days.
         */
        private fun extractDaysFromBitString(daysActive: String): Set<DayOfWeek> {
            val daysActiveList = hashSetOf<DayOfWeek>()
            if (daysActive.length != 7) {
                throw IllegalArgumentException("Given string length must be exactly 7.")
            }
            // Iterate over each character of the given string
            daysActive.forEachIndexed { index, value ->
                // Treat every non-zero character as true
                val isActive = value.toString() != "0"
                if (isActive) {
                    // DayOfWeek.of accepts an int from 1-7 Monday-Sunday. The only case we want to process
                    // is Sunday which is index 0 on the string but value 7 on the function. Other days' indexes can be
                    // just given to the function.
                    val dayIndex = if (index == 0) {
                        7
                    } else {
                        index
                    }
                    daysActiveList.add(DayOfWeek.of(dayIndex))
                }
            }
            return daysActiveList
        }
    }
}
