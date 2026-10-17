# Afghan Hijri Shamsi Calendar

Offline Hijri Shamsi (Afghan Solar Hijri) calendar for Android, with Dari/Pashto months, Afghan holidays, and a transparent minimal home-screen widget.

## Features

- Hijri Shamsi calendar with Dari and Pashto month names, in Persian digits
- Saturday-first week, with Friday as the weekend
- Gregorian and lunar Hijri dates shown alongside
- Afghan holidays (Nowruz, Eids, Ashura, Mawlid, national days)
- Transparent, minimal home-screen widget that updates at midnight
- 100% offline: no internet permission, no ads, no tracking
- Light and dark mode

## Install

1. Go to the **Releases** page and download the latest `.apk`.
2. Open it on your phone and allow "Install unknown apps" if asked.
3. Long-press the home screen, choose **Widgets**, and add the calendar.

Requires Android 8.0 (API 26) or newer.

## Build from source

Requires JDK 17 and the Android SDK.

```
gradle wrapper --gradle-version 8.7
./gradlew assembleRelease
```

The APK is created in `app/build/outputs/apk/release/`.

Or let GitHub build it: every push runs the workflow in `.github/workflows/build.yml`. Download the APK from the **Actions** tab under Artifacts.

## Holidays

Edit `Holidays.of()` in `app/src/main/java/af/calendar/Jalali.kt` to match the calendar you follow. Lunar holidays use the Umm al-Qura calendar, so they may differ by one day from local moon sighting.

## License

MIT
