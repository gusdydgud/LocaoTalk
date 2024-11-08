import com.android.build.api.dsl.Packaging
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services") // Firebase 설정을 위한 플러그인 추가
}
android {
    namespace = "com.example.locaotalk"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.locaotalk"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        // local.properties 파일에서 NAVER_MAP_CLIENT_ID와 FIREBASE_DATABASE_URL 가져오기
        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localProperties.load(localPropertiesFile.inputStream())
        }

        // 네이버 지도 클라이언트 ID와 Firebase Database URL을 BuildConfig와 Manifest로 설정
        val naverMapClientId = localProperties.getProperty("NAVER_MAP_CLIENT_ID") ?: ""
        manifestPlaceholders["NAVER_MAP_CLIENT_ID"] = naverMapClientId

        val firebaseDatabaseUrl = localProperties.getProperty("FIREBASE_DATABASE_URL") ?: ""
        buildConfigField("String", "FIREBASE_DATABASE_URL", "\"$firebaseDatabaseUrl\"")

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true // buildConfig 활성화 설정 추가
    }

    packaging {
        resources.excludes += listOf(
            "/META-INF/AL2.0",
            "/META-INF/LGPL2.1",
            "META-INF/androidx.localbroadcastmanager_localbroadcastmanager.version",
            "META-INF/androidx.cursoradapter_cursoradapter.version"
        )
    }
}
dependencies {
    // 기본 라이브러리
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat) // 최신 appcompat 버전 명시
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Firebase 라이브러리 (BOM을 통해 관리)
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-storage")

    // 네이버 지도 SDK (androidx 호환 버전)
    implementation("com.naver.maps:map-sdk:3.14.0")
    implementation(libs.play.services.location)

    // 테스트 라이브러리
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    
}

// Firebase 설정 파일 필요 (google-services.json)
apply(plugin = "com.google.gms.google-services")
