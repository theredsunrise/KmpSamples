import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    id("io.github.ttypic.swiftklib") version "0.6.4"
}

kotlin {
    jvmToolchain(21)

    androidTarget {
        dependencies {
            val composeBom = platform(libs.androidx.compose.bom)
            implementation(composeBom)
            releaseCompileOnly(compose.uiTooling)
            releaseCompileOnly(compose.preview)
            debugImplementation(compose.uiTooling)
            debugImplementation(compose.preview)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.compilations {
            val main by getting {
                cinterops {
                    create("UILayerView")
                    create("GalleryPickerHelper")
                    create("WrapperVC")
                }
            }
        }

        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        iosMain.dependencies {
            implementation(libs.ktor.engine.darwin)
            implementation(libs.material3.windowSizeClass)
        }
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.compose.adaptive)
            implementation(libs.ktor.engine.cio)
            implementation(libs.koin.android)
            implementation(libs.material3.windowSizeClass)
            implementation(libs.androidx.exoplayer)
            implementation(libs.androidx.exoplayer.compose)
            implementation(libs.androidx.media3.ui)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.androidx.navigation.compose)
            implementation(libs.kotlinx.datetime)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.viewmodel.compose)
            implementation(libs.ktor.client)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.serializer.json)
            implementation(libs.room.runtime)
            implementation(libs.room.bundled)
            implementation(libs.material3.windowSizeClass)
        }
    }
}

swiftklib {
    create("UILayerView") {
        path = file("native/UILayerView")
        packageName("com.example.kmpsamples.ui.layerview")
    }
    create("GalleryPickerHelper") {
        path = file("native/GalleryPickerHelper")
        packageName("com.example.kmpsamples.ui.galleryimagepicker")
    }
    create("WrapperVC") {
        minIos = 14
        path = file("native/WrapperVC")
        packageName("com.example.kmpsamples.ui.wrappervc")
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

android {
    namespace = "org.example.kmpsamples"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "org.example.kmpsamples"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    signingConfigs {
        create("release") {
            val keystoreProperties = Properties().apply {
                load(FileInputStream(rootProject.file("keystore.properties")))
            }
            storeFile = file(keystoreProperties["storeFile"] as String)
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storePassword = keystoreProperties["storePassword"] as String
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt")
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
}

dependencies {
    setOf(
        "kspAndroid", "kspIosX64", "kspIosArm64", "kspIosSimulatorArm64"
    ).forEach {
        add(it, libs.room.compiler)
    }
}

