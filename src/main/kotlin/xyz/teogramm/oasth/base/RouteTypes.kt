package xyz.teogramm.oasth.base

/**
 * Available route directions.
 *
 * - Outbound is start->end
 * - Inbound is end->start
 * - Circular is start->start
 */
enum class RouteTypes(val id: Int) {
    OUTBOUND(1),
    INBOUND(2),
    CIRCULAR(3);
    companion object {
        private val VALUES = values()

        /**
         * Matches route type id to enum value
         * @return RouteTypes value if id corresponds to enum, null otherwise
         */
        fun getById(id: Int) = VALUES.firstOrNull { it.id == id }
    }
}
