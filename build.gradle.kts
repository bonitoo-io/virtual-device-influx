import com.adarshr.gradle.testlogger.TestLoggerExtension
import com.adarshr.gradle.testlogger.theme.ThemeType

plugins {
    id("com.adarshr.test-logger") version "3.2.0"
    id("java")
}

subprojects {

    apply {
        plugin("com.adarshr.test-logger")
    }

    configure<TestLoggerExtension> {
        theme = ThemeType.MOCHA
        showExceptions = true
        showStackTraces = true
        showFullStackTraces = false
        showCauses = true
        slowThreshold = 2000
        showSummary = true
        showSimpleNames = false
        showPassed = true
        showSkipped = true
        showFailed = true
        showOnlySlow = false
        showStandardStreams = false
        showPassedStandardStreams = true
        showSkippedStandardStreams = true
        showFailedStandardStreams = true
        logLevel = LogLevel.LIFECYCLE
    }
}

task("Wumpus"){
    println("Hunt the wumpus!")
}
/*
configurations.all {

    resolutionStrategy {
        failOnVersionConflict()
        // prefer modules that are part of this build (multi-project or composite build) over external modules
        preferProjectModules()
        println("DEBUG inside resolutionStrategy")

        force("io.netty:netty-common:4.1.93.Final")

        eachDependency(){
            if( requested.group == "io.netty" && requested.version == "4.1.48.Final"){
                println("DEBUG calling replacement")
                useVersion("4.1.93.Final")
                because("4.1.48 does not contain some methods used by other APIs")
            }
        }
    }
}
*/