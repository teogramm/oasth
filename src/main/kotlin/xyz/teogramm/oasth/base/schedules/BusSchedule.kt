package xyz.teogramm.oasth.base.schedules

import java.time.LocalTime

/**
 * A BusSchedule has the departure times for a line and a BusCalendar indicating when the schedule is active.
 *
 * There are 2 Lists containing departure times:
 *
 * + inboundTimes contains departure times for inbound routes
 * + outboundTimes contains departure times for outbound routes
 *
 * Both lists are sorted.
 *
 * For the exact definition of inbound and outbound see [org.teogramm.oasth.base.RouteTypes].
 * Lines with circular routes contain entries only in the outboundTimes List.
 */
data class BusSchedule(
    val calendar: BusCalendar,
    val outboundTimes: List<LocalTime>,
    val inboundTimes: List<LocalTime>
)
