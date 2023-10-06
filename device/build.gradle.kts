/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Java application project to get you started.
 * For more details on building Java & JVM projects, please refer to https://docs.gradle.org/8.3/userguide/building_java_projects.html in the Gradle documentation.
 */

/*
configurations.runtimeClasspath{
    resolutionStrategy.force("io.netty:netty-common:4.1.93.Final")
}

 */


plugins {
    // Apply the application plugin to add support for building a CLI application in Java.
    application

}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
    mavenLocal()
}

dependencies {

//    constraints {
        implementation("io.netty:netty-common:4.1.93.Final") // {
 //           because("virtual-device has transitive dependency on 4.1.48.Final which is unusable here.")
 //       }
 //   }

    // influxdb2-client
    // implementation("com.influxdb:influxdb-client-java:6.7.0")
    // influxdb3-client
    implementation("com.influxdb:influxdb3-java:0.1.0")

    // Use JUnit Jupiter for testing.
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.3")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // This dependency is used by the application.
    implementation("com.google.guava:guava:32.1.1-jre")

    implementation("io.bonitoo.qa:virtual-device:0.1-SNAPSHOT"){
        // N.B. this version of io.netty is needed by HiveMQ, which
        // writes to an MQTT Broker.  For this project, which seeks to
        // write directly to Influx, the MQTT Broker is not needed.
        // However, HiveMQ scopes this version into the project at runtime
        // and replaces the later version (4.1.93.Final), which is the one
        // needed by influx3 client.  The earlier version is missing some used methods.
        exclude("io.netty", "netty-buffer")
        exclude("io.netty", "netty-codec")
        exclude("io.netty", "netty-common")
        exclude("io.netty", "netty-handler")
        exclude("io.netty", "netty-transport")
        exclude("io.netty", "netty-codec-http")
        exclude("io.netty", "netty-handler-proxy")
        exclude("io.netty", "netty-transport-native-epoll")
    }

    compileOnly("org.projectlombok:lombok:1.18.28")
    annotationProcessor("org.projectlombok:lombok:1.18.28")

    testCompileOnly("org.projectlombok:lombok:1.18.28")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.28")


}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

application {
    // Define the main class for the application.
    mainClass.set("io.bonitoo.virtual.device.influx.IFluxDevice")
    applicationDefaultJvmArgs = listOf("--add-opens",
            "java.base/java.nio=ALL-UNNAMED",
            "-Dmessage=\"ahoj z konfigu\"",
            "-Ddefault.ttl=41000")
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
    jvmArgs("--add-opens","java.base/java.nio=ALL-UNNAMED")
}

task("bar"){
    println("BAR BAR!")
}


