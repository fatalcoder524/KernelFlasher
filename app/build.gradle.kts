import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.devtools.ksp)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.compose.compiler)
}

val keystoreProperties = Properties().apply {
    val file = rootProject.file("key.properties")
    if (file.exists()) {
        file.inputStream().use { load(it) }
    }
}

android {
    compileSdk = 36
    namespace = "com.github.capntrips.kernelflasher"

    defaultConfig {
        applicationId = "com.github.capntrips.kernelflasher"
        minSdk = 29
        targetSdk = 36
        versionCode = 10600
        versionName = "1.6.0"

        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "room.schemaLocation" to "$projectDir/schemas",
                    "room.incremental" to "true",
                )
            }
        }

        ndk {
            //noinspection ChromeOsAbiSupport
            abiFilters.add("arm64-v8a")
        }

        vectorDrawables {
            useSupportLibrary = true
        }
        }

        signingConfigs {
            if (keystoreProperties.isNotEmpty()) {
                create("release") {
                    storeFile = rootProject.file(keystoreProperties.getProperty("storeFile"))
                    storePassword = keystoreProperties.getProperty("storePassword")
                    keyAlias = keystoreProperties.getProperty("keyAlias")
                    keyPassword = keystoreProperties.getProperty("keyPassword")
                }
            }
        }

        buildTypes {
            release {
                // R8 minify + resource shrinking are ON to keep the APK small (mainly to
                // tree-shake material-icons-extended). Keep rules in proguard-rules.pro cover
                // AIDL/serialization/libsu/retrofit/gson, and -dontobfuscate protects Room and
                // reflection-by-name. Verify core flows on-device after changing keep rules.
                isMinifyEnabled = true
                isShrinkResources = true
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
                )
                if (keystoreProperties.isNotEmpty()) {
                    signingConfig = signingConfigs.getByName("release")
                }
            }
        }

        sourceSets {
            getByName("main") {
                jniLibs.srcDirs("src/main/jniLibs")
            }
        }

        buildFeatures {
            buildConfig = true
            aidl = true
            compose = true
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_21
            targetCompatibility = JavaVersion.VERSION_21
        }

        kotlin {
            jvmToolchain(21)

        }

        packaging {
            resources {
                excludes += setOf("/META-INF/{AL2.0,LGPL2.1}")
            }
            jniLibs {
                useLegacyPackaging = true
            }
            dex {
                useLegacyPackaging = true
            }
        }

        androidResources {
            generateLocaleConfig = true
        }

        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
            arg("room.incremental", "true")
        }
}

    dependencies {
        implementation(libs.androidx.activity.compose)
        implementation(libs.androidx.appcompat)
        implementation(libs.androidx.compose.material)
        implementation(libs.androidx.compose.material3)
        implementation(libs.androidx.compose.material.icons.extended)
        implementation(libs.androidx.compose.foundation)
        implementation(libs.androidx.compose.ui)
        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.core.splashscreen)
        implementation(libs.androidx.lifecycle.runtime.ktx)
        implementation(libs.androidx.lifecycle.viewmodel.compose)
        implementation(libs.androidx.navigation.compose)
        implementation(libs.androidx.room.runtime)
        annotationProcessor(libs.androidx.room.compiler)
        ksp(libs.androidx.room.compiler)
        implementation(libs.libsu.core)
        implementation(libs.libsu.io)
        implementation(libs.libsu.nio)
        implementation(libs.libsu.service)
        implementation(libs.material)
        implementation(libs.okhttp)
        implementation(libs.kotlinx.serialization.json)
        implementation(libs.kotlinx.serialization.json)
        implementation(libs.retrofit)
        implementation(libs.converter.gson)
    }