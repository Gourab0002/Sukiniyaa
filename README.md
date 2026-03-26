<div align="center">

# 🌸 Sukiniyaa

**A native Android client for [sukebei.nyaa.si](https://sukebei.nyaa.si)**

Built with Jetpack Compose & Material 3 for a fast, fluid browsing experience.

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-24%20(Android%207.0)-brightgreen?logo=android)](https://developer.android.com)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-35%20(Android%2015)-brightgreen?logo=android)](https://developer.android.com)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue)](LICENSE)

</div>

---

## ✨ Features

| | Feature | Description |
|---|---|---|
| 🔍 | **Full-text search** | Find torrents by title or keyword |
| ♾️ | **Infinite scroll** | Results load seamlessly as you browse |
| 🗂️ | **Category & quality filters** | Art, Real Life sub-categories; All / No Remakes / Trusted Only |
| ↕️ | **Flexible sorting** | Date, Seeders, Leechers, Size, Downloads, Comments — asc or desc |
| 🃏 | **Rich torrent cards** | Title, category badge, trust status, size, date, S/L/D counts |
| 📄 | **Detail screen** | Full metadata + one-tap magnet open, copy, `.torrent` download, share |
| 🔖 | **Bookmarks** | Save torrents offline; swipe to remove |
| 🎨 | **Theme picker** | Purple · Red · Sakura Pink · Matcha Green · Sunset Orange |
| 🌈 | **Material You** | Dynamic wallpaper colors on Android 12+ |
| 📐 | **Edge-to-edge UI** | Content draws behind system bars |

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin 2.0 |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM — `ViewModel` + `StateFlow` |
| Navigation | Navigation Compose |
| Networking | OkHttp 4.12 |
| Parsing | `XmlPullParser` (RSS) · Jsoup (HTML) · Markwon (Markdown) |
| Image loading | Coil |
| Async | Kotlin Coroutines |

---

## 🚀 Getting Started

### Prerequisites

- **Android Studio** Hedgehog (2023.1.1) or newer  
  *or* JDK 17 + Android SDK API 35 + Build-Tools 35
- Internet connection for the first Gradle sync

### Clone & build

```bash
git clone https://github.com/Gourab0002/Sukiniyaa.git
cd Sukiniyaa
./gradlew assembleDebug
```

Output APK → `app/build/outputs/apk/debug/app-debug.apk`

### Run on a device

```bash
./gradlew installDebug
```

### Open in Android Studio

1. **File → Open** → select the `Sukiniyaa` folder
2. Wait for Gradle sync
3. Press **▶ Run** or `Shift+F10`

---

## ⚙️ How It Works

Sukiniyaa fetches results from the sukebei.nyaa.si **RSS feed**:

```
https://sukebei.nyaa.si/?page=rss&q=<query>&c=<category>&f=<filter>&s=<sort>&o=<order>
```

`SukebeiRssParser` reads the `<item>` elements (including `nyaa:` namespace fields) with `XmlPullParser` and assembles magnet links from the `infoHash` + public trackers — no extra torrent-client API needed.

---

## 🗂️ Project Structure

```
app/src/main/java/com/nyaa/sukiniyaa/
├── MainActivity.kt              # Entry point & NavHost
├── data/
│   ├── api/                     # RSS + HTML parsers
│   ├── model/Torrent.kt         # Data models, enums, SearchParams
│   └── repository/              # Network calls & bookmark storage
└── ui/
    ├── screens/                 # Search, Detail, Bookmarks, Settings
    ├── theme/                   # Colors, typography, Material You
    └── viewmodel/               # StateFlow-backed ViewModels
```

---

## 🤝 Contributing

1. Fork the repo and create a feature branch.
2. Keep PRs focused and small.
3. Open a PR against `main` with a clear description.

---

## 📄 License

Licensed under the [Apache 2.0 License](LICENSE).

---

<div align="center">

> ⚠️ **Disclaimer:** Sukiniyaa is an unofficial third-party client and is not affiliated with or endorsed by sukebei.nyaa.si.

</div>
