package xyz.teogramm.oasth.export

import xyz.teogramm.oasth.base.BusStop
import xyz.teogramm.oasth.live.BusArrival
import xyz.teogramm.oasth.live.BusLocation
import xyz.teogramm.oasth.live.Coordinates
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

class TextExporter {
    companion object{
        fun exportCoordinatesToFile(fileName: String, coordinates: List<Coordinates>) {
            val htmlFile = File(fileName)
            val fstream = OutputStreamWriter(FileOutputStream(htmlFile), StandardCharsets.UTF_8)
            val writer = BufferedWriter(fstream)

            coordinates.forEach {
                writer.write("${it.latitude},${it.longitude}${System.lineSeparator()}")
            }
            writer.flush()
            writer.close()
            fstream.close()
        }

        fun exportArrivalsToFile(fileName: String, arrivals: List<BusArrival>) {
            val htmlFile = File(fileName)
            val fstream = OutputStreamWriter(FileOutputStream(htmlFile), StandardCharsets.UTF_8)
            val writer = BufferedWriter(fstream)

            arrivals.forEach {
                writer.write("${it.routeCode},${it.vehicleCode},${it.estimatedTime}${System.lineSeparator()}")
            }
            writer.flush()
            writer.close()
            fstream.close()
        }

        fun exportLocationsToFile(fileName: String, locations: List<BusLocation>) {
            val htmlFile = File(fileName)
            val fstream = OutputStreamWriter(FileOutputStream(htmlFile), StandardCharsets.UTF_8)
            val writer = BufferedWriter(fstream)

            locations.forEach {
                writer.write("${it.vehicleCode},${it.routeID},${it.time},${it.coordinates.longitude}," +
                    "${it.coordinates.latitude},${System.lineSeparator()}")
            }
            writer.flush()
            writer.close()
            fstream.close()
        }

        fun exportStopsToCSV(fileName: String, stops: List<BusStop>) {
            val csvFile = File(fileName)
            val fstream = OutputStreamWriter(FileOutputStream(csvFile), StandardCharsets.UTF_8)
            val writer = BufferedWriter(fstream)

            val csvHeader = "stopid,stopname,longitude,latitude"
            writer.write(csvHeader)
            stops.forEach {
                writer.write("${it.internalId},${it.nameEL},${it.longitude},${it.latitude},${System.lineSeparator()}")
            }
            writer.flush()
            writer.close()
            fstream.close()
        }
    }
}
