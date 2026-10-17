# Afghan Solar Hijri Calendar (offline)
Dari/Pashto months, Saturday-first week, Friday weekend, Afghan holidays, Gregorian + lunar Hijri secondary dates,
transparent minimal home-screen widget. No internet permission.

## Build (JDK 17 + Android SDK, or the Android Linux terminal)
    gradle wrapper --gradle-version 8.7     # once, creates ./gradlew
    ./gradlew assembleRelease               # APK in app/build/outputs/apk/release/
Or push to GitHub: the included Actions workflow builds the APK for you.

ARM64 Linux terminal: set android.aapt2FromMavenOverride in gradle.properties to an ARM aapt2.
Holidays: edit Holidays.of() in Jalali.kt.
