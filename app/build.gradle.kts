import java.io.FileInputStream
import java.util.*

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("realm-android")
}

android {
    compileSdk = 30
    buildToolsVersion = "30.0.3"

    val buildPropsFile = file("build.properties")

    val buildProps = Properties()

    if(buildPropsFile.exists())
        buildProps.load(FileInputStream(buildPropsFile))

    val etherScanApiKey = buildProps.getProperty("ETHERSCAN_API_KEY", "\"\"")
    val cmcApiKey = buildProps.getProperty("COINMARKETCAP_API_KEY", "\"\"")

    defaultConfig {
        applicationId = "misha.miner"
        minSdk = 21
        targetSdk = 30
        versionCode = 1
        versionName = "1.0"
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
        kotlinCompilerExtensionVersion = rootProject.extra["compose_version"] as String
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.6.0")
    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.compose.ui:ui:${rootProject.extra["compose_version"]}")
    implementation("androidx.compose.material:material:${rootProject.extra["compose_version"]}")
    implementation("androidx.compose.ui:ui-tooling:${rootProject.extra["compose_version"]}")
    implementation("androidx.compose.runtime:runtime:${rootProject.extra["compose_version"]}")
    implementation("androidx.compose.runtime:runtime-livedata:${rootProject.extra["compose_version"]}")
    implementation("androidx.compose.material:material-icons-extended:${rootProject.extra["compose_version"]}")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.3.1")
    implementation("androidx.activity:activity-compose:1.3.0")
    implementation("androidx.navigation:navigation-compose:2.4.0-alpha05")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.0-beta02")
    implementation("com.jcraft:jsch:0.1.55")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:${rootProject.extra["compose_version"]}")

    //Hilt
    kapt("com.google.dagger:hilt-compiler:${rootProject.extra["hilt_version"]}")
    implementation("com.google.dagger:hilt-android:${rootProject.extra["hilt_version"]}")

    //Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.1")

    //Chuck HTTP interceptor
    debugImplementation("com.github.chuckerteam.chucker:library:3.5.0")
    releaseImplementation("com.github.chuckerteam.chucker:library-no-op:3.5.0")
}