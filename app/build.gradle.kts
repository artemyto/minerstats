import java.io.FileInputStream
import java.util.*

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlinter)
    id("kotlin-kapt")
    id("realm-android")
}

android {
    compileSdk = libs.versions.sdk.get().toInt()

    val javaVersion = JavaVersion.VERSION_1_8

    val buildPropsFile = file("build.properties")

    val buildProps = Properties()

    if(buildPropsFile.exists())
        buildProps.load(FileInputStream(buildPropsFile))

    val etherScanApiKey = buildProps.getProperty("ETHERSCAN_API_KEY", "\"\"")
    val cmcApiKey = buildProps.getProperty("COINMARKETCAP_API_KEY", "\"\"")

    defaultConfig {
        applicationId = "misha.miner"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.sdk.get().toInt()
        versionCode = libs.versions.appVersionCode.get().toInt()
        versionName = libs.versions.appVersion.get()
        buildConfigField("String", "ETHERSCAN_API_KEY", etherScanApiKey)
        buildConfigField("String", "COINMARKETCAP_API_KEY", cmcApiKey)

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        create("preRelease") {
            initWith(getByName("debug"))
            isDebuggable = false
        }
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }
    kotlinOptions {
        jvmTarget = javaVersion.toString()
        freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlin.RequiresOptIn"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
}

dependencies {

    implementation(libs.androidx.core)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.bundles.compose)
    implementation(libs.androidx.lifecycle)
    implementation(libs.androidx.browser)
    implementation(libs.activity.compose)
    implementation(libs.navigation.compose)
    implementation(libs.hilt.navigaiton.compose)
    implementation(libs.accompanist.swiperefresh)
    implementation(libs.jsch)

    coreLibraryDesugaring(libs.desugaring)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.espresso)
    androidTestImplementation(libs.compose.junit)

    //Hilt
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.android)

    //Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofitSerialization)
    implementation(libs.okhttp)
    implementation(libs.okhttpLoggingInterceptor)

    //Pluto
    debugImplementation(libs.bundles.pluto)
    "preReleaseImplementation"(libs.bundles.plutoNoOp)
    releaseImplementation(libs.bundles.plutoNoOp)

    implementation(libs.kotlinxSerializationJson)
}