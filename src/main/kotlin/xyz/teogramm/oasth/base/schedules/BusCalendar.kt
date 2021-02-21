package xyz.teogramm.oasth.base.schedules

import java.time.DayOfWeek

/**
 * A Calendar is a set of days. It is associated with a schedule to indicate when the schedule is in effect.
 */
class BusCalendar(val id: Int, val nameEL: String, val nameEN: String, private val daysActive: Set<DayOfWeek>) {
    fun isActiveOn(day: DayOfWeek): Boolean {
        return day in daysActive
    }
}
