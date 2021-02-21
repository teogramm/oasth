.. _organization:

Data organization and structure
===============================

Entities
--------

- Calendar: a set of days
- Schedule: a list of bus departure times, associated with a calendar that indicates when the schedule is active.
- Stop: a designated place where buses stop for passengers to get on and off the bus.
- Route: a list of stops in a specific order. A route might be:
- + Outbound: traveling from start to end.
- + Return: traveling from end to start.
- + Circular: starting and ending at the same stop.
- Line: groups multiple routes. A line can have multiple routes associated with it. A circular line might have 1 route, while 
- some lines might have different routes for different days of the week. A line contains a schedule for the outbound and return 
- directions.
- Masterline: groups multiple lines. All lines of a masterline begin with the same number and might have a letter attached. For example, 
  masterline 83 might include lines 83, 83B and 83T.

All entities that are fetched from the API have an internal ID. This ID is used when getting information from the live endpoints. It can also 
be used when storing the data in a database. The internal ID is different only between objects of the same class (routes, lines etc.).