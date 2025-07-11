import java.time.Instant
import java.net.URI
import java.util.Properties

plugins {
    kotlin("jvm") version "2.1.10"
    id("org.jetbrains.dokka") version "2.0.0"
    `maven-publish`
    signing
}

// Load properties from local.properties
val propertiesFile = project.rootProject.file("local.properties")
if(propertiesFile.exists()) {
    val p = Properties()
    propertiesFile.inputStream().use {
        p.load(it)
    }
    p.forEach { name, value ->
        extra[name.toString()] = value.toString()
    }
}

group = "xyz.teogramm"
version = "0.9.1"

repositories {
    mavenCentral()
}

val jarComponent: SoftwareComponent = components["kotlin"]

tasks {
    jar {
        manifest {
            attributes["Built-By"] = "Theodoros Grammenos"
            attributes["Build-Jdk"] =
                "${System.getProperty("java.version")} (${System.getProperty("java.vendor")} ${System.getProperty("java.vm.version")})"
            attributes["Build-Timestamp"] = Instant.now().toString()
            attributes["Created-By"] = "Gradle ${gradle.gradleVersion}"
            attributes["Implementation-Title"] = project.name
            attributes["Implementation-Version"] = project.version
        }
    }


    val sourcesJar = register<Jar>("sourcesJar") {
        archiveClassifier.set("sources")
        from(sourceSets.main.get().allSource)
    }

    val javadocJar = register<Jar>("javadocJar") {
        dependsOn(dokkaGeneratePublicationHtml)
        from(dokkaGeneratePublicationHtml.flatMap { it.outputDirectory })
        archiveClassifier.set("javadoc")
    }

    publishing {
        publications {
            create<MavenPublication>("release") {
                from(jarComponent)
                artifact(sourcesJar)
                artifact(javadocJar)
                pom {
                    name.set("Oasth")
                    description.set("A library to access the OASTH API.")
                    url.set("https://github.com/teogramm/oasth")
                    licenses {
                        license {
                            name.set("GNU General Public License v3")
                            url.set("https://github.com/teogramm/oasth/blob/master/LICENSE")
                        }
                    }
                    developers {
                        developer {
                            id.set("teorgamm")
                            name.set("Theodoros Grammenos")
                            email.set("teogramm@outlook.com")
                        }
                    }
                    scm {
                        connection.set("scm:git:git://github.com/teogramm/oasth.git")
                        developerConnection.set("scm:git:git://github.com/teogramm/oasth.git")
                        url.set("https://github.com/teogramm/oasth")
                    }
                }
            }
        }

        // The repository to publish to, Sonatype/MavenCentral
        repositories {
            maven {
                name = "mavenCentral"
                url = URI("https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2/")
                credentials {
                    username = System.getenv("SONATYPE_USERNAME")
                    password = System.getenv("SONATYPE_PASSWORD")
                }
            }
        }
    }
}

signing {
    setRequired({
        gradle.taskGraph.hasTask("publishReleasePublicationToMavenCentralRepository")
    })
    // Workaround Gradle issue https://github.com/gradle/gradle/issues/5064
    // To enable signing set the "signing.gnupg.keyName" property
    if(project.hasProperty("signing.gnupg.keyName")) {
        useGpgCmd()
    }
    sign(publishing.publications)
}

sourceSets {
    // Add main sources to the debug source set
    create("debug") {
        compileClasspath += sourceSets.main.get().runtimeClasspath
        runtimeClasspath += sourceSets.main.get().runtimeClasspath
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
}
