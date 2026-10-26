plugins { id("com.android.test"); id("androidx.baselineprofile") }
android {
    namespace = "dev.senda.launcher.baselineprofile"
    compileSdk = 37
    defaultConfig { minSdk = 29; targetSdk = 37; testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" }
    targetProjectPath = ":app"
    buildTypes { create("performance") { matchingFallbacks += listOf("release") } }
    experimentalProperties["android.experimental.self-instrumenting"] = true
    compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
}
baselineProfile { useConnectedDevices = true }
dependencies {
    implementation("androidx.benchmark:benchmark-macro-junit4:1.5.0")
    implementation("androidx.test.ext:junit:1.3.0")
    implementation("androidx.test:runner:1.7.0")
    implementation("androidx.test.uiautomator:uiautomator:2.3.0")
}
