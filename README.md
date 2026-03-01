# Sukiniyaa

A native Android app for searching torrents on [sukebei.nyaa.si](https://sukebei.nyaa.si) — the adult content section of the Nyaa tracker. Built with Jetpack Compose and Material 3 for a fast, fluid experience.

---

## Features

- **Full-text search** — find any torrent by title or keyword
- **Category filtering** — narrow results to Art (Anime, Doujinshi, Games, Manga, Pictures) and Real Life (Photobooks / Pictures, Videos) sub-categories
- **Quality filter** — show all results, exclude remakes, or display trusted uploads only
- **Flexible sorting** — sort by Date, Seeders, Leechers, Size, Downloads, or Comments in ascending or descending order
- **Torrent cards** — each result shows title, category badge, Trusted / Remake status, file size, publish date, seeder count, leecher count, and download count at a glance
- **Detail screen** — view full torrent metadata plus one-tap actions:
  - Open magnet link in any torrent client
  - Copy magnet link to clipboard
  - Download the `.torrent` file
  - Share the torrent page or magnet link
  - Open the torrent page on sukebei.nyaa.si
- **Bookmarks** — save torrents locally and access them offline; swipe to remove
- **Theme picker** — choose from Default Purple, Sukebei Red, Sakura Pink, Matcha Green, or Sunset Orange
- **Material You** — dynamic color support on Android 12+ adapts to your wallpaper
- **Edge-to-edge UI** — content draws behind the system bars for an immersive layout

---

## Screenshots

> _Build the app and run it on a device or emulator to see the UI._

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin 2.0 |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM — `ViewModel` + `StateFlow` |
| Navigation | Navigation Compose |
| Networking | OkHttp 4.12 |
| XML parsing | Android `XmlPullParser` (no extra deps) |
| HTML parsing | Jsoup |
| Markdown | Markwon |
| Image loading | Coil |
| Async | Kotlin Coroutines |
| Min SDK | 24 (Android 7.0) |
| Target SDK | 35 (Android 15) |

---

## Project Structure

```
app/src/main/
├── java/com/nyaa/sukiniyaa/
│   ├── MainActivity.kt                  # Entry point, NavHost setup
│   ├── data/
│   │   ├── api/
│   │   │   ├── SukebeiRssParser.kt      # RSS XML parser + magnet-link builder
│   │   │   └── SukebeiCommentParser.kt  # Jsoup HTML parser for descriptions & comments
│   │   ├── model/
│   │   │   └── Torrent.kt               # Data models, enums, SearchParams
│   │   └── repository/
│   │       ├── SukebeiRepository.kt     # OkHttp calls, URL builder
│   │       └── BookmarkRepository.kt    # SharedPreferences bookmark storage
│   └── ui/
│       ├── screens/
│       │   ├── SearchScreen.kt          # Search bar, results list, filter sheet
│       │   ├── TorrentDetailScreen.kt   # Full detail + action buttons + comments
│       │   ├── BookmarksScreen.kt       # Saved bookmarks with swipe-to-dismiss
│       │   └── SettingsScreen.kt        # Theme selection
│       ├── theme/
│       │   ├── Color.kt
│       │   ├── Theme.kt                 # Dynamic color / dark mode support
│       │   ├── ThemePreferences.kt      # Persistent theme selection
│       │   └── Type.kt
│       └── viewmodel/
│           ├── SearchViewModel.kt       # StateFlow-backed search state
│           ├── BookmarkViewModel.kt     # Bookmark state management
│           └── CommentsViewModel.kt     # Torrent description & comments state
└── res/
    ├── drawable/                        # Adaptive icon foreground
    ├── mipmap-anydpi-v26/               # Adaptive icon definitions
    └── values/                          # strings, colors, theme
```

---

## Building

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or newer **or** JDK 17 + Android SDK (API 35)
- Android SDK Build-Tools 35
- An internet connection for the first Gradle sync (downloads dependencies)

### Clone & build

```bash
git clone https://github.com/Gourab0002/Sukiniyaa.git
cd Sukiniyaa
./gradlew assembleDebug
```

The debug APK is written to `app/build/outputs/apk/debug/app-debug.apk`.

### Install directly on a connected device

```bash
./gradlew installDebug
```

### Open in Android Studio

1. **File → Open** and select the `Sukiniyaa` directory.
2. Wait for the Gradle sync to finish.
3. Press **Run ▶** or use `Shift+F10`.

---

## How It Works

Sukiniyaa queries the sukebei.nyaa.si **RSS feed** endpoint:

```
https://sukebei.nyaa.si/?page=rss&q=<query>&c=<category>&f=<filter>&s=<sort>&o=<order>
```

The RSS feed returns standard `<item>` elements extended with `nyaa:` namespace fields (seeders, leechers, downloads, infoHash, size, trusted, remake). `SukebeiRssParser` reads the stream with `XmlPullParser` and builds a list of `Torrent` data objects.

Magnet links are assembled from the `infoHash` field and a set of public trackers, so no additional torrent-client API is required.

---

## Search Parameters

| Parameter | Options |
|---|---|
| **Category** | All, Art, Art-Anime, Art-Doujinshi, Art-Games, Art-Manga, Art-Pictures, Real Life, Real Life-Photobooks / Pictures, Real Life-Videos |
| **Filter** | No Filter · No Remakes · Trusted Only |
| **Sort by** | Date · Seeders · Leechers · Size · Downloads · Comments |
| **Order** | Descending · Ascending |

---

## Permissions

| Permission | Reason |
|---|---|
| `INTERNET` | Fetch search results from sukebei.nyaa.si |

No other permissions are requested.

---

## Contributing

1. Fork the repository and create a feature branch.
2. Make your changes — keep PRs focused and small.
3. Open a pull request against `main` with a clear description.

---

## License

This project is open source. See [LICENSE](LICENSE) for details.

---

> **Disclaimer:** Sukiniyaa is an unofficial third-party client. It is not affiliated with or endorsed by sukebei.nyaa.si.
