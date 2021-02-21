package xyz.teogramm.oasth.util

import xyz.teogramm.oasth.Oasth

/**
 * Offers methods for making requests to the Oasth API
 */
internal class OasthAPIFetcher {
    companion object {
        /**
         * Gets endpoint data and formats it.
         * @param endpointName Endpoint name, must not start or end with slashes.
         * @return A String List with all entries returned.
         */
        fun fetchEntriesFromEndpoint(endpointName: String): List<String> {
            val stringData = WebFetcher.getGzippedURL(assembleEndpointURL(endpointName))
            return getEntriesFromString(stringData)
        }

        /**
         * Gets data from an endpoint that returns data in JSON format.
         *
         * This function is intended for data returned by the OASTH API in JSON format. It cannot
         * get data from an arbitrary JSON endpoint.
         *
         * @param endpointParameter Optional parameter to include after the endpoint name, is used in live API calls.
         * @return List with each object represented as a HashMap of String key-value pairs.
         */
        fun fetchFromSimpleJSONEndpoint(endpointName: String, endpointParameter: String = ""): List<Map<String,String>>{
            val completeEndpointName = if (endpointParameter.isBlank()){
                endpointName
            }else{
                "${endpointName}/${endpointParameter}"
            }
            val stringData = WebFetcher.getURL(assembleEndpointURL(completeEndpointName))
            /* There is not a single response for invalid data. Some endpoints return null, others an empty array [].
             If a new endpoint is added and the error response is not included, it must be added here.
             */
            // Check if the response actually contains data
            return if (responseContainsData(stringData)){
                formatApiJson(stringData)
            }else{
                emptyList()
            }
        }

        /**
         * Checks whether the response has returned any data.
         *
         * There is not a single error response for no data. Some endpoints return null, others an empty array
         * []. Additionally, in live endpoints a response for an invalid parameter is the same as the
         * empty response (for example requesting arrivals for an invalid stop ID returns null, which is returned
         * when there are no arrivals at the stop).
         */
        private fun responseContainsData(unformattedData: String): Boolean {
            /*
            Known invalid responses
            null - getBusLocation, getStopArrivals
            [] - getRouteDetailPerRoute
             */
            val emptyResponses = listOf("null","[]")
            // Check if returned data contains one of the values for empty responses
            emptyResponses.forEach {
                if(unformattedData.contains(it)){
                    return false
                }
            }
            // Else check if the response is blank
            return unformattedData.isNotBlank()
        }

        /**
         * Function used to convert JSON responses into a Set containing each object as a Map
         * with String key-value pairs.
         *
         * This function is not in any way a complete JSON parses and cannot be used as one.
         * @param unformattedData JSON data from API endpoint as string
         * @return List with each object represented as a HashMap of String key-value pairs.
         */
        private fun formatApiJson(unformattedData: String): List<Map<String,String>>{
            /* This function is not that good, but it works so meh.
            General data format: [{"a":"x","b":"y",...}, {"a":"x","b":"y",...},......,{"a":"x","b":"y",...}]
             */

            // Remove surrounding square brackets
            val data = unformattedData.removeSurrounding("[", "]")
            // Split objects with } character
            var objectStrings = data.split("}")
            // Remove ',' prefix that is present in every object except the first and the last. Then remove
            // the starting curly bracket, present on all objects.
            objectStrings = objectStrings.map { it.removePrefix(",").removePrefix("{") }
            // The objectStrings variable should now be a list each entry having the contents of each object
            val objects = mutableListOf<HashMap<String, String>>()
            for(objectString in objectStrings){
                // Some strings may not contain an item
                if(objectString.isBlank()){
                    continue
                }
                val objectPropertyMap = HashMap<String,String>()
                // Split the key-value pairs
                val properties = objectString.split(",")
                // Store each key-value pair for this object in a map
                properties.forEach { propertyPair ->
                    // Split using quotes, in order to prevent splitting on timestamps
                    // Then, remove the unnecessary quotes
                    val pair = propertyPair.split("\":\"").map{ it.replace("\"","")}
                    // Each property should be in the form: "a":"b", so splitting on the colon produces 2 strings
                    if(pair.size == 2) {
                        objectPropertyMap[pair[0]] = pair[1]
                    }
                }
                // Add the Map for this object to the Set of all objects
                objects.add(objectPropertyMap)
            }
            return objects
        }

        /**
         * Extracts all entries from the given string. Each entry has the format: (..,..,..).
         * @return A List with each entry as a string
         */
        private fun getEntriesFromString(unformattedData: String): List<String> {
            val rawEntries = unformattedData.split("),").toMutableList()
            val rawEntryIterator = rawEntries.listIterator()
            while (rawEntryIterator.hasNext()) {
                var currentPar = rawEntryIterator.next()
                // Remove opening and closing parentheses
                currentPar = currentPar.removePrefix("(").removeSuffix(")")
                // Remove some unnecessary quotes and spaces after commas
                currentPar = currentPar.replace("\"", "").replace(", ", ",")
                rawEntryIterator.set(currentPar)
            }
            return rawEntries.toList()
        }

        /**
         * Creates the URL used to fetch data from given endpoint
         */
        private fun assembleEndpointURL(endpointName: String): String {
            return "${Oasth.API_URL}/$endpointName/?a=1"
        }
    }
}
