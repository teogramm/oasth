package xyz.teogramm.oasth.processing

import xyz.teogramm.oasth.base.BusLine
import xyz.teogramm.oasth.base.BusMasterLine

/**
 * MasterLineProcessor is responsible for creating MasterLines and assigning lines to them.
 */
internal class MasterLineProcessor {
    companion object {
        /**
         * Creates a Map assigning lines to their corresponding MasterLines.
         *
         * If a line is referenced in an entry but is not found in the lines Map, the entry is ignored.
         * @param masterLineDetailsEntries List of entries matching lines to MasterLines, returned by the
         * getMasterLinesDetails API endpoints
         * @param lines Map matching internal line ids to the corresponding BusLine object
         * @return Map matching each MasterLine id to a list of BusLine objects
         */
        fun getLinesForMasterLineIds(masterLineDetailsEntries: List<String>, lines: Map<Int, BusLine>):
            Map<Int, List<BusLine>> {
                val masterLinesToLines = HashMap<Int, MutableList<BusLine>>()
                masterLineDetailsEntries.forEach { entry ->
                    val values = entry.split(",")
                    val masterLineId = values[1].toInt()
                    val lineId = values[2].toInt()
                    // For some reason there is a specific entry in masterLinesDetails response that references a line and
                    // masterline that do not exist. For this reason we first check that the referenced line actually
                    // exists.
                    val thisLine = lines[lineId]
                    if (thisLine != null) {
                        // Get the existing list of bus lines for this masterline id, if it does not exist create an empty
                        // list and put it in the map
                        val linesForThisMasterLine = masterLinesToLines.getOrPut(masterLineId, { mutableListOf() })
                        linesForThisMasterLine.add(thisLine)
                    }
                }
                // Convert mutable lists to lists
                return masterLinesToLines.mapValues {
                    it.value.toList()
                }
            }

        /**
         * Creates BusMasterLine objects.
         * <p>
         * If a masterline id has no BusLine list in the linesForMasterLineIds Map, the created MasterLine has no lines
         * associated with it.
         */
        fun createMasterLines(masterLineEntries: List<String>, linesForMasterLineIds: Map<Int, List<BusLine>>):
            Map<Int, BusMasterLine> {
                val masterLines = HashMap<Int, BusMasterLine>()
                masterLineEntries.forEach { entry ->
                    val values = entry.split(",")
                    val internalId = values[0].toInt()
                    val masterLineNumber = values[1]
                    val nameEL = values[2]
                    val nameEN = values[3]
                    val linesList = linesForMasterLineIds.getOrDefault(internalId, emptyList())
                    masterLines[internalId] = BusMasterLine(internalId, masterLineNumber, nameEL, nameEN, linesList)
                }
                return masterLines
            }
    }
}
