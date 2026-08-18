# Sukiniyaa

A native Android client for [sukebei.nyaa.si](https://sukebei.nyaa.si). Search, filter, and open torrents from a Material 3 interface built with Jetpack Compose.

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-24-brightgreen?logo=android)](https://developer.android.com)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-35-brightgreen?logo=android)](https://developer.android.com)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue)](LICENSE)

Sukiniyaa is unofficial and is not affiliated with or endorsed by sukebei.nyaa.si.

## Features

- **Search** — full-text queries against the public RSS feed, with infinite scroll
- **Filters** — Art and Real Life categories, plus All / No Remakes / Trusted Only
- **Sorting** — date, seeders, leechers, size, downloads, or comments (ascending or descending)
- **Torrent details** — metadata, markdown description, nested file list, and comments
- **Actions** — open magnet, copy magnet, download `.torrent`, share, or open the listing in a browser
- **Bookmarks** — save listings locally; swipe to remove
- **Search history** — last 50 queries, with swipe-to-delete and clear-all
- **Themes** — Material You on Android 12+, plus Sukebei Red, Sakura Pink, Matcha Green, and Sunset Orange
- **Edge-to-edge** — content draws behind system bars

## Requirements

- Android Studio Ladybug (2024.2) or newer, **or** JDK 17 + Android SDK API 35 + Build-Tools 35
- Android 7.0 (API 24) or later on the device / emulator
- Network access for the first Gradle sync and for live search

## Build

```bash
git clone https://github.com/Gourab0002/Sukiniyaa.git
cd Sukiniyaa
```

On Windows use `gradlew.bat` in place of `./gradlew`.

```bash
./gradlew testDebugUnitTest
./gradlew assembleDebug
```

Debug APK:

```
app/build/outputs/apk/debug/app-debug.apk
```

Install on a connected device:

```bash
./gradlew installDebug
```

In Android Studio: **File → Open** the `Sukiniyaa` directory, wait for Gradle sync, then run the `app` configuration.

Release builds enable R8 shrinking (`./gradlew assembleRelease`). Sign the output before distributing.

## Tests

Unit tests cover RSS parsing, HTML page parsing (description, file paths, comments), search URL construction, page merge / dedupe, date formatting, and torrent identity.

```bash
./gradlew testDebugUnitTest
```

CI on `main` and pull requests runs those tests, then builds the debug APK.

## How it works

Search uses the sukebei RSS endpoint:

```
https://sukebei.nyaa.si/?page=rss&q=<query>&c=<category>&f=<filter>&s=<sort>&o=<order>&p=<page>
```

`SukebeiRssParser` reads each `<item>`, including `nyaa:` namespace fields (seeders, size, info hash, trusted, remake). Magnet links are built from the info hash and a short public tracker list — no third-party torrent API is required.

The detail screen fetches the HTML listing and `SukebeiCommentParser` extracts the markdown description, folder-aware file list, and comments.

Local data (bookmarks, search history, theme) is stored in SharedPreferences. Bookmarks and history are not included in Android backups.

## Architecture

MVVM with a single-activity Compose `NavHost`.

| Layer | Role |
| --- | --- |
| Kotlin 2.0 | Language |
| Jetpack Compose + Material 3 | UI |
| Navigation Compose | Tabs and detail route (`detail/{id}`) |
| `ViewModel` + `StateFlow` | Screen state |
| OkHttp | Shared HTTP client; requests cancel with coroutines |
| XmlPullParser / Jsoup / Markwon | RSS, HTML, and markdown |
| Coil | Avatars and remote images |
| Coroutines | I/O and pagination |

```
app/src/main/java/com/nyaa/sukiniyaa/
├── MainActivity.kt          Entry point and navigation
├── data/
│   ├── api/                 RSS and HTML parsers
│   ├── model/               Torrent, search params, page data
│   ├── network/             Shared OkHttp client
│   └── repository/          Search, bookmarks, history
├── ui/
│   ├── screens/             Search, History, Bookmarks, Settings, Detail
│   ├── theme/               Color schemes and Material You
│   └── viewmodel/           StateFlow view models
└── util/                    Date formatting
```

## Contributing

1. Fork the repository and create a focused feature branch.
2. Run `./gradlew testDebugUnitTest` before opening a pull request.
3. Target `main` and describe the change clearly.

## License

Licensed under the [Apache License 2.0](LICENSE).
