// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    val compose_version by extra("1.0.3")
    val hilt_version by extra ("2.39.1")
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.30")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle.kts files

        classpath("com.google.dagger:hilt-android-gradle-plugin:$hilt_version")
        classpath("io.realm:realm-gradle-plugin:10.8.0")
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