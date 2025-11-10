# Stagehand Android Mobile App

<div align="center">

**Mobile companion app for podcast/show producers to manage URLs on-the-go**

![Platform](https://img.shields.io/badge/Platform-Android-green)
![Min SDK](https://img.shields.io/badge/Min%20SDK-31-blue)
![Target SDK](https://img.shields.io/badge/Target%20SDK-34-blue)
![Version](https://img.shields.io/badge/Version-1.0.0-orange)
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
- **UI Framework**: Jetpack Compose (BOM 2024.12.01) with Material 3
- **Architecture**: MVVM + Clean Architecture
- **Networking**: Retrofit 2.9.0 + OkHttp 4.12.0
- **Dependency Injection**: Koin 3.5.0
- **Image Loading**: Coil 2.5.0
- **Async**: Kotlin Coroutines 1.7.3 + Flow
- **Navigation**: Jetpack Navigation Compose 2.7.6
- **Build System**: Gradle 9.0-milestone-1

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
stagehand-app/
├── app/
│   ├── src/main/
│   │   ├── java/com/chucklingkoala/stagehand/
│   │   │   ├── data/                     # Data layer
│   │   │   │   ├── remote/
│   │   │   │   │   ├── api/
│   │   │   │   │   │   └── StagehandApi.kt          # Retrofit API interface
│   │   │   │   │   ├── dto/                          # Data transfer objects
│   │   │   │   │   │   ├── BulkOperationRequest.kt
│   │   │   │   │   │   ├── CategoryDto.kt
│   │   │   │   │   │   ├── CategoryRequest.kt
│   │   │   │   │   │   ├── LinkPreviewDto.kt
│   │   │   │   │   │   ├── PaginatedUrlResponse.kt
│   │   │   │   │   │   ├── UpdateUrlRequest.kt
│   │   │   │   │   │   ├── UrlDto.kt
│   │   │   │   │   │   └── VersionDto.kt
│   │   │   │   │   └── NetworkModule.kt              # Retrofit & OkHttp configuration
│   │   │   │   └── repository/                       # Repository pattern
│   │   │   │       ├── UrlRepository.kt
│   │   │   │       └── CategoryRepository.kt
│   │   │   ├── domain/                   # Business logic layer
│   │   │   │   └── model/                            # Domain models
│   │   │   │       ├── Url.kt
│   │   │   │       ├── Category.kt
│   │   │   │       ├── UrlStatus.kt
│   │   │   │       └── LinkPreview.kt
│   │   │   ├── presentation/             # UI layer (Jetpack Compose)
│   │   │   │   ├── theme/                            # Material 3 theming
│   │   │   │   │   ├── Color.kt
│   │   │   │   │   ├── Type.kt
│   │   │   │   │   └── Theme.kt
│   │   │   │   ├── components/                       # Reusable UI components
│   │   │   │   │   ├── UrlCard.kt
│   │   │   │   │   ├── CategoryChip.kt
│   │   │   │   │   └── StatusBadge.kt
│   │   │   │   ├── dashboard/                        # Dashboard screen
│   │   │   │   │   ├── DashboardScreen.kt
│   │   │   │   │   ├── DashboardViewModel.kt
│   │   │   │   │   └── DashboardState.kt
│   │   │   │   ├── urldetail/                        # URL detail screen
│   │   │   │   │   ├── UrlDetailScreen.kt
│   │   │   │   │   ├── UrlDetailViewModel.kt
│   │   │   │   │   └── UrlDetailState.kt
│   │   │   │   ├── categories/                       # Categories screen
│   │   │   │   │   ├── CategoriesScreen.kt
│   │   │   │   │   ├── CategoriesViewModel.kt
│   │   │   │   │   ├── CategoriesState.kt
│   │   │   │   │   └── CategoryDialogs.kt
│   │   │   │   └── navigation/
│   │   │   │       └── Navigation.kt                 # Navigation graph
│   │   │   ├── di/                       # Dependency injection
│   │   │   │   └── AppModule.kt                      # Koin module
│   │   │   ├── util/                     # Utilities
│   │   │   │   ├── DateFormatter.kt
│   │   │   │   └── Constants.kt
│   │   │   ├── StagehandApplication.kt               # Application class (Koin init)
│   │   │   └── MainActivity.kt                       # Entry point
│   │   ├── res/                          # Android resources
│   │   │   ├── drawable/
│   │   │   ├── mipmap-*/                             # App icons
│   │   │   ├── values/
│   │   │   │   ├── colors.xml
│   │   │   │   ├── strings.xml
│   │   │   │   └── themes.xml
│   │   │   └── xml/
│   │   │       ├── network_security_config.xml       # HTTPS enforcement
│   │   │       └── backup_rules.xml
│   │   └── AndroidManifest.xml
│   ├── build.gradle.kts                  # App module configuration
│   └── proguard-*.pro                    # ProGuard rules
├── gradle/                               # Gradle wrapper
├── build.gradle.kts                      # Root project configuration
├── settings.gradle.kts                   # Gradle settings
├── gradle.properties                     # Gradle properties
├── keystore.properties                   # Signing configuration (gitignored)
├── stagehand-release.keystore            # Release keystore (gitignored)
└── Documentation/
    ├── README.md                         # This file
    ├── QUICK_START.md                    # 5-step quick start
    ├── BUILD_INSTRUCTIONS.md             # Detailed build guide
    ├── ANDROID_APP_SPECIFICATION.md      # Complete specification
    ├── IMPLEMENTATION_STATUS.md          # Development status
    └── NEXT_STEPS.md                     # Roadmap
```

## 🚀 Quick Start

### Prerequisites

- Android Studio Iguana (2023.2.1) or newer
- JDK 17 or JDK 21
- Android SDK 31+ (API Level 31-34)
- Gradle 9.0+ (included via wrapper)

### Setup

1. **Clone or navigate to the repository**
   ```bash
   cd d:\Github\stagehand-app
   ```

2. **Open in Android Studio**
   - File → Open → Select the `stagehand-app` folder
   - Wait for Gradle sync to complete (may take 5-15 minutes on first run)
   - If prompted about Gradle version, trust the project settings

3. **Configure signing (for release builds)**
   - Create `keystore.properties` in the root directory (already exists)
   - Verify it contains your keystore credentials
   - See [BUILD_INSTRUCTIONS.md](BUILD_INSTRUCTIONS.md) for keystore setup

4. **Run the app**
   - Click the Run ▶️ button in the toolbar
   - Select an emulator (API 31+) or connected physical device
   - App will build and launch automatically

For detailed build instructions, troubleshooting, and release builds, see [BUILD_INSTRUCTIONS.md](BUILD_INSTRUCTIONS.md)

## 📖 API Configuration

The app connects to the Stagehand backend API:

- **Production URL**: `https://stagehand.theprestream.com/api/`
- **Configuration**: Set in [app/build.gradle.kts](app/build.gradle.kts) → `buildConfigField("API_BASE_URL")`
- **Available Endpoints**:
  - `GET /urls` - Fetch paginated URLs with filters
  - `GET/PUT /urls/{id}` - Get/update single URL
  - `POST /urls/bulk` - Bulk operations
  - `GET /categories` - Fetch all categories
  - `POST/PUT/DELETE /categories` - Category CRUD operations
  - `GET /link-preview` - Fetch URL preview metadata
  - `GET /version` - API version check

## 🎨 Theming

The app uses a dark theme inspired by the web app's color scheme. Colors are defined using Material 3 theming and can be customized in [app/src/main/java/com/chucklingkoala/stagehand/presentation/theme/Color.kt](app/src/main/java/com/chucklingkoala/stagehand/presentation/theme/Color.kt):

```kotlin
object StagehandColors {
    val BackgroundPrimary = Color(0xFF1A1A1A)
    val BackgroundSecondary = Color(0xFF2D2D2D)
    val TextPrimary = Color(0xFFE0E0E0)
    val AccentColor = Color(0xFF7289DA)
    // ... etc
}
```

The theme is applied globally in [Theme.kt](app/src/main/java/com/chucklingkoala/stagehand/presentation/theme/Theme.kt) using Material 3's `darkColorScheme`.

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

The project is structured for testing with proper separation of concerns:

- **Unit Tests**: ViewModels, Repositories, Utilities (JUnit + Mockk)
- **Integration Tests**: API calls, Data flow
- **UI Tests**: Composables (Compose UI Testing Library + Espresso)

Run tests:
```bash
./gradlew test                    # Unit tests
./gradlew connectedAndroidTest    # Instrumented tests on device/emulator
```

**Note**: Test implementations are ready to be written. The testing infrastructure is in place with all necessary dependencies.

## 🔐 Security

- **Network**: HTTPS-only communication enforced via Network Security Config
- **No Authentication**: v1.0 uses direct API access (authentication planned for v2)
- **ProGuard**: Code obfuscation rules configured (currently disabled in release due to Koin reflection)
- **No Local Storage**: No sensitive data cached locally
- **Future Ready**: Infrastructure prepared for JWT authentication implementation

## 📦 Release Build

A keystore already exists for signing release builds. To create a signed APK:

1. **Verify keystore.properties exists** in the root directory with:
   ```properties
   storeFile=stagehand-release.keystore
   storePassword=your_store_password
   keyAlias=stagehand
   keyPassword=your_key_password
   ```

2. **Build signed APK**:
   ```bash
   ./gradlew assembleRelease
   ```

3. **Locate the signed APK**:
   ```
   app/build/outputs/apk/release/app-release.apk
   ```

4. **Install on device**:
   ```bash
   adb install app/build/outputs/apk/release/app-release.apk
   ```

See [BUILD_INSTRUCTIONS.md](BUILD_INSTRUCTIONS.md) for detailed build steps, creating new keystores, and troubleshooting.

## 📝 Future Enhancements

See [NEXT_STEPS.md](NEXT_STEPS.md) for the complete roadmap.

### Planned Features (v2.0)
- User authentication with JWT tokens
- Episode management and URL assignment to episodes
- Push notifications for new URLs
- Advanced filters (date range, multi-select categories)
- Offline mode with local database caching
- Dark/Light theme toggle

### Future Considerations (v3.0+)
- Leaderboard for top URL contributors
- Manual URL submission from mobile
- Home screen widgets for quick access
- Bulk operations UI (multi-select and batch actions)
- Cross-platform version (iOS via Kotlin Multiplatform or native SwiftUI)

## 🤝 Contributing

This is a private project for podcast producers. If you encounter issues or have feature requests:

1. Review existing documentation ([QUICK_START.md](QUICK_START.md), [BUILD_INSTRUCTIONS.md](BUILD_INSTRUCTIONS.md))
2. Check the [IMPLEMENTATION_STATUS.md](IMPLEMENTATION_STATUS.md) to see what's been completed
3. Create detailed bug reports with:
   - Device/emulator information
   - Android version
   - Steps to reproduce
   - Screenshots or logs
4. For feature requests, check [NEXT_STEPS.md](NEXT_STEPS.md) first

## 📄 License

Private project - All rights reserved

**Package**: `com.chucklingkoala.stagehand`

## 🙏 Acknowledgments

- **Built with**: Android Studio Iguana
- **UI Framework**: Jetpack Compose with Material 3
- **Icons**: Material Design Icons Extended
- **Networking**: Retrofit + OkHttp
- **Dependency Injection**: Koin
- **Image Loading**: Coil
- **Testing**: JUnit, Mockk, Espresso

## 📞 Support & Resources

**Documentation:**
- [QUICK_START.md](QUICK_START.md) - Get started in 5 steps
- [BUILD_INSTRUCTIONS.md](BUILD_INSTRUCTIONS.md) - Detailed build guide with troubleshooting
- [ANDROID_APP_SPECIFICATION.md](ANDROID_APP_SPECIFICATION.md) - Complete app specification
- [IMPLEMENTATION_STATUS.md](IMPLEMENTATION_STATUS.md) - Development progress tracking
- [NEXT_STEPS.md](NEXT_STEPS.md) - Future roadmap and planned features

**API:**
- Production: https://stagehand.theprestream.com/api/
- Test endpoint: https://stagehand.theprestream.com/api/version

**Project Status:** ✅ Production-ready v1.0.0

---

**Built with ❤️ for podcast producers**
