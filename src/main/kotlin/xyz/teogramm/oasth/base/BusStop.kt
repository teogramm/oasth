package xyz.teogramm.oasth.base

data class BusStop(
    val internalId: Int,
    val publicId: String,
    val nameEL: String,
    val nameEN: String,
    val heading: Int,
    val longitude: Double,
    val latitude: Double
)
