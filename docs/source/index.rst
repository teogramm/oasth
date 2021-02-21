oasth: Transportation data for Thessaloniki
===========================================

Welcome to the documentation for the oasth library. This library enables access to static and live 
data of the bus network in the area of Thessaloniki.

The plugin is published on the Maven Central repository with artifact ID ``xyz.teogramm:oasth``

Example for Gradle Kotlin DSL:

**build.gradle.kts**

.. code-block:: kotlin

   repositories {
      mavenCentral()
   }
   dependencies {
      implementation("xyz.teogramm:oasth")
   }

Issues
------

Please report any issues and bugs you encounter on: https://github.com/teogramm/oasth/issues

.. toctree::
   :maxdepth: 2
   :caption: Contents:
   
   static-data/index
   live-data
