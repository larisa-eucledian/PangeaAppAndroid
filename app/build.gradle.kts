import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.navigation.safeargs)
    id("com.google.gms.google-services")
}

val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        load(localPropertiesFile.inputStream())
    }
}

android {
    namespace = "com.example.pangeaapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.pangeaapp"
        minSdk = 25
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "STRIPE_PUBLISHABLE_KEY",
            "\"${localProperties.getProperty("STRIPE_PUBLISHABLE_KEY", "")}\""
        )
        buildConfigField(
            "String",
            "TENANT_API_KEY",
            "\"${localProperties.getProperty("TENANT_API_KEY", "")}\""
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        viewBinding = true
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}
ksp {
    arg("dagger.hilt.android.internal.disableAndroidSuperclassValidation", "true")
}

dependencies {
    // AndroidX Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Material Design
    implementation(libs.material)

    // Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)
    implementation(libs.activity.compose)

    // Navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // SwipeRefreshLayout
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Networking
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging.interceptor)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // Security
    implementation(libs.androidx.security.crypto)
    implementation(libs.tink.android)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Image Loading
    implementation(libs.coil)

    // Stripe Payment
    implementation("com.stripe:stripe-android:20.49.0")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.4"))
    implementation("com.google.firebase:firebase-analytics-ktx")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}