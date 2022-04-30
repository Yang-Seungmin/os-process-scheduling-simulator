import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    id("org.jetbrains.compose") version "1.1.1"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.6.10"
}

group = "be.ysmstudio"
version = "1.0"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    implementation("io.insert-koin:koin-core:3.1.6")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "15"
}

compose.desktop {
    application {
        javaHome = System.getenv("JDK_17")
    }
    application {
        mainClass = "SchedulingSimulator"
        nativeDistributions {
            modules("java.instrument", "jdk.unsupported")
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "OS_Process_Scheduling_Simulator"
            packageVersion = "1.0.0"
        }
    }
}
val compileKotlin: KotlinCompile by tasks