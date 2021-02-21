Live Data
=========

Live data endpoints are accessible through static methods of the ``xyz.teogramm.oasth.OasthLive`` class. The live endpoints 
accept the corresponding object's internal ID as a parameter. However, an empty response is not differentiated from a response to 
a request with an invalid parameter.

Classes
-------
Classes for storing responses from the live API.

BusArrival
^^^^^^^^^^

+-------------------------+------------------------------------------------------------------+
| routeCode: **Int**      | Internal ID of the route this bus is on.                         |
+-------------------------+------------------------------------------------------------------+
| vehicleCode: **String** | Code of the vehicle that is performing this route.               |
+-------------------------+------------------------------------------------------------------+
| estimatedTime: **Int**  | Estimated time until arrival of the bus to the stop, in minutes. |
+-------------------------+------------------------------------------------------------------+

BusLocation
^^^^^^^^^^^
Location of a bus on the map.

+------------------------------+-------------------------------------------------+
| vehicleCode: **String**      | Code of the bus.                                |
+------------------------------+-------------------------------------------------+
| time: **LocalDateTime**      | Time the location was reported.                 |
+------------------------------+-------------------------------------------------+
| coordinates: **Coordinates** | Coordinates of the bus location.                |
+------------------------------+-------------------------------------------------+
| routeID: **Int**             | Internal ID of the route the bus is performing. |
+------------------------------+-------------------------------------------------+

Coordinates
^^^^^^^^^^^
Pair of latitude, longitude **Doubles**.

Functions
---------

getRoutePoints
^^^^^^^^^^^^^^
Gets a list of points that can be used to draw the exact shape of a route.

If the returned list is empty the route ID given is invalid.

**Parameters**: RouteID as Int, or a BusRoute object.

**Returns**: Ordered list of **Cordinates**.

getStopArrivals
^^^^^^^^^^^^^^^

Gets information about estimated bus arrival times to a stop.

If the returned list is empty, no buses are due to arrive at the stop or an invalid 
stop ID was given.

**Parameters**: stopID as Int, or a BusStop object.

**Returns**: List of **BusArrival** objects ordered by ascending estimated arrival time.

getBusLocations
^^^^^^^^^^^^^^^

Gets the location of all the buses on the given route.

If the returned list is empty, no buses are on the route or the given route ID is invalid.

**Parameters**: routeID as Int, or a BusRoute object.

**Returns**: List of **BusLocation** objects.