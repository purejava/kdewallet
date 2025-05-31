import net.thebugmc.gradle.sonatypepublisher.PublishingType.*

plugins {
    id("java-library")
    id("net.thebugmc.gradle.sonatype-central-portal-publisher") version "1.2.4"
    id("maven-publish")
    id("signing")
}

repositories {
    mavenCentral()
}

dependencies {
    api(libs.com.github.hypfvieh.dbus.java.core)
    api(libs.com.github.hypfvieh.dbus.java.transport.native.unixsocket)
    api(libs.org.slf4j.slf4j.api)
    testImplementation(libs.org.junit.jupiter.junit.jupiter.api)
    testImplementation(libs.org.junit.jupiter.junit.jupiter.engine)
    testImplementation(libs.org.junit.jupiter.junit.jupiter)
    testImplementation(libs.org.slf4j.slf4j.simple)
    testRuntimeOnly(libs.org.junit.platform.junit.platform.launcher)
}

group = "org.purejava"
version = "1.6.1-SNAPSHOT"
description = "A Java library for storing secrets on linux in a KDE wallet over D-Bus, implements kwallet."

val sonatypeUsername: String = System.getenv("SONATYPE_USERNAME") ?: ""
val sonatypePassword: String = System.getenv("SONATYPE_PASSWORD") ?: ""

java {
    java.sourceCompatibility = JavaVersion.VERSION_19
    withSourcesJar()
    withJavadocJar()
}

tasks.test {
    useJUnitPlatform()
    filter {
        includeTestsMatching("KDEWalletTest")
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            pom {
                name.set("kdewallet")
                description.set("A Java library for storing secrets on linux in a KDE wallet over D-Bus, implements kwallet.")
                url.set("https://github.com/purejava/kdewallet")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

                developers {
                    developer {
                        id.set("purejava")
                        name.set("Ralph Plawetzki")
                        email.set("ralph@purejava.org")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/purejava/kdewallet.git")
                    developerConnection.set("scm:git:ssh://github.com/purejava/kdewallet.git")
                    url.set("https://github.com/purejava/kdewallet/tree/main")
                }

                issueManagement {
                    system.set("GitHub Issues")
                    url.set("https://github.com/purejava/kdewallet/issues")
                }
            }
        }
    }
}

centralPortal {
    publishingType.set(USER_MANAGED)

    username.set(sonatypeUsername)
    password.set(sonatypePassword)

    // Configure POM metadata
    pom {
        name.set("kdewallet")
        description.set("A Java library for storing secrets on linux in a KDE wallet over D-Bus, implements kwallet.")
        url.set("https://github.com/purejava/kdewallet")
        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/licenses/MIT")
            }
        }
        developers {
            developer {
                id.set("purejava")
                name.set("Ralph Plawetzki")
                email.set("ralph@purejava.org")
            }
        }
        scm {
            connection.set("scm:git:git://github.com/purejava/kdewallet.git")
            developerConnection.set("scm:git:ssh://github.com/purejava/kdewallet.git")
            url.set("https://github.com/purejava/kdewallet/tree/main")
        }
        issueManagement {
            system.set("GitHub Issues")
            url.set("https://github.com/kdewallet/issues")
        }
    }
}

if (!version.toString().endsWith("-SNAPSHOT")) {
    signing {
        useGpgCmd()
        sign(configurations.runtimeElements.get())
        sign(publishing.publications["mavenJava"])
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    isFailOnError = false
    if (JavaVersion.current().isJava9Compatible) {
        (options as? StandardJavadocDocletOptions)?.addBooleanOption("html5", true)
    }
    (options as? StandardJavadocDocletOptions)?.encoding = "UTF-8"
}
