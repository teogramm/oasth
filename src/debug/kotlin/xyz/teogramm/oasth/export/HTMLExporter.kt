package xyz.teogramm.oasth.export

import xyz.teogramm.oasth.base.BusLine
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

/**
 * Contains various methods that export objects to HTML. Used for debugging.
 */
class HTMLExporter {
    companion object {
        @JvmStatic
        fun exportLinesSchedules(lines: Set<BusLine>, fileName: String) {
            val htmlFile = File(fileName)
            val fstream = OutputStreamWriter(FileOutputStream(htmlFile), StandardCharsets.UTF_8)
            val writer = BufferedWriter(fstream)

            val htmlHeader = """
                <!DOCTYPE html>
                <html>
                <head>
                <title>OASTH Lines</title>
                </head>
                <body>
            """.trimIndent()
            writer.write(htmlHeader)
            writer.flush()

            // For each line create
            lines.forEach { line ->
                val lineHeader = "<h1 style=\"text-align:center\">${line.number}-${line.nameEL}</h3>\n"
                writer.write(lineHeader)
                // Create a table for each schedule
                line.schedules.forEach { schedule ->
                    val scheduleHeader = "<h3 style=\"text-align:center\">${schedule.calendar.nameEL}</h1>\n"
                    writer.write(scheduleHeader)
                    val tableHeader = """
                        <table>
                        <tr>
                        <th>Μετάβαση</th>
                        <th>Επιστροφή</th>
                        </tr>
                    """.trimIndent()
                    writer.write(tableHeader)
                    val outboundIterator = schedule.outboundTimes.listIterator()
                    val returnIterator = schedule.inboundTimes.listIterator()
                    while (outboundIterator.hasNext() or returnIterator.hasNext()) {
                        writer.write("<tr>\n")
                        val outboundTimeRow = if (outboundIterator.hasNext()) {
                            "<td>${outboundIterator.next()}</td>\n"
                        } else {
                            "<td></td>\n"
                        }
                        writer.write(outboundTimeRow)
                        val returnTimeRow = if (returnIterator.hasNext()) {
                            "<td>${returnIterator.next()}</td>\n"
                        } else {
                            "<td></td>\n"
                        }
                        writer.write(returnTimeRow)
                        writer.write("</tr>\n")
                    }
                    writer.write("</table>\n")
                }
                writer.flush()
            }
            writer.close()
        }
    }
}
