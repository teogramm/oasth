import org.jetbrains.dokka.gradle.DokkaTask
import java.time.Instant
import java.net.URI

plugins {
    kotlin("jvm") version "1.4.30"
    id("org.jetbrains.dokka") version "1.4.20"
    `maven-publish`
    signing
}

group = "xyz.teogramm"
version = "0.9.0"

repositories {
    mavenCentral()
    jcenter()
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
    val sourcesJar by registering(Jar::class) {
        archiveClassifier.set("sources")
        from(sourceSets.main.get().allSource)
    }
    val dokkaJavadoc by getting(DokkaTask::class)
    val javadocJar by registering(Jar::class) {
        archiveClassifier.set("javadoc")
        from("$buildDir/dokka/javadoc")
        dependsOn(dokkaJavadoc.path)
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
                url = URI("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
                credentials {
                    username = System.getenv("SONATYPE_USERNAME")
                    password = System.getenv("SONATYPE_PASSWORD")
                }
            }
        }
    }

    signing {
        setRequired {
           (gradle.taskGraph.hasTask("publish") || gradle.taskGraph.hasTask("publishPlugins"))
        }
        // Signing key and passphrase set in environment variables
        val signingKey: String? = System.getenv("GPG_SECRET")
        val signingPassword: String? = System.getenv("GPG_PASSPHRASE")
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(publishing.publications)
    }
}

sourceSets {
    create("debug") {
        compileClasspath += sourceSets.main.get().runtimeClasspath
        runtimeClasspath += sourceSets.main.get().runtimeClasspath
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
}
