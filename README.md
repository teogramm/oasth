oasth
========

This is a library for accessing the OASTH API. 

Example usage:

    import org.teogramm.oasth

    val oasth = Oasth()
    val data = oasth.fetch()

Features
--------

- Get static data about the network. Routes, lines, stops and schedules.
- Get live data about bus locations and arrival times.

Usage
------------

The library is available in Maven Central with artifact ID: ```xyz.teogramm:oasth```

Gradle Kotlin DSL Example:
#### **build.gradle.kts**
```kotlin
repositories {
    mavenCentral()
}
dependencies {
    implementation("xyz.teogramm:oasth")
}
```

Documentation
----------

Documentation is available on [ReadTheDocs](https://oasth.readthedocs.io/).

Contribute
----------
Bug reports and pull requests are welcome!

- Issue Tracker: github.com/teogramm/oasth/issues

License
-------

The project is licensed under the GPLv3 license.
