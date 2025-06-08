import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

val localProperties = Properties()
localProperties.load(FileInputStream(rootProject.file("local.properties")))

val SENDER_EMAIL by extra(localProperties.getProperty("SENDER_EMAIL"))
val GMAIL_PASSWORD by extra(localProperties.getProperty("GMAIL_PASSWORD"))
val RECIPIENT_EMAIL by extra(localProperties.getProperty("RECIPIENT_EMAIL"))

android {
    namespace = "com.project"
    compileSdk = 35

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.project"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "SENDER_EMAIL", "\"$SENDER_EMAIL\"")
        buildConfigField("String", "GMAIL_PASSWORD", "\"$GMAIL_PASSWORD\"")
        buildConfigField("String", "RECIPIENT_EMAIL", "\"$RECIPIENT_EMAIL\"")
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(fileTree(mapOf(
        "dir" to "libs",
        "include" to listOf("*.aar", "*.jar")
    )))
    implementation(libs.kotlinx.coroutines.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}