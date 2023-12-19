import java.util.*

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.rangr"
    compileSdk = 34


    defaultConfig {
        applicationId = "com.rangr"
        minSdk = 27
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        val appProperties = Properties()
        val appPropertiesFile = rootProject.file("app.properties")
        if (appPropertiesFile.exists()) {
            appProperties.load(appPropertiesFile.inputStream())
        }

        android.buildFeatures.buildConfig = true
        buildConfigField("String", "LINZ_API_KEY", "\"${appProperties["LINZ_API_KEY"]}\"")
        buildConfigField("String", "MAPBOX_API_KEY", "\"${appProperties["MAPBOX_API_KEY"]}\"")

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
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.1")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("com.google.android.material:material:1.11.0")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    testImplementation("junit:junit:4.13.2")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    implementation("io.ktor:ktor-client-core:2.3.7")
    implementation("io.ktor:ktor-client-cio:2.3.7")

    implementation("com.mapbox.maps:android:11.0.0")
    implementation("com.mapbox.mapboxsdk:mapbox-sdk-turf:6.15.0")
    implementation("com.mapbox.extension:maps-compose:11.0.0")

}