package xyz.teogramm.oasth.base

/**
 * A masterline contains multiple similar lines.
 */
data class BusMasterLine(val internalId: Int,
    val number: String,
    val nameEL: String,
    val nameEN: String,
    val lines: List<BusLine>
)
