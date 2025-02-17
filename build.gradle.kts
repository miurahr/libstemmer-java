plugins {
    java
    signing
    `maven-publish`
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0"
}

version="2.2.0"

tasks.wrapper {
    distributionType = Wrapper.DistributionType.BIN
    gradleVersion = "8.10.2"
}

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = "tokyo.northside"
            artifactId = "libstemmer"
            pom {
                name.set("example")
                description.set("Example Library")
                url.set("https://github.com/miurahr/libstemmer-java")
                licenses {
                    license {
                        name.set("The GNU General Public License, Version 3")
                        url.set("https://www.gnu.org/licenses/licenses/gpl-3.html")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        id.set("miurahr")
                        name.set("Hiroshi Miura")
                        email.set("miurahr@linux.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/miurahr/libstemmer-java.git")
                    developerConnection.set("scm:git:git://github.com/miurahr/libstemmer-java.git")
                    url.set("https://github.com/miurahr/libstemmer-java")
                }
            }
        }
    }
}

val signKey = listOf("signingKey", "signing.keyId", "signing.gnupg.keyName").find { project.hasProperty(it) }
tasks.withType<Sign> {
    onlyIf { signKey != null && !rootProject.version.toString().endsWith("-SNAPSHOT") }
}

signing {
    when (signKey) {
        "signingKey" -> {
            val signingKey: String? by project
            val signingPassword: String? by project
            useInMemoryPgpKeys(signingKey, signingPassword)
        }
        "signing.keyId" -> { /* do nothing */
        }
        "signing.gnupg.keyName" -> {
            useGpgCmd()
        }
    }
    sign(publishing.publications["mavenJava"])
}

tasks.withType<Javadoc>() {
    setFailOnError(false)
    options {
        jFlags("-Duser.language=en")
    }
}

val sonatypeUsername: String? by project
val sonatypePassword: String? by project

nexusPublishing.repositories {
    sonatype {
        stagingProfileId = "121f28671d24dc"
        if (sonatypeUsername != null && sonatypePassword != null) {
            username.set(sonatypeUsername)
            password.set(sonatypePassword)
        }
    }
}

tasks.withType<Copy> {
    duplicatesStrategy = DuplicatesStrategy.WARN
}

tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
