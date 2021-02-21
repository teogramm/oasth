package xyz.teogramm.oasth.base

import xyz.teogramm.oasth.base.schedules.BusSchedule

/**
 * A BusLine has multiple routes and an operation schedule.
 */
data class BusLine(
    val internalId: Int,
    val number: String,
    val nameEL: String,
    val nameEN: String,
    val routes: List<BusRoute>,
    val schedules: Set<BusSchedule>
)
