import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// Set to true to import Z3 native libraries for Day 24 of 2023 (or any others that may need it)
val importZ3NativeLibs = false

plugins {
    kotlin("jvm") version "2.2.21"
    id("org.jlleitschuh.gradle.ktlint") version "14.0.1"
    kotlin("plugin.serialization") version "2.2.21"
    application
}

repositories {
    mavenCentral()
    // 2023 Day 24. I cheated here...
    if (importZ3NativeLibs) {
        maven {
            url = uri("https://artifacts.itemis.cloud/repository/maven-mps/")
        }
        maven {
            url = uri("https://projects.itemis.de/nexus/content/repositories/OS/")
        }
    }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:6.0.1"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    // 2023 Day 24. I cheated here...
    if (importZ3NativeLibs) {
        testImplementation("com.microsoft.z3:java-jar:4.11.2")
        testRuntimeOnly("com.microsoft.z3:libz3.java.linux:4.11.2@zip")
        fileTree("$buildDir/nativeLibs")
    }
    implementation(kotlin("stdlib-jdk8"))
}

tasks {
    register<Exec>("createDay") {
        group = "advent of code"
        description = "Create the current day's directory and files from the template"
        commandLine("sh", "-c", "${project.rootDir}/scripts/create-day.sh")
    }

    register<Exec>("createReadme") {
        group = "advent of code"
        description = "Create the current day's README from the AoC html page"
        commandLine("sh", "-c", "${project.rootDir}/scripts/generate-readme.sh")
    }

    if (importZ3NativeLibs) {
        register<Copy>("extractZ3NativeLib") {
            val zipFile =
                configurations.testRuntimeClasspath
                    .get()
                    .files
                    .first { it.name.contains("libz3\\.java\\.linux-.*\\.zip".toRegex()) }
            val nativeLibsDir = file("$buildDir/nativeLibs")

            from(zipTree(zipFile))
            into(nativeLibsDir)

            doLast {
                println("Extracted Z3 ${zipFile.name} native library to $nativeLibsDir")
            }
        }
    }

    register<Test>("testDay") {
        if (importZ3NativeLibs) {
            dependsOn("extractZ3NativeLib")
            val javaLibraryPath = System.getProperty("java.library.path")
            systemProperty("java.library.path", "$buildDir/nativeLibs${File.separatorChar}$javaLibraryPath")
        }

        group = "advent of code"
        description = "Run the current day's tests"
        useJUnitPlatform()
        val day = LocalDate.now().format(DateTimeFormatter.ofPattern("dd"))
        filter {
            includeTestsMatching("day$day.*")
        }
        testLogging {
            info.events =
                mutableSetOf(
                    TestLogEvent.PASSED,
                    TestLogEvent.FAILED,
                    TestLogEvent.STANDARD_OUT,
                    TestLogEvent.STANDARD_ERROR,
                )
            events = info.events
        }
    }

    test {
        if (importZ3NativeLibs) {
            dependsOn("extractZ3NativeLib")
            val javaLibraryPath = System.getProperty("java.library.path")
            systemProperty("java.library.path", "$buildDir/nativeLibs${File.pathSeparatorChar}$javaLibraryPath")
        }

        useJUnitPlatform()
        testLogging.events =
            mutableSetOf(
                TestLogEvent.PASSED,
                TestLogEvent.FAILED,
                TestLogEvent.STANDARD_OUT,
                TestLogEvent.STANDARD_ERROR,
            )
        filter {
            // Don't run tests on the "template" classes
            excludeTestsMatching("dayNN.*")
            // Don't run tests on real input during CI build, since those will fail (personal input data for problems is not checked in to github)
            if (System.getenv("CI") == "true") {
                excludeTestsMatching("*Real Input*")
            }
        }
    }

    withType<KotlinCompile> {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
        java {
            sourceCompatibility = JavaVersion.VERSION_21
            targetCompatibility = JavaVersion.VERSION_21
        }
    }

    sourceSets["test"].resources {
        srcDirs("src/test/kotlin")
        exclude("**/*.kt")
    }
}
