import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.gradleVersionPlugin)
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}

buildscript {

    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath(libs.realm)
    }
}

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

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}

val prepareDirs by tasks.register<Task>("prepareDirs") {
    mkdir("${rootProject.rootDir}/.git/hooks")
}

val installGitHook by tasks.register<Copy>("installGitHook") {
    from(File(rootProject.rootDir, "pre-push"))
    into { File(rootProject.rootDir, ".git/hooks") }
    fileMode = 0b111101101
}

tasks.getByPath(":app:preBuild")
    .dependsOn(prepareDirs)
    .dependsOn(installGitHook)