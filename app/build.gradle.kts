import java.io.FileInputStream
import java.util.*

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("realm-android")
    kotlin("plugin.serialization")
    alias(libs.plugins.kotlinter)
}

android {
    compileSdk = libs.versions.sdk.get().toInt()

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
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.get()
    }
}

dependencies {

    implementation(libs.androidx.core)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.compose.ui)
    implementation(libs.compose.material)
    implementation(libs.compose.uiTooling)
    implementation(libs.compose.runtime)
    implementation(libs.compose.runtimeLivedata)
    implementation(libs.compose.materialIconsExtended)
    implementation(libs.androidx.lifecycle)
    implementation(libs.activity.compose)
    implementation(libs.navigation.compose)
    implementation(libs.constraintlayout.compose)
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
    debugImplementation(libs.pluto)
    releaseImplementation(libs.plutoNoOp)
    debugImplementation(libs.plutoExceptions)
    releaseImplementation(libs.plutoExceptionsNoOp)
    debugImplementation(libs.plutoLogger)
    releaseImplementation(libs.plutoLoggerNoOp)
    debugImplementation(libs.plutoNetwork)
    releaseImplementation(libs.plutoNetworkNoOp)
    debugImplementation(libs.plutoPreferences)
    releaseImplementation(libs.plutoPreferencesNoOp)

    implementation(libs.kotlinxSerializationJson)
}