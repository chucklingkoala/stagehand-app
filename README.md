# Stagehand Android Mobile App

<div align="center">

**Mobile companion app for podcast/show producers to manage URLs on-the-go**

![Platform](https://img.shields.io/badge/Platform-Android-green)
![Min SDK](https://img.shields.io/badge/Min%20SDK-31-blue)
![Target SDK](https://img.shields.io/badge/Target%20SDK-34-blue)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple)
![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-blue)

</div>

## 📱 Overview

Stagehand Android is a native mobile application that provides producers with quick access to manage URLs scraped from Discord communities. Built with modern Android development tools and practices.

### Key Features

- ✅ **Browse URLs** - Paginated list with infinite scroll
- 🔍 **Search** - Real-time search with 500ms debounce
- 🏷️ **Categorize** - Assign URLs to custom categories
- 📊 **Filter** - By category, status, or uncategorized
- ⚡ **Quick Actions** - Mark as "On Show" or "Dump"
- 🎨 **Categories Management** - Create, edit, and delete with custom colors
- 🌙 **Dark Theme** - Easy-to-read interface based on web app design

## 🛠️ Tech Stack

- **Language**: Kotlin 1.9.20
- **UI Framework**: Jetpack Compose (Material 3)
- **Architecture**: MVVM + Clean Architecture
- **Networking**: Retrofit 2 + OkHttp 4
- **Dependency Injection**: Koin 3.5
- **Image Loading**: Coil 2.5
- **Async**: Kotlin Coroutines + Flow
- **Navigation**: Jetpack Navigation Compose

## 🏗️ Architecture

```
┌─────────────────────────────────────────┐
│         Presentation Layer              │
│  ┌──────────────────────────────────┐  │
│  │    UI (Jetpack Compose)          │  │
│  └──────────────────────────────────┘  │
│  ┌──────────────────────────────────┐  │
│  │       ViewModel + State          │  │
│  └──────────────────────────────────┘  │
└─────────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────────┐
│          Domain Layer                   │
│  ┌──────────────────────────────────┐  │
│  │       Domain Models              │  │
│  └──────────────────────────────────┘  │
└─────────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────────┐
│           Data Layer                    │
│  ┌──────────────────────────────────┐  │
│  │      Repository Pattern          │  │
│  └──────────────────────────────────┘  │
│  ┌──────────────────────────────────┐  │
│  │  Remote Data Source (Retrofit)   │  │
│  └──────────────────────────────────┘  │
└─────────────────────────────────────────┘
```

## 📂 Project Structure

```
app/src/main/java/com/chucklingkoala/stagehand/
├── data/
│   ├── remote/
│   │   ├── api/
│   │   │   └── StagehandApi.kt          # API service interface
│   │   ├── dto/                          # Data transfer objects
│   │   └── NetworkModule.kt              # Retrofit setup
│   └── repository/
│       ├── UrlRepository.kt              # URL data operations
│       └── CategoryRepository.kt         # Category data operations
├── domain/
│   └── model/
│       ├── Url.kt                        # Domain models
│       ├── Category.kt
│       ├── UrlStatus.kt
│       └── LinkPreview.kt
├── presentation/
│   ├── theme/                            # App theming
│   │   ├── Color.kt                      # Color palette
│   │   ├── Type.kt                       # Typography
│   │   └── Theme.kt                      # Material 3 theme
│   ├── components/                       # Reusable UI components
│   │   ├── UrlCard.kt
│   │   ├── CategoryChip.kt
│   │   └── StatusBadge.kt
│   ├── dashboard/                        # Main screen
│   │   ├── DashboardScreen.kt
│   │   ├── DashboardViewModel.kt
│   │   └── DashboardState.kt
│   ├── urldetail/                        # URL details
│   │   ├── UrlDetailScreen.kt
│   │   ├── UrlDetailViewModel.kt
│   │   └── UrlDetailState.kt
│   ├── categories/                       # Category management
│   │   ├── CategoriesScreen.kt
│   │   ├── CategoriesViewModel.kt
│   │   ├── CategoriesState.kt
│   │   └── CategoryDialogs.kt
│   └── navigation/
│       └── Navigation.kt                 # Navigation graph
├── di/
│   └── AppModule.kt                      # Dependency injection
├── util/
│   ├── DateFormatter.kt                  # Date utilities
│   └── Constants.kt                      # App constants
├── StagehandApplication.kt               # Application class
└── MainActivity.kt                       # Entry point
```

## 🚀 Quick Start

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17+
- Android SDK 31+

### Setup

1. **Clone the repository**
   ```bash
   cd d:\Github\stagehand-app
   ```

2. **Open in Android Studio**
   - File → Open → Select `stagehand-app` folder
   - Wait for Gradle sync (5-15 minutes first time)

3. **Generate App Icons**
   - Right-click `app/src/main/res`
   - New → Image Asset
   - Select `apple-touch-icon.png` as source
   - Click Finish

4. **Run the app**
   - Click ▶️ Run button
   - Select emulator or connected device

For detailed build instructions, see [BUILD_INSTRUCTIONS.md](BUILD_INSTRUCTIONS.md)

## 📖 API Configuration

The app connects to the Stagehand backend API:

- **Production URL**: `https://stagehand.theprestream.com/api/`
- **Configuration**: `app/build.gradle.kts` → `buildConfigField`

## 🎨 Theming

The app uses a dark theme based on the web app's color scheme. Colors can be easily customized in:

**`presentation/theme/Color.kt`**:
```kotlin
object StagehandColors {
    val BackgroundPrimary = Color(0xFF1A1A1A)
    val BackgroundSecondary = Color(0xFF2D2D2D)
    val TextPrimary = Color(0xFFE0E0E0)
    val AccentColor = Color(0xFF7289DA)
    // ... etc
}
```

## 📱 Screens

### Dashboard
- Displays paginated list of URLs
- Real-time search with debounce
- Horizontal scrolling filter chips
- Quick status toggle ("On Show")
- Pull-to-refresh
- Infinite scroll

### URL Detail
- Full URL information
- Link preview (image, title, description)
- Category assignment dropdown
- Status radio buttons (None/On Show/Dump)
- Open in browser
- Share URL
- Copy to clipboard

### Categories
- List of all categories with URL counts
- Create new categories with color picker
- Edit existing categories
- Delete categories (with confirmation)
- Color preview for each category

## 🔧 Key Features

### Search & Filtering

- **Search**: Searches title, URL, posted_by with 500ms debounce
- **Filters**: All, On Show, Dump, Uncategorized, + Category chips
- **Sticky**: Filters persist during scroll
- **Instant**: UI updates immediately

### Pagination

- Page size: 50 URLs per page
- Auto-load more when scrolling near bottom
- Loading indicator during fetch
- Tracks total count and current offset

### State Management

- Unidirectional data flow (MVVM)
- StateFlow for reactive UI updates
- Event-based user actions
- Proper error handling

## 🧪 Testing

The project is structured for easy testing:

- **Unit Tests**: ViewModels, Repositories, Utilities
- **Integration Tests**: API calls, Data flow
- **UI Tests**: Composables (using Compose testing library)

Run tests:
```bash
gradlew test           # Unit tests
gradlew connectedTest  # Instrumented tests
```

## 🔐 Security

- No authentication in v1 (direct API access)
- Prepared for future JWT implementation
- ProGuard rules for release builds
- HTTPS-only network communication
- No sensitive data stored locally

## 📦 Release Build

1. **Create keystore** (one-time):
   ```bash
   keytool -genkey -v -keystore stagehand-release.keystore -alias stagehand -keyalg RSA -keysize 2048 -validity 10000
   ```

2. **Build signed APK**:
   ```bash
   gradlew assembleRelease
   ```

3. **Locate APK**:
   ```
   app/build/outputs/apk/release/app-release.apk
   ```

See [BUILD_INSTRUCTIONS.md](BUILD_INSTRUCTIONS.md) for detailed steps.

## 📝 Future Enhancements

### Phase 2
- [ ] User authentication (JWT)
- [ ] Episode management
- [ ] Push notifications
- [ ] Advanced filters (date range, multi-select)
- [ ] Offline mode with local caching

### Phase 3
- [ ] Leaderboard
- [ ] Manual URL submission
- [ ] Home screen widgets
- [ ] Bulk operations UI
- [ ] iOS version (React Native or SwiftUI)

## 🤝 Contributing

This is a private project for podcast producers. For issues or feature requests:

1. Check existing issues
2. Create detailed bug report or feature request
3. Include screenshots/logs if applicable

## 📄 License

Private project - All rights reserved

## 🙏 Acknowledgments

- Built with Android Studio
- Powered by Jetpack Compose
- Icons from Material Design
- Networking via Retrofit
- DI via Koin

## 📞 Support

For questions or issues:
- Review [BUILD_INSTRUCTIONS.md](BUILD_INSTRUCTIONS.md)
- Check [ANDROID_APP_SPECIFICATION.md](ANDROID_APP_SPECIFICATION.md)
- Test API: https://stagehand.theprestream.com/api/version

---

**Built with ❤️ for podcast producers**
