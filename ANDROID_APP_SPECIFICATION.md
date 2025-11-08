# Stagehand Android Mobile App - Complete Specification Document

**Version:** 1.0
**Date:** 2025-11-08
**Purpose:** Comprehensive specification for building an Android mobile application to interface with the Stagehand backend

---

## Table of Contents

1. [Executive Summary](#executive-summary)
2. [Project Overview](#project-overview)
3. [Backend API Reference](#backend-api-reference)
4. [Android App Architecture](#android-app-architecture)
5. [Data Models](#data-models)
6. [Screen Specifications](#screen-specifications)
7. [UI/UX Design Guidelines](#uiux-design-guidelines)
8. [Implementation Guide](#implementation-guide)
9. [Testing Strategy](#testing-strategy)
10. [Deployment Checklist](#deployment-checklist)
11. [Future Enhancements](#future-enhancements)

---

## 1. Executive Summary

### Project Goal
Create a mobile Android application for podcast/show producers to manage and categorize URLs scraped from Discord channels while on-the-go.

### Target Users
- Podcast producers
- Co-producers
- Content managers who need to categorize community-submitted URLs

### Core Features (MVP)
- **No authentication** - Direct access to dashboard on launch
- **URL Dashboard** - Mobile-optimized list view with filtering and search
- **Categorization** - View, create, and assign categories to URLs
- **Status Management** - Mark URLs as "On Show" or "Dump"
- **Category Management** - Create, edit, and manage categories with custom colors

### Out of Scope (Initial Release)
- User authentication (framework prepared for future)
- Episode management with show notes
- Image uploads
- Showtime drag-and-drop view
- Offline mode
- Push notifications
- Leaderboard

### Technology Stack Flexibility
This specification is **framework-agnostic** and can be implemented with:
- **Native Android** (Kotlin + Jetpack Compose)
- **React Native** (TypeScript + React)
- **Flutter** (Dart)

---

## 2. Project Overview

### 2.1 What is Stagehand?

Stagehand is a full-stack web application that automates the collection and organization of URLs shared in Discord communities. It:
- Monitors Discord channels for shared links
- Automatically scrapes and stores URLs with metadata
- Allows producers to categorize content by topic
- Tracks contributor engagement via leaderboards
- Prepares show content for podcast/livestream production

### 2.2 Backend Technology

**Stack:**
- Node.js/Express backend (TypeScript)
- PostgreSQL 15+ database
- RESTful API architecture
- JWT-based authentication (future use)
- Docker Compose deployment

**API Base URL:**
```
Development: http://10.0.2.2:5001/api (Android Emulator)
Development: http://<computer_ip>:5001/api (Physical Device)
Production: https://your-domain.com/api
```

### 2.3 Mobile App Requirements

**Platform:** Android 8.0 (API 26) and above
**Distribution:** Private APK file (no Play Store initially)
**Session Management:** None (no authentication in v1)
**Network:** Requires active internet connection (no offline mode)
**Orientation:** Portrait primary, responsive design

---

## 3. Backend API Reference

### 3.1 Base Configuration

**API Base URL:** `http://your-server:5001/api`
**Content-Type:** `application/json`
**Authentication:** None required for mobile app v1

### 3.2 URL Endpoints

#### GET /api/urls
**Purpose:** Get paginated list of URLs with filtering

**Query Parameters:**
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `limit` | number | 100 | Items per page (25/50/100/200) |
| `offset` | number | 0 | Pagination offset |
| `category_id` | number\|"0" | - | Filter by category (0 = uncategorized) |
| `status` | string | - | Filter by status ("on_show"\|"dump") |
| `covered` | boolean | - | Filter by covered flag |
| `search` | string | - | Search in title/URL/description |
| `posted_by` | string | - | Filter by username |
| `sort` | string | "desc" | Sort order ("asc"\|"desc") |

**Response:**
```json
{
  "urls": [
    {
      "id": 1,
      "url": "https://example.com",
      "posted_by": "john_doe",
      "posted_at": "2025-01-15T12:00:00Z",
      "title": "Example Article",
      "discord_message_id": "1234567890",
      "discord_message_link": "https://discord.com/channels/...",
      "discord_user_id": "user_id_123",
      "twitter_username": "handle",
      "category_id": 2,
      "episode_id": null,
      "status": "on_show",
      "covered": false,
      "display_order": 0,
      "created_at": "2025-01-15T12:00:00Z",
      "updated_at": "2025-01-15T12:30:00Z",
      "category_name": "Tech News",
      "episode_number": null,
      "is_duplicate": false
    }
  ],
  "total": 150,
  "limit": 100,
  "offset": 0
}
```

#### GET /api/urls/:id
**Purpose:** Get single URL by ID

**Response:** Single URL object (same structure as above)

#### PUT /api/urls/:id
**Purpose:** Update URL properties

**Request Body:**
```json
{
  "category_id": 2,
  "status": "on_show",
  "display_order": 5,
  "covered": true
}
```

**Response:** Updated URL object

#### POST /api/urls/bulk
**Purpose:** Bulk categorize or flag multiple URLs

**Request Body:**
```json
{
  "url_ids": [1, 2, 3, 5, 8],
  "operation": "categorize",
  "value": 2
}
```

**Operations:**
- `categorize`: Set category_id (value = category_id or null)
- `flag`: Set status (value = "on_show" or "dump")

**Response:**
```json
{
  "success": true,
  "message": "Bulk categorize completed",
  "affected": 5
}
```

### 3.3 Category Endpoints

#### GET /api/categories
**Purpose:** Get all categories with URL counts

**Response:**
```json
[
  {
    "id": 1,
    "name": "Tech News",
    "color": "#3498db",
    "created_at": "2025-01-01T00:00:00Z",
    "updated_at": "2025-01-01T00:00:00Z",
    "url_count": 25
  },
  {
    "id": 2,
    "name": "Politics",
    "color": "#e74c3c",
    "created_at": "2025-01-02T00:00:00Z",
    "updated_at": "2025-01-02T00:00:00Z",
    "url_count": 12
  }
]
```

#### GET /api/categories/:id
**Purpose:** Get single category

**Response:** Single category object

#### POST /api/categories
**Purpose:** Create new category

**Request Body:**
```json
{
  "name": "Science",
  "color": "#2ecc71"
}
```

**Response:** Created category object

#### PUT /api/categories/:id
**Purpose:** Update category

**Request Body:**
```json
{
  "name": "Science & Tech",
  "color": "#27ae60"
}
```

**Response:** Updated category object

### 3.4 Link Preview Endpoint

#### GET /api/link-preview
**Purpose:** Get cached link preview or fetch new one

**Query Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `url` | string | Yes | URL to get preview for |
| `fetch` | boolean | No | Fetch from API if not cached |

**Response:**
```json
{
  "title": "Article Title",
  "description": "Article description text...",
  "image_url": "https://example.com/image.jpg"
}
```

### 3.5 Version Endpoint

#### GET /api/version
**Purpose:** Get application version (useful for compatibility checks)

**Response:**
```json
{
  "version": "1.1.0",
  "name": "stagehand",
  "description": "Stagehand automates the collection..."
}
```

### 3.6 Error Handling

All endpoints return errors in the following format:

```json
{
  "success": false,
  "error": "Error message here",
  "message": "User-friendly error message"
}
```

**HTTP Status Codes:**
- `200` - Success
- `400` - Bad Request (invalid parameters)
- `401` - Unauthorized (for future auth-protected endpoints)
- `404` - Not Found
- `500` - Internal Server Error

---

## 4. Android App Architecture

### 4.1 Recommended Architecture Pattern

**MVVM (Model-View-ViewModel)** with Clean Architecture principles:

```
┌─────────────────────────────────────────┐
│         Presentation Layer              │
│  ┌──────────────────────────────────┐  │
│  │    UI (Compose/Views/Widgets)    │  │
│  └──────────────────────────────────┘  │
│  ┌──────────────────────────────────┐  │
│  │       ViewModel + State          │  │
│  └──────────────────────────────────┘  │
└─────────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────────┐
│          Domain Layer                   │
│  ┌──────────────────────────────────┐  │
│  │        Use Cases/Interactors     │  │
│  └──────────────────────────────────┘  │
│  ┌──────────────────────────────────┐  │
│  │       Domain Models/Entities     │  │
│  └──────────────────────────────────┘  │
└─────────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────────┐
│           Data Layer                    │
│  ┌──────────────────────────────────┐  │
│  │      Repository Implementation   │  │
│  └──────────────────────────────────┘  │
│  ┌──────────────────────────────────┐  │
│  │  Remote Data Source (API)        │  │
│  └──────────────────────────────────┘  │
└─────────────────────────────────────────┘
```

### 4.2 Project Structure

#### For Kotlin/Jetpack Compose:

```
app/
├── src/main/
│   ├── java/com/yourname/stagehand/
│   │   ├── StagehandApplication.kt
│   │   │
│   │   ├── data/
│   │   │   ├── remote/
│   │   │   │   ├── api/
│   │   │   │   │   ├── StagehandApi.kt
│   │   │   │   │   └── ApiService.kt
│   │   │   │   ├── dto/
│   │   │   │   │   ├── UrlDto.kt
│   │   │   │   │   ├── CategoryDto.kt
│   │   │   │   │   └── PaginatedResponse.kt
│   │   │   │   └── NetworkModule.kt
│   │   │   │
│   │   │   └── repository/
│   │   │       ├── UrlRepository.kt
│   │   │       └── CategoryRepository.kt
│   │   │
│   │   ├── domain/
│   │   │   ├── model/
│   │   │   │   ├── Url.kt
│   │   │   │   ├── Category.kt
│   │   │   │   └── UrlStatus.kt
│   │   │   │
│   │   │   └── usecase/
│   │   │       ├── GetUrlsUseCase.kt
│   │   │       ├── UpdateUrlUseCase.kt
│   │   │       ├── GetCategoriesUseCase.kt
│   │   │       └── CreateCategoryUseCase.kt
│   │   │
│   │   ├── presentation/
│   │   │   ├── dashboard/
│   │   │   │   ├── DashboardScreen.kt
│   │   │   │   ├── DashboardViewModel.kt
│   │   │   │   └── DashboardState.kt
│   │   │   │
│   │   │   ├── urldetail/
│   │   │   │   ├── UrlDetailScreen.kt
│   │   │   │   ├── UrlDetailViewModel.kt
│   │   │   │   └── UrlDetailState.kt
│   │   │   │
│   │   │   ├── categories/
│   │   │   │   ├── CategoriesScreen.kt
│   │   │   │   ├── CategoriesViewModel.kt
│   │   │   │   └── CategoriesState.kt
│   │   │   │
│   │   │   ├── components/
│   │   │   │   ├── UrlCard.kt
│   │   │   │   ├── CategoryChip.kt
│   │   │   │   ├── FilterBar.kt
│   │   │   │   └── SearchBar.kt
│   │   │   │
│   │   │   ├── navigation/
│   │   │   │   └── Navigation.kt
│   │   │   │
│   │   │   └── theme/
│   │   │       ├── Color.kt
│   │   │       ├── Theme.kt
│   │   │       └── Type.kt
│   │   │
│   │   └── util/
│   │       ├── DateFormatter.kt
│   │       ├── ColorParser.kt
│   │       └── Constants.kt
│   │
│   ├── res/
│   │   ├── drawable/
│   │   │   ├── ic_launcher.xml (from logo.png)
│   │   │   └── logo.xml
│   │   ├── values/
│   │   │   ├── colors.xml
│   │   │   ├── strings.xml
│   │   │   └── themes.xml
│   │   └── mipmap/
│   │       └── (generated launcher icons)
│   │
│   └── AndroidManifest.xml
│
└── build.gradle.kts
```

#### For React Native:

```
src/
├── api/
│   ├── client.ts
│   ├── urls.ts
│   └── categories.ts
│
├── types/
│   ├── Url.ts
│   ├── Category.ts
│   └── ApiResponse.ts
│
├── screens/
│   ├── DashboardScreen.tsx
│   ├── UrlDetailScreen.tsx
│   └── CategoriesScreen.tsx
│
├── components/
│   ├── UrlCard.tsx
│   ├── CategoryChip.tsx
│   ├── FilterBar.tsx
│   └── SearchBar.tsx
│
├── navigation/
│   └── AppNavigator.tsx
│
├── hooks/
│   ├── useUrls.ts
│   └── useCategories.ts
│
├── theme/
│   ├── colors.ts
│   └── typography.ts
│
└── utils/
    ├── dateFormatter.ts
    └── constants.ts
```

### 4.3 Key Dependencies

#### Kotlin/Native Android:
```kotlin
// build.gradle.kts (app level)
dependencies {
    // Jetpack Compose
    implementation("androidx.compose.ui:ui:1.6.0")
    implementation("androidx.compose.material3:material3:1.2.0")
    implementation("androidx.activity:activity-compose:1.8.0")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.0")

    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Dependency Injection (optional but recommended)
    implementation("io.insert-koin:koin-androidx-compose:3.5.0")

    // Image Loading
    implementation("io.coil-kt:coil-compose:2.5.0")
}
```

#### React Native:
```json
// package.json
{
  "dependencies": {
    "react": "^18.2.0",
    "react-native": "^0.73.0",
    "@react-navigation/native": "^6.1.0",
    "@react-navigation/stack": "^6.3.0",
    "axios": "^1.6.0",
    "react-native-gesture-handler": "^2.14.0",
    "react-native-safe-area-context": "^4.8.0"
  }
}
```

---

## 5. Data Models

### 5.1 Kotlin Data Classes

```kotlin
// domain/model/Url.kt
data class Url(
    val id: Int,
    val url: String,
    val postedBy: String,
    val postedAt: String, // ISO 8601 timestamp
    val title: String?,
    val discordMessageId: String,
    val discordMessageLink: String?,
    val discordUserId: String?,
    val twitterUsername: String?,
    val categoryId: Int?,
    val episodeId: Int?,
    val status: UrlStatus?,
    val covered: Boolean,
    val displayOrder: Int,
    val createdAt: String,
    val updatedAt: String,
    val categoryName: String?,
    val episodeNumber: Int?,
    val isDuplicate: Boolean
)

// domain/model/UrlStatus.kt
enum class UrlStatus(val value: String) {
    ON_SHOW("on_show"),
    DUMP("dump");

    companion object {
        fun fromString(value: String?): UrlStatus? {
            return when (value) {
                "on_show" -> ON_SHOW
                "dump" -> DUMP
                else -> null
            }
        }
    }
}

// domain/model/Category.kt
data class Category(
    val id: Int,
    val name: String,
    val color: String, // Hex color "#RRGGBB"
    val createdAt: String,
    val updatedAt: String,
    val urlCount: Int? = null
)

// domain/model/LinkPreview.kt
data class LinkPreview(
    val title: String?,
    val description: String?,
    val imageUrl: String?
)

// data/remote/dto/PaginatedUrlResponse.kt
data class PaginatedUrlResponse(
    val urls: List<UrlDto>,
    val total: Int,
    val limit: Int,
    val offset: Int
)

// data/remote/dto/UrlDto.kt (matches API response exactly)
data class UrlDto(
    val id: Int,
    val url: String,
    val posted_by: String,
    val posted_at: String,
    val title: String?,
    val discord_message_id: String,
    val discord_message_link: String?,
    val discord_user_id: String?,
    val twitter_username: String?,
    val category_id: Int?,
    val episode_id: Int?,
    val status: String?,
    val covered: Boolean,
    val display_order: Int,
    val created_at: String,
    val updated_at: String,
    val category_name: String?,
    val episode_number: Int?,
    val is_duplicate: Boolean
) {
    // Extension function to convert to domain model
    fun toDomain(): Url = Url(
        id = id,
        url = url,
        postedBy = posted_by,
        postedAt = posted_at,
        title = title,
        discordMessageId = discord_message_id,
        discordMessageLink = discord_message_link,
        discordUserId = discord_user_id,
        twitterUsername = twitter_username,
        categoryId = category_id,
        episodeId = episode_id,
        status = UrlStatus.fromString(status),
        covered = covered,
        displayOrder = display_order,
        createdAt = created_at,
        updatedAt = updated_at,
        categoryName = category_name,
        episodeNumber = episode_number,
        isDuplicate = is_duplicate
    )
}

// data/remote/dto/CategoryDto.kt
data class CategoryDto(
    val id: Int,
    val name: String,
    val color: String,
    val created_at: String,
    val updated_at: String,
    val url_count: Int?
) {
    fun toDomain(): Category = Category(
        id = id,
        name = name,
        color = color,
        createdAt = created_at,
        updatedAt = updated_at,
        urlCount = url_count
    )
}

// data/remote/dto/UpdateUrlRequest.kt
data class UpdateUrlRequest(
    val category_id: Int?,
    val status: String?,
    val display_order: Int? = null,
    val covered: Boolean? = null
)

// data/remote/dto/BulkOperationRequest.kt
data class BulkOperationRequest(
    val url_ids: List<Int>,
    val operation: String, // "categorize" or "flag"
    val value: Any // Int for category_id, String for status
)

// data/remote/dto/CreateCategoryRequest.kt
data class CreateCategoryRequest(
    val name: String,
    val color: String
)

// data/remote/dto/ApiResponse.kt
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: String? = null,
    val message: String? = null
)
```

### 5.2 TypeScript Types (React Native)

```typescript
// types/Url.ts
export interface Url {
  id: number;
  url: string;
  posted_by: string;
  posted_at: string;
  title: string | null;
  discord_message_id: string;
  discord_message_link: string | null;
  discord_user_id: string | null;
  twitter_username: string | null;
  category_id: number | null;
  episode_id: number | null;
  status: 'on_show' | 'dump' | null;
  covered: boolean;
  display_order: number;
  created_at: string;
  updated_at: string;
  category_name: string | null;
  episode_number: number | null;
  is_duplicate: boolean;
}

export type UrlStatus = 'on_show' | 'dump';

// types/Category.ts
export interface Category {
  id: number;
  name: string;
  color: string;
  created_at: string;
  updated_at: string;
  url_count?: number;
}

// types/ApiResponse.ts
export interface PaginatedUrlResponse {
  urls: Url[];
  total: number;
  limit: number;
  offset: number;
}

export interface ApiError {
  success: false;
  error: string;
  message?: string;
}
```

---

## 6. Screen Specifications

### 6.1 Screen Flow Diagram

```
App Launch
    ↓
┌─────────────────────┐
│  Dashboard Screen   │ ← Main Entry Point (no auth)
│  (URL List)         │
└─────────────────────┘
    │
    ├──→ Tap URL ──→ ┌─────────────────────┐
    │                 │ URL Detail Screen   │
    │                 │ (Edit, Categorize)  │
    │                 └─────────────────────┘
    │
    ├──→ Filters ──→ ┌─────────────────────┐
    │                 │ Filter Bottom Sheet │
    │                 └─────────────────────┘
    │
    └──→ Categories ──→ ┌─────────────────────┐
                        │ Categories Screen   │
                        │ (List, Create, Edit)│
                        └─────────────────────┘
                            ↓
                        ┌─────────────────────┐
                        │ Create Category     │
                        │ (Dialog/Screen)     │
                        └─────────────────────┘
```

### 6.2 Dashboard Screen (Main Screen)

**Purpose:** Mobile-optimized URL list for quick browsing and categorization

#### Layout Components:

```
┌─────────────────────────────────────┐
│  🔍 [Search Bar]              [⋮]   │ ← Top Bar
├─────────────────────────────────────┤
│  [Filters Chips Row]                │ ← Horizontal scroll
│  All | On Show | Dump | Tech | ...  │
├─────────────────────────────────────┤
│                                     │
│  ┌───────────────────────────────┐ │
│  │ 🌐 Article Title Here         │ │
│  │ https://example.com/article   │ │
│  │ ─────────────────────────────│ │
│  │ Posted by: john_doe           │ │
│  │ 🏷️ Tech News    📅 2 hours ago│ │
│  │ [On Show] [Categorize]        │ │
│  └───────────────────────────────┘ │
│                                     │
│  ┌───────────────────────────────┐ │
│  │ 🌐 Another Article            │ │
│  │ ...                           │ │
│  └───────────────────────────────┘ │
│                                     │
│  [Load More...]                     │
│                                     │
└─────────────────────────────────────┘
```

#### Features:

1. **Search Bar (Top)**
   - Placeholder: "Search URLs..."
   - Real-time search with 500ms debounce
   - Searches in: title, URL, posted_by

2. **Menu Button (⋮)**
   - Tap to open: Categories, Settings, About

3. **Filter Chips (Horizontal Scroll)**
   - "All" (default, shows all URLs)
   - "On Show" (status filter)
   - "Dump" (status filter)
   - "Uncategorized" (category_id = 0)
   - Dynamic chips for each category (name + color)
   - Tap to toggle filter
   - Active chips have filled background

4. **URL Cards (Vertical List)**
   Each card shows:
   - **Favicon/Icon** (left side) - from URL or generic globe
   - **Title** (bold, 2 lines max with ellipsis)
   - **URL** (gray text, 1 line with ellipsis)
   - **Metadata Row:**
     - Posted by username (with user color dot)
     - Category chip (if categorized)
     - Time ago (e.g., "2 hours ago")
   - **Status Badge** (if set): "ON SHOW" (green) or "DUMP" (red)
   - **Duplicate Badge** (if is_duplicate)
   - **Quick Actions:**
     - "On Show" button (toggles status)
     - "Categorize" button (opens category picker)

5. **Pull-to-Refresh**
   - Standard Android pull gesture
   - Reloads first page of URLs

6. **Infinite Scroll / Load More**
   - Auto-load next page when scrolling near bottom
   - Or "Load More" button at bottom

7. **Empty State**
   - When no URLs: Show message "No URLs found" with illustration
   - When filters applied: "No URLs match filters"

#### State Management:

```kotlin
data class DashboardState(
    val urls: List<Url> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val selectedCategory: Int? = null, // null = all
    val selectedStatus: UrlStatus? = null, // null = all
    val showUncategorized: Boolean = false,
    val currentPage: Int = 0,
    val hasMore: Boolean = true,
    val totalCount: Int = 0
)
```

#### ViewModel Logic:

```kotlin
class DashboardViewModel(
    private val getUrlsUseCase: GetUrlsUseCase,
    private val updateUrlUseCase: UpdateUrlUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    fun loadUrls(refresh: Boolean = false) {
        viewModelScope.launch {
            if (refresh) {
                _state.update { it.copy(isRefreshing = true, currentPage = 0) }
            } else {
                _state.update { it.copy(isLoading = true) }
            }

            val result = getUrlsUseCase(
                limit = 50,
                offset = if (refresh) 0 else state.value.currentPage * 50,
                categoryId = state.value.selectedCategory,
                status = state.value.selectedStatus?.value,
                search = state.value.searchQuery.takeIf { it.isNotBlank() }
            )

            result.fold(
                onSuccess = { response ->
                    _state.update {
                        it.copy(
                            urls = if (refresh) response.urls else it.urls + response.urls,
                            totalCount = response.total,
                            hasMore = (it.currentPage + 1) * 50 < response.total,
                            currentPage = it.currentPage + 1,
                            isLoading = false,
                            isRefreshing = false,
                            error = null
                        )
                    }
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = error.message
                        )
                    }
                }
            )
        }
    }

    fun setSearchQuery(query: String) {
        _state.update { it.copy(searchQuery = query, currentPage = 0) }
        loadUrls(refresh = true)
    }

    fun setCategoryFilter(categoryId: Int?) {
        _state.update {
            it.copy(
                selectedCategory = categoryId,
                selectedStatus = null,
                showUncategorized = false,
                currentPage = 0
            )
        }
        loadUrls(refresh = true)
    }

    fun setStatusFilter(status: UrlStatus?) {
        _state.update {
            it.copy(
                selectedStatus = status,
                selectedCategory = null,
                showUncategorized = false,
                currentPage = 0
            )
        }
        loadUrls(refresh = true)
    }

    fun toggleUrlStatus(urlId: Int, currentStatus: UrlStatus?) {
        viewModelScope.launch {
            val newStatus = when (currentStatus) {
                UrlStatus.ON_SHOW -> null // Clear status
                else -> UrlStatus.ON_SHOW // Set to On Show
            }

            updateUrlUseCase(urlId, status = newStatus)
                .onSuccess { loadUrls(refresh = true) }
                .onFailure { /* Show error */ }
        }
    }
}
```

### 6.3 URL Detail Screen

**Purpose:** View full URL details and edit properties

#### Layout:

```
┌─────────────────────────────────────┐
│  ← Article Title              [⋮]   │ ← Top Bar
├─────────────────────────────────────┤
│                                     │
│  ┌─────────────────────────────┐   │
│  │   [Preview Image]           │   │ ← Link Preview
│  │                             │   │
│  │   Article Title             │   │
│  │   Brief description...      │   │
│  └─────────────────────────────┘   │
│                                     │
│  URL:                               │
│  https://example.com/long-url...    │
│  [Open in Browser]                  │
│                                     │
│  ─────────────────────────────────  │
│                                     │
│  Posted by: john_doe                │
│  Posted: Jan 15, 2025 at 12:00 PM   │
│  Discord: [View Message]            │
│                                     │
│  ─────────────────────────────────  │
│                                     │
│  Category:                          │
│  [Tech News ▼]                      │ ← Dropdown
│                                     │
│  Status:                            │
│  ○ None  ● On Show  ○ Dump          │ ← Radio Group
│                                     │
│  ─────────────────────────────────  │
│                                     │
│  [Save Changes]                     │
│                                     │
└─────────────────────────────────────┘
```

#### Features:

1. **Link Preview Card**
   - Fetched from `/api/link-preview?url=...&fetch=true`
   - Shows image, title, description
   - Cached for performance

2. **URL Info Section**
   - Full URL (scrollable if long)
   - "Open in Browser" button (opens in Android browser)
   - Posted by username
   - Posted timestamp (formatted: "Jan 15, 2025 at 12:00 PM")
   - Discord message link button

3. **Edit Section**
   - **Category Dropdown:** Lists all categories + "Uncategorized"
   - **Status Radio Group:** None / On Show / Dump
   - Changes auto-save on selection OR via "Save Changes" button

4. **Menu (⋮)**
   - Copy URL
   - Share URL (Android share sheet)
   - View on Discord (if discord_message_link exists)

#### State:

```kotlin
data class UrlDetailState(
    val url: Url? = null,
    val linkPreview: LinkPreview? = null,
    val categories: List<Category> = emptyList(),
    val selectedCategoryId: Int? = null,
    val selectedStatus: UrlStatus? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null
)
```

### 6.4 Categories Screen

**Purpose:** Manage categories (view, create, edit, delete)

#### Layout:

```
┌─────────────────────────────────────┐
│  Categories                   [+]   │ ← Top Bar
├─────────────────────────────────────┤
│                                     │
│  ┌─────────────────────────────┐   │
│  │ ● Tech News         (25)    │   │ ← Color dot, name, count
│  │                    [Edit]   │   │
│  └─────────────────────────────┘   │
│                                     │
│  ┌─────────────────────────────┐   │
│  │ ● Politics          (12)    │   │
│  │                    [Edit]   │   │
│  └─────────────────────────────┘   │
│                                     │
│  ┌─────────────────────────────┐   │
│  │ ● Science           (8)     │   │
│  │                    [Edit]   │   │
│  └─────────────────────────────┘   │
│                                     │
└─────────────────────────────────────┘
```

#### Features:

1. **Category List**
   - Each card shows:
     - Color indicator (filled circle)
     - Category name
     - URL count in parentheses
     - Edit button

2. **Add Category (+)**
   - Opens "Create Category" dialog/bottom sheet

3. **Edit Category**
   - Opens "Edit Category" dialog with:
     - Name input field
     - Color picker (grid of preset colors)
     - Save button
     - Delete button (with confirmation)

#### Create/Edit Category Dialog:

```
┌─────────────────────────────────────┐
│  Create Category              [×]   │
├─────────────────────────────────────┤
│                                     │
│  Name:                              │
│  [___________________________]      │
│                                     │
│  Color:                             │
│  ┌─────────────────────────────┐   │
│  │ ●  ●  ●  ●  ●  ●  ●  ●  ● │   │ ← Color grid
│  │ ●  ●  ●  ●  ●  ●  ●  ●  ● │   │
│  └─────────────────────────────┘   │
│                                     │
│  Preview:                           │
│  ┌─────────────────────────────┐   │
│  │ ● Category Name             │   │
│  └─────────────────────────────┘   │
│                                     │
│         [Cancel]    [Create]        │
│                                     │
└─────────────────────────────────────┘
```

#### Preset Colors:

```kotlin
val PRESET_COLORS = listOf(
    "#3498db", // Blue
    "#e74c3c", // Red
    "#2ecc71", // Green
    "#f39c12", // Orange
    "#9b59b6", // Purple
    "#1abc9c", // Teal
    "#e67e22", // Dark Orange
    "#34495e", // Dark Gray
    "#16a085", // Dark Teal
    "#27ae60", // Dark Green
    "#2980b9", // Dark Blue
    "#8e44ad", // Dark Purple
    "#c0392b", // Dark Red
    "#d35400", // Pumpkin
    "#7f8c8d", // Gray
    "#95a5a6", // Light Gray
    "#f1c40f", // Yellow
    "#e91e63", // Pink
)
```

### 6.5 Filter Bottom Sheet

**Purpose:** Advanced filtering options

#### Layout:

```
┌─────────────────────────────────────┐
│  ═══                                │ ← Drag handle
│  Filters                            │
├─────────────────────────────────────┤
│                                     │
│  Category:                          │
│  ○ All Categories                   │
│  ○ Uncategorized                    │
│  ○ Tech News                        │
│  ○ Politics                         │
│  ○ Science                          │
│                                     │
│  Status:                            │
│  ○ All                              │
│  ○ On Show                          │
│  ○ Dump                             │
│  ○ None                             │
│                                     │
│  [Clear Filters]    [Apply]         │
│                                     │
└─────────────────────────────────────┘
```

---

## 7. UI/UX Design Guidelines

### 7.1 Color Scheme (Dark Theme - Primary)

Based on the web app's dark mode CSS variables:

```kotlin
// theme/Color.kt
object StagehandColors {
    // Dark Theme (Primary)
    val BackgroundPrimary = Color(0xFF1A1A1A)      // --bg-primary
    val BackgroundSecondary = Color(0xFF2D2D2D)    // --bg-secondary
    val TextPrimary = Color(0xFFE0E0E0)            // --text-primary
    val TextSecondary = Color(0xFFA0A0A0)          // --text-secondary
    val BorderColor = Color(0xFF404040)            // --border-color
    val CardBackground = Color(0xFF2D2D2D)         // --card-bg
    val InputBackground = Color(0xFF3A3A3A)        // --input-bg
    val AccentColor = Color(0xFF7289DA)            // --accent-color (Discord purple)
    val LinkColor = Color(0xFF66B3FF)              // --link-color

    // Light Theme (Optional for future)
    val LightBackgroundPrimary = Color(0xFFF5F5F5)
    val LightBackgroundSecondary = Color(0xFFFFFFFF)
    val LightTextPrimary = Color(0xFF333333)
    val LightTextSecondary = Color(0xFF666666)

    // Status Colors
    val OnShowGreen = Color(0xFF57F287)
    val DumpRed = Color(0xFFED4245)
    val DuplicateOrange = Color(0xFFFAA61A)

    // Button Colors
    val ButtonPrimary = Color(0xFF5865F2)          // Discord blurple
    val ButtonSuccess = Color(0xFF57F287)
    val ButtonDanger = Color(0xFFED4245)
    val ButtonSecondary = Color(0xFF4F545C)
}
```

### 7.2 Typography

```kotlin
// theme/Type.kt
val StagehandTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    )
)
```

### 7.3 Spacing & Dimensions

```kotlin
// theme/Dimensions.kt
object Spacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 16.dp
    val lg = 24.dp
    val xl = 32.dp
    val xxl = 48.dp
}

object CornerRadius {
    val small = 4.dp
    val medium = 8.dp
    val large = 12.dp
}

object Elevation {
    val none = 0.dp
    val low = 2.dp
    val medium = 4.dp
    val high = 8.dp
}
```

### 7.4 Component Design Guidelines

#### URL Card:
- **Background:** CardBackground (#2D2D2D)
- **Border:** 1dp solid BorderColor (#404040)
- **Border Radius:** 8dp
- **Padding:** 16dp
- **Margin Bottom:** 12dp
- **Elevation:** 2dp shadow
- **Ripple Effect:** On tap
- **Duplicate Indicator:** 4dp left border in DuplicateOrange

#### Category Chip:
- **Background:** Category color at 20% opacity
- **Text Color:** Category color (full opacity)
- **Border:** 1dp solid category color
- **Border Radius:** 16dp (pill shape)
- **Padding:** 6dp horizontal, 4dp vertical
- **Font Size:** 12sp
- **Font Weight:** Medium

#### Status Badge:
- **On Show:**
  - Background: OnShowGreen (#57F287)
  - Text: Dark gray (#333333)
- **Dump:**
  - Background: DumpRed (#ED4245)
  - Text: White
- **Common:**
  - Border Radius: 4dp
  - Padding: 4dp horizontal, 2dp vertical
  - Font Size: 10sp
  - Font Weight: Bold
  - Text Transform: Uppercase

#### Buttons:
- **Height:** 48dp (standard), 40dp (small)
- **Border Radius:** 8dp
- **Elevation:** 2dp (raised)
- **Ripple:** AccentColor at 30% opacity
- **Text:** All caps, 14sp, medium weight

### 7.5 Animations

- **Screen Transitions:** 300ms slide/fade
- **Card Ripple:** 200ms Material ripple
- **Pull-to-Refresh:** Standard Android animation
- **Loading Spinners:** Indeterminate circular (AccentColor)
- **Button Press:** Scale down to 0.95 (100ms)

---

## 8. Implementation Guide

### 8.1 API Service Implementation (Kotlin)

```kotlin
// data/remote/api/StagehandApi.kt
interface StagehandApi {

    @GET("urls")
    suspend fun getUrls(
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0,
        @Query("category_id") categoryId: Int? = null,
        @Query("status") status: String? = null,
        @Query("search") search: String? = null,
        @Query("covered") covered: Boolean? = null,
        @Query("posted_by") postedBy: String? = null,
        @Query("sort") sort: String = "desc"
    ): PaginatedUrlResponse

    @GET("urls/{id}")
    suspend fun getUrl(@Path("id") id: Int): UrlDto

    @PUT("urls/{id}")
    suspend fun updateUrl(
        @Path("id") id: Int,
        @Body request: UpdateUrlRequest
    ): UrlDto

    @POST("urls/bulk")
    suspend fun bulkOperation(
        @Body request: BulkOperationRequest
    ): BulkOperationResponse

    @GET("categories")
    suspend fun getCategories(): List<CategoryDto>

    @GET("categories/{id}")
    suspend fun getCategory(@Path("id") id: Int): CategoryDto

    @POST("categories")
    suspend fun createCategory(
        @Body request: CreateCategoryRequest
    ): CategoryDto

    @PUT("categories/{id}")
    suspend fun updateCategory(
        @Path("id") id: Int,
        @Body request: UpdateCategoryRequest
    ): CategoryDto

    @GET("link-preview")
    suspend fun getLinkPreview(
        @Query("url") url: String,
        @Query("fetch") fetch: Boolean = true
    ): LinkPreviewDto

    @GET("version")
    suspend fun getVersion(): VersionDto
}

// data/remote/NetworkModule.kt
object NetworkModule {

    private const val BASE_URL = "http://10.0.2.2:5001/api/" // Development
    // Production: "https://your-domain.com/api/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: StagehandApi = retrofit.create(StagehandApi::class.java)
}
```

### 8.2 Repository Implementation

```kotlin
// data/repository/UrlRepository.kt
class UrlRepository(private val api: StagehandApi) {

    suspend fun getUrls(
        limit: Int = 50,
        offset: Int = 0,
        categoryId: Int? = null,
        status: String? = null,
        search: String? = null
    ): Result<PaginatedUrlResponse> {
        return try {
            val response = api.getUrls(
                limit = limit,
                offset = offset,
                categoryId = categoryId,
                status = status,
                search = search
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUrl(
        id: Int,
        categoryId: Int? = null,
        status: String? = null,
        covered: Boolean? = null
    ): Result<UrlDto> {
        return try {
            val request = UpdateUrlRequest(
                category_id = categoryId,
                status = status,
                covered = covered
            )
            val response = api.updateUrl(id, request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun bulkCategorize(
        urlIds: List<Int>,
        categoryId: Int?
    ): Result<BulkOperationResponse> {
        return try {
            val request = BulkOperationRequest(
                url_ids = urlIds,
                operation = "categorize",
                value = categoryId ?: 0
            )
            val response = api.bulkOperation(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// data/repository/CategoryRepository.kt
class CategoryRepository(private val api: StagehandApi) {

    suspend fun getCategories(): Result<List<Category>> {
        return try {
            val response = api.getCategories()
            val categories = response.map { it.toDomain() }
            Result.success(categories)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createCategory(
        name: String,
        color: String
    ): Result<Category> {
        return try {
            val request = CreateCategoryRequest(name, color)
            val response = api.createCategory(request)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateCategory(
        id: Int,
        name: String,
        color: String
    ): Result<Category> {
        return try {
            val request = UpdateCategoryRequest(name, color)
            val response = api.updateCategory(id, request)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

### 8.3 Use Case Implementation

```kotlin
// domain/usecase/GetUrlsUseCase.kt
class GetUrlsUseCase(private val repository: UrlRepository) {

    suspend operator fun invoke(
        limit: Int = 50,
        offset: Int = 0,
        categoryId: Int? = null,
        status: String? = null,
        search: String? = null
    ): Result<PaginatedUrlResponse> {
        return repository.getUrls(
            limit = limit,
            offset = offset,
            categoryId = categoryId,
            status = status,
            search = search
        )
    }
}

// domain/usecase/UpdateUrlUseCase.kt
class UpdateUrlUseCase(private val repository: UrlRepository) {

    suspend operator fun invoke(
        id: Int,
        categoryId: Int? = null,
        status: UrlStatus? = null,
        covered: Boolean? = null
    ): Result<Url> {
        return repository.updateUrl(
            id = id,
            categoryId = categoryId,
            status = status?.value,
            covered = covered
        ).map { it.toDomain() }
    }
}
```

### 8.4 Composable UI Components (Jetpack Compose)

```kotlin
// presentation/components/UrlCard.kt
@Composable
fun UrlCard(
    url: Url,
    onCardClick: (Int) -> Unit,
    onStatusToggle: (Int, UrlStatus?) -> Unit,
    onCategorizeClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onCardClick(url.id) },
        colors = CardDefaults.cardColors(
            containerColor = StagehandColors.CardBackground
        ),
        border = BorderStroke(
            width = if (url.isDuplicate) 4.dp else 1.dp,
            color = if (url.isDuplicate) {
                StagehandColors.DuplicateOrange
            } else {
                StagehandColors.BorderColor
            }
        ),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Title
            Text(
                text = url.title ?: "No Title",
                style = MaterialTheme.typography.titleMedium,
                color = StagehandColors.TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // URL
            Text(
                text = url.url,
                style = MaterialTheme.typography.bodySmall,
                color = StagehandColors.TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Metadata Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Posted by
                Text(
                    text = "Posted by: ${url.postedBy}",
                    style = MaterialTheme.typography.bodySmall,
                    color = StagehandColors.TextSecondary
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Category chip
                url.categoryName?.let { categoryName ->
                    CategoryChip(
                        name = categoryName,
                        color = url.categoryId?.let { /* Get color from categories list */ } ?: "#808080"
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Time ago
                Text(
                    text = formatTimeAgo(url.postedAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = StagehandColors.TextSecondary
                )
            }

            // Status & Duplicate Badges
            if (url.status != null || url.isDuplicate) {
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    url.status?.let { status ->
                        StatusBadge(status = status)
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    if (url.isDuplicate) {
                        DuplicateBadge()
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onStatusToggle(url.id, url.status) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (url.status == UrlStatus.ON_SHOW) {
                            StagehandColors.OnShowGreen
                        } else {
                            StagehandColors.ButtonSecondary
                        }
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = if (url.status == UrlStatus.ON_SHOW) "On Show ✓" else "On Show",
                        color = if (url.status == UrlStatus.ON_SHOW) Color.Black else Color.White
                    )
                }

                OutlinedButton(
                    onClick = { onCategorizeClick(url.id) },
                    modifier = Modifier.weight(1f),
                    border = BorderStroke(1.dp, StagehandColors.AccentColor)
                ) {
                    Text(
                        text = "Categorize",
                        color = StagehandColors.AccentColor
                    )
                }
            }
        }
    }
}

// presentation/components/CategoryChip.kt
@Composable
fun CategoryChip(
    name: String,
    color: String,
    modifier: Modifier = Modifier
) {
    val categoryColor = try {
        Color(android.graphics.Color.parseColor(color))
    } catch (e: Exception) {
        Color.Gray
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = categoryColor.copy(alpha = 0.2f),
        border = BorderStroke(1.dp, categoryColor)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Canvas(modifier = Modifier.size(8.dp)) {
                drawCircle(color = categoryColor)
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.labelSmall,
                color = categoryColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// presentation/components/StatusBadge.kt
@Composable
fun StatusBadge(status: UrlStatus) {
    val (bgColor, textColor, text) = when (status) {
        UrlStatus.ON_SHOW -> Triple(
            StagehandColors.OnShowGreen,
            Color.Black,
            "ON SHOW"
        )
        UrlStatus.DUMP -> Triple(
            StagehandColors.DumpRed,
            Color.White,
            "DUMP"
        )
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.Bold
        )
    }
}
```

### 8.5 Date Formatting Utility

```kotlin
// util/DateFormatter.kt
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object DateFormatter {

    fun formatTimeAgo(isoTimestamp: String): String {
        return try {
            val instant = Instant.parse(isoTimestamp)
            val now = Instant.now()

            val minutes = ChronoUnit.MINUTES.between(instant, now)
            val hours = ChronoUnit.HOURS.between(instant, now)
            val days = ChronoUnit.DAYS.between(instant, now)

            when {
                minutes < 1 -> "Just now"
                minutes < 60 -> "${minutes}m ago"
                hours < 24 -> "${hours}h ago"
                days < 7 -> "${days}d ago"
                else -> {
                    val formatter = DateTimeFormatter.ofPattern("MMM d")
                        .withZone(ZoneId.systemDefault())
                    formatter.format(instant)
                }
            }
        } catch (e: Exception) {
            "Unknown"
        }
    }

    fun formatFullDate(isoTimestamp: String): String {
        return try {
            val instant = Instant.parse(isoTimestamp)
            val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a")
                .withZone(ZoneId.systemDefault())
            formatter.format(instant)
        } catch (e: Exception) {
            isoTimestamp
        }
    }
}
```

### 8.6 Navigation Setup

```kotlin
// presentation/navigation/Navigation.kt
sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object UrlDetail : Screen("url/{urlId}") {
        fun createRoute(urlId: Int) = "url/$urlId"
    }
    object Categories : Screen("categories")
    object CreateCategory : Screen("create_category")
    object EditCategory : Screen("edit_category/{categoryId}") {
        fun createRoute(categoryId: Int) = "edit_category/$categoryId"
    }
}

@Composable
fun StagehandNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onUrlClick = { urlId ->
                    navController.navigate(Screen.UrlDetail.createRoute(urlId))
                },
                onCategoriesClick = {
                    navController.navigate(Screen.Categories.route)
                }
            )
        }

        composable(
            route = Screen.UrlDetail.route,
            arguments = listOf(
                navArgument("urlId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val urlId = backStackEntry.arguments?.getInt("urlId") ?: return@composable
            UrlDetailScreen(
                urlId = urlId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Categories.route) {
            CategoriesScreen(
                onBackClick = { navController.popBackStack() },
                onCreateClick = {
                    navController.navigate(Screen.CreateCategory.route)
                }
            )
        }

        // Add more composables...
    }
}
```

### 8.7 Dependency Injection (Koin)

```kotlin
// di/AppModule.kt
val appModule = module {

    // Network
    single { NetworkModule.api }

    // Repositories
    single { UrlRepository(get()) }
    single { CategoryRepository(get()) }

    // Use Cases
    factory { GetUrlsUseCase(get()) }
    factory { UpdateUrlUseCase(get()) }
    factory { GetCategoriesUseCase(get()) }
    factory { CreateCategoryUseCase(get()) }

    // ViewModels
    viewModel { DashboardViewModel(get(), get()) }
    viewModel { (urlId: Int) -> UrlDetailViewModel(urlId, get(), get(), get()) }
    viewModel { CategoriesViewModel(get(), get(), get()) }
}

// StagehandApplication.kt
class StagehandApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@StagehandApplication)
            modules(appModule)
        }
    }
}
```

---

## 9. Testing Strategy

### 9.1 Unit Tests

**Test Coverage:**
- Data models (DTO to domain conversion)
- ViewModels (state transitions)
- Use cases (business logic)
- Utilities (date formatting, color parsing)

**Example:**
```kotlin
class DashboardViewModelTest {

    @Test
    fun `loadUrls should update state with fetched URLs`() = runTest {
        // Given
        val mockUrls = listOf(/* mock data */)
        val mockUseCase = mockk<GetUrlsUseCase>()
        coEvery { mockUseCase(any(), any(), any(), any(), any()) } returns Result.success(
            PaginatedUrlResponse(mockUrls, 100, 50, 0)
        )
        val viewModel = DashboardViewModel(mockUseCase, mockk())

        // When
        viewModel.loadUrls()

        // Then
        assertEquals(mockUrls, viewModel.state.value.urls)
        assertEquals(100, viewModel.state.value.totalCount)
        assertFalse(viewModel.state.value.isLoading)
    }
}
```

### 9.2 Integration Tests

**Test Coverage:**
- API service calls
- Repository implementations
- End-to-end data flow

### 9.3 UI Tests (Compose)

**Test Coverage:**
- Screen rendering
- User interactions
- Navigation flow

**Example:**
```kotlin
@Test
fun dashboard_displaysUrls() {
    composeTestRule.setContent {
        DashboardScreen(/* mock dependencies */)
    }

    composeTestRule.onNodeWithText("Example Article").assertIsDisplayed()
    composeTestRule.onNodeWithText("On Show").assertExists()
}
```

### 9.4 Manual Testing Checklist

- [ ] URL list loads and displays correctly
- [ ] Search functionality works with debounce
- [ ] Filters apply correctly
- [ ] URL detail screen shows all information
- [ ] Category assignment updates URL
- [ ] Status toggle works (none → on_show → none)
- [ ] Bulk categorization works
- [ ] Create new category
- [ ] Edit existing category
- [ ] Delete category (with confirmation)
- [ ] Pull-to-refresh updates data
- [ ] Infinite scroll/load more works
- [ ] Error states display properly
- [ ] Loading states show spinners
- [ ] Network errors handled gracefully
- [ ] Deep linking works (if implemented)
- [ ] Back button navigation works correctly
- [ ] App works on different screen sizes
- [ ] Dark theme displays correctly
- [ ] Landscape orientation (if supported)

---

## 10. Deployment Checklist

### 10.1 Pre-Build Configuration

1. **Update API Base URL**
   ```kotlin
   // NetworkModule.kt
   private const val BASE_URL = "https://your-production-domain.com/api/"
   ```

2. **Set App Icon**
   - Convert `src/frontend/assets/apple-touch-icon.png` to Android launcher icons
   - Use Android Studio Image Asset Studio
   - Generate all density variants (mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi)

3. **Configure App Name & Version**
   ```xml
   <!-- res/values/strings.xml -->
   <string name="app_name">Stagehand</string>
   ```

   ```kotlin
   // build.gradle.kts
   android {
       defaultConfig {
           applicationId = "com.yourname.stagehand"
           versionCode = 1
           versionName = "1.0.0"
       }
   }
   ```

4. **Configure ProGuard (Release Build)**
   ```proguard
   # Retrofit
   -keepattributes Signature
   -keepattributes *Annotation*
   -keep class retrofit2.** { *; }

   # Gson
   -keep class com.yourname.stagehand.data.remote.dto.** { *; }
   ```

### 10.2 Build APK

**For Kotlin/Native:**
```bash
# Debug APK
./gradlew assembleDebug

# Release APK (signed)
./gradlew assembleRelease
```

**For React Native:**
```bash
cd android
./gradlew assembleRelease
```

### 10.3 Signing Configuration

1. **Generate Keystore**
   ```bash
   keytool -genkey -v -keystore stagehand-release.keystore \
     -alias stagehand -keyalg RSA -keysize 2048 -validity 10000
   ```

2. **Configure Gradle Signing**
   ```kotlin
   // build.gradle.kts
   android {
       signingConfigs {
           create("release") {
               storeFile = file("../stagehand-release.keystore")
               storePassword = System.getenv("KEYSTORE_PASSWORD")
               keyAlias = "stagehand"
               keyPassword = System.getenv("KEY_PASSWORD")
           }
       }
       buildTypes {
           release {
               signingConfig = signingConfigs.getByName("release")
               isMinifyEnabled = true
               proguardFiles(...)
           }
       }
   }
   ```

### 10.4 Distribution

**Private APK Distribution:**
1. Build signed release APK
2. Locate APK: `app/build/outputs/apk/release/app-release.apk`
3. Distribute via:
   - Email attachment
   - Cloud storage (Dropbox, Google Drive)
   - Internal company portal
   - Firebase App Distribution (recommended)

**Firebase App Distribution Setup:**
```bash
# Install Firebase CLI
npm install -g firebase-tools

# Login and initialize
firebase login
firebase init appdistribution

# Upload APK
firebase appdistribution:distribute \
  app/build/outputs/apk/release/app-release.apk \
  --app YOUR_FIREBASE_APP_ID \
  --groups "producers"
```

### 10.5 Post-Deployment Verification

- [ ] APK installs successfully on test devices
- [ ] App connects to production API
- [ ] All features work in production environment
- [ ] No crashes on app launch
- [ ] Network requests complete successfully
- [ ] Error handling works with production errors
- [ ] App icon displays correctly
- [ ] App name is correct in launcher

---

## 11. Future Enhancements

### 11.1 Phase 2 Features (Post-MVP)

1. **User Authentication**
   - Implement username/password login
   - 30-day session persistence
   - User profile screen
   - Logout functionality

2. **Episode Management**
   - View episodes list
   - Assign URLs to episodes
   - View episode details (without show notes editing)

3. **Push Notifications**
   - Firebase Cloud Messaging integration
   - Notifications for new URLs scraped
   - Notifications for scrape completion

4. **Advanced Filtering**
   - Date range filters
   - Multi-select categories
   - Save filter presets

5. **Offline Support**
   - Local database caching (Room)
   - Offline queue for actions
   - Sync when back online

### 11.2 Phase 3 Features

1. **Leaderboard**
   - User statistics
   - Time-based filtering
   - Sortable columns

2. **URL Submission**
   - Manual URL submission from mobile
   - Link preview before submission

3. **Widgets**
   - Home screen widget for stats
   - Quick actions widget

4. **iOS Version**
   - SwiftUI native app
   - Or React Native cross-platform

5. **Dark/Light Theme Toggle**
   - User preference
   - System theme detection

---

## 12. Backend Migration Notes (Future Username/Password Auth)

### 12.1 Database Changes

The migration file `009_add_user_authentication_fields.sql` has been created and adds:
- `password_hash` column (VARCHAR 255, nullable)
- `email` column (VARCHAR 255, nullable)
- `is_active` column (BOOLEAN, default true)
- `role` column (VARCHAR 50, default 'producer')
- `last_login` column (TIMESTAMP, nullable)

**Roles:**
- `admin` - Full access (web app)
- `producer` - Can categorize, create categories (mobile app)
- `co_producer` - Can categorize (mobile app)
- `viewer` - Read-only access

### 12.2 Auth Endpoints to Implement

#### POST /api/auth/login-user
**Request:**
```json
{
  "username": "john_producer",
  "password": "password123"
}
```

**Response:**
```json
{
  "success": true,
  "token": "session_token_here",
  "user": {
    "id": 1,
    "username": "john_producer",
    "email": "john@example.com",
    "role": "producer"
  }
}
```

#### POST /api/auth/register (Admin only)
**Request:**
```json
{
  "username": "new_producer",
  "email": "producer@example.com",
  "password": "password123",
  "role": "producer"
}
```

### 12.3 Mobile App Changes Required

1. **Add Login Screen**
   - Username/password input
   - "Remember Me" checkbox
   - Login button

2. **Token Storage**
   - Use EncryptedSharedPreferences (Android)
   - Store token securely
   - Store user info (username, role)

3. **Add Auth Interceptor**
   ```kotlin
   class AuthInterceptor(
       private val tokenManager: TokenManager
   ) : Interceptor {
       override fun intercept(chain: Interceptor.Chain): Response {
           val token = tokenManager.getToken()
           val request = if (token != null) {
               chain.request().newBuilder()
                   .addHeader("Authorization", "Bearer $token")
                   .build()
           } else {
               chain.request()
           }
           return chain.proceed(request)
       }
   }
   ```

4. **Add Token Refresh Logic**
   - Check token expiry
   - Auto-logout on 30-day expiration
   - Optional: Implement refresh tokens

5. **Update UI**
   - Add user info to app bar
   - Add logout option
   - Show username in settings

---

## 13. Additional Resources

### 13.1 Logo Assets

**Location in Repository:**
- App Icon: `src/frontend/assets/apple-touch-icon.png`
- Logo: `src/frontend/assets/logo.png`
- Banner: `src/frontend/assets/banner.png`

**Usage:**
- Convert PNG to Android XML drawable for vector graphics
- Or use as-is for bitmap launcher icons

### 13.2 API Documentation

**Base URL:** `http://your-server:5001/api`

**Full Endpoint Reference:** See Section 3

**Testing API:**
```bash
# Get URLs
curl http://localhost:5001/api/urls?limit=10

# Get categories
curl http://localhost:5001/api/categories

# Create category
curl -X POST http://localhost:5001/api/categories \
  -H "Content-Type: application/json" \
  -d '{"name":"New Category","color":"#3498db"}'
```

### 13.3 Development Tools

**Recommended:**
- **Android Studio**: Latest stable (Hedgehog or newer)
- **Postman/Insomnia**: API testing
- **ADB**: Android Debug Bridge for device testing
- **Scrcpy**: Screen mirroring for testing

**Useful Libraries:**
- **Timber**: Better logging
- **LeakCanary**: Memory leak detection
- **Chucker**: Network inspector

---

## 14. Contact & Support

For questions about the backend API or this specification:
- Review the main repository README at `d:\Github\stagehand\README.md`
- Check backend code in `src/backend/`
- Review migrations in `migrations/`

---

## Appendix A: Quick Start Checklist

### For New Android Developer:

- [ ] Read Executive Summary (Section 1)
- [ ] Review Backend API Reference (Section 3)
- [ ] Understand Data Models (Section 5)
- [ ] Review Screen Specifications (Section 6)
- [ ] Choose technology stack (Kotlin/React Native/Flutter)
- [ ] Set up development environment
- [ ] Create new Android project
- [ ] Implement NetworkModule with API service
- [ ] Implement data models and DTOs
- [ ] Build Dashboard screen (core feature)
- [ ] Implement URL detail screen
- [ ] Build Categories screen
- [ ] Add navigation
- [ ] Style with Stagehand color scheme
- [ ] Test on emulator
- [ ] Test on physical device
- [ ] Build release APK
- [ ] Distribute to producers

### Total Estimated Development Time:

**For Experienced Android Developer:**
- **Native Kotlin/Compose**: 2-3 weeks
- **React Native**: 2-3 weeks (if familiar with RN)
- **Flutter**: 3-4 weeks (if familiar with Flutter)

**For New Android Developer:**
- **Any Stack**: 4-6 weeks (including learning)

---

## Appendix B: Environment Variables

### Backend (.env file):

```env
# Required
DISCORD_BOT_TOKEN=your_token
POSTGRES_HOST=postgres
POSTGRES_PORT=5432
POSTGRES_DB=stagehand
POSTGRES_USER=stagehand
POSTGRES_PASSWORD=your_password
ADMIN_PASSWORD=your_admin_password
PORT=5001

# Optional
LINKPREVIEW_API_KEY=your_key
DISCORD_WEBHOOK_URL=your_webhook
```

### Android App (build.gradle.kts):

```kotlin
android {
    defaultConfig {
        buildConfigField("String", "API_BASE_URL", "\"http://10.0.2.2:5001/api/\"")
    }

    buildTypes {
        release {
            buildConfigField("String", "API_BASE_URL", "\"https://your-domain.com/api/\"")
        }
    }
}
```

---

## Document Version History

| Version | Date | Changes |
|---------|------|---------|
| 1.0 | 2025-11-08 | Initial specification created |

---

**End of Specification Document**

This document contains everything needed to build the Stagehand Android mobile app without access to the original codebase. All API endpoints, data models, UI specifications, and implementation guidance are included.
