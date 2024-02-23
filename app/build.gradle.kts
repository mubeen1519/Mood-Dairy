plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.appdev.moodapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.appdev.moodapp"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.biometric:biometric:1.1.0")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("io.github.farshidroohi:lineGraph:1.0.2")
    implementation("com.google.android.material:material:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-auth:22.3.1")
    implementation("com.google.firebase:firebase-database:20.3.0")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.github.fornewid:neumorphism:0.3.2")
    implementation("com.intuit.ssp:ssp-android:1.1.0")
    implementation("com.intuit.sdp:sdp-android:1.1.0")
    implementation("com.kizitonwose.calendar:view:2.4.1")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.github.ozcanalasalvar.picker:datepicker:2.0.7")
    implementation("com.github.ozcanalasalvar.picker:wheelview:2.0.7")
    implementation("androidx.compose.material3:material3:1.2.0-alpha02")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
    implementation("com.github.lecho:hellocharts-library:1.5.8@aar")
    implementation("androidx.camera:camera-core:1.4.0-alpha01")
    implementation("androidx.camera:camera-lifecycle:1.4.0-alpha01")
    implementation("androidx.camera:camera-video:1.4.0-alpha01")
    implementation("androidx.camera:camera-view:1.4.0-alpha01")
    implementation("androidx.camera:camera-view:1.4.0-alpha01")
    implementation("androidx.camera:camera-extensions:1.4.0-alpha01")
    implementation ("com.ramotion.paperonboarding:paper-onboarding:1.1.3")
}