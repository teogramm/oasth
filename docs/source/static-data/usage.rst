Usage
=====

Fetching the data

.. code-block:: java

    import org.teogramm.oasth.Oasth

    Oasth o = new Oasth();
    OasthData d = o.fetchData();

The fetchData method returns an OasthData object. This object contains multiple maps matching each entity's internal ID 
to the respective object. The masterLines map contains all the necessary information, as each masterline contains lines, each line 
contains schedules and routes and each route contains stops. If access to individual entites is needed, for example when saving the data 
to a database, the individual Maps should be preferred, as they might contain extra information that is not associated with any line or route, like 
unused stops.