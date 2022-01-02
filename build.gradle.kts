import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

buildscript {
    val hilt_version by extra ("2.40.5")
    val gradle_version by extra ("7.0.4")
    val realm_version by extra ("10.9.0")
    val gradle_versions_plugin_version by extra ("0.40.0")
    val kotlin_version by extra ("1.6.0")

    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:$gradle_version")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle.kts files

        classpath("com.google.dagger:hilt-android-gradle-plugin:$hilt_version")
        classpath("io.realm:realm-gradle-plugin:$realm_version")
        classpath("com.github.ben-manes:gradle-versions-plugin:$gradle_versions_plugin_version")
    }
}

apply(plugin = "com.github.ben-manes.versions")

fun String.isNonStable(): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { toUpperCase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(this)
    return isStable.not()
}

tasks.withType<DependencyUpdatesTask> {

    // Example 2: disallow release candidates as upgradable versions from stable versions
    rejectVersionIf {
        candidate.version.isNonStable() && !currentVersion.isNonStable()
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}