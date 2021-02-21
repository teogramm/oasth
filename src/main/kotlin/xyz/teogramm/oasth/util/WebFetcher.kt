package xyz.teogramm.oasth.util

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.util.zip.GZIPInputStream

/**
 * Utility class for making web requests
 */
internal class WebFetcher {
    companion object {
        /**
         * Fetches the contents of the given URL.
         * @return Contents of given url as string
         */
        fun getURL(url: String): String {
            return URL(url).openStream().use { it.bufferedReader().readLines().joinToString() }
        }

        /**
         * Fetches and decompresses the contents of the given URL.
         * @return String with URL contents
         */
        fun getGzippedURL(url: String): String {
            // We need to create a GZIP Input Stream, then a Reader from that and then
            // a Buffered Reader to read the server response.
            return URL(url).openStream().use {
                BufferedReader(InputStreamReader(GZIPInputStream(it))).readLines().joinToString()
            }
        }
    }
}
