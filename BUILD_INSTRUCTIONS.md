# Stagehand Android App - Build Instructions

## Prerequisites

### Required Software
1. **Android Studio** (Hedgehog 2023.1.1 or newer)
   - Download from: https://developer.android.com/studio
   - Install with default settings

2. **JDK 17** (Usually bundled with Android Studio)
   - Verify: Open Terminal and run `java -version`
   - Should show version 17 or higher

### System Requirements
- **OS**: Windows 10/11, macOS 10.14+, or Linux
- **RAM**: 8GB minimum (16GB recommended)
- **Disk Space**: 10GB free space
- **Internet**: Required for downloading dependencies

## Setup Steps

### 1. Open Project in Android Studio

1. Launch Android Studio
2. Click **"Open"** (not "New Project")
3. Navigate to `d:\Github\stagehand-app`
4. Click **OK**

Android Studio will automatically:
- Download Gradle wrapper
- Sync Gradle dependencies
- Index the project

**First sync takes 5-15 minutes** depending on internet speed.

### 2. Verify Configuration

Once sync completes, verify:

- ✅ No red errors in the code
- ✅ Gradle sync successful (check bottom status bar)
- ✅ Build variants show "debug" and "release"

### 3. Set Up Android SDK

Android Studio should auto-install SDK 34. If needed:

1. **File → Settings** (Windows/Linux) or **Android Studio → Preferences** (Mac)
2. **Appearance & Behavior → System Settings → Android SDK**
3. Check **Android 14.0 (API 34)** is installed
4. Click **Apply** if you need to install it

### 4. Create App Icons (Important!)

The app needs launcher icons. You have two options:

#### Option A: Use Android Studio's Image Asset Studio (Recommended)

1. In Android Studio, right-click `app/src/main/res`
2. Select **New → Image Asset**
3. **Icon Type**: Launcher Icons (Adaptive and Legacy)
4. **Foreground Layer**:
   - **Source Asset**: Image
   - Click **Path** folder icon
   - Navigate to and select `d:\Github\stagehand-app\apple-touch-icon.png`
5. **Background Layer**: Use default or solid color `#1A1A1A`
6. Click **Next**, then **Finish**

This creates all required icon densities automatically.

#### Option B: Manual Icon Placement

If Image Asset Studio isn't available, you'll need to manually create icons in these sizes and place them in the respective folders:

```
app/src/main/res/
├── mipmap-mdpi/ic_launcher.png (48x48)
├── mipmap-hdpi/ic_launcher.png (72x72)
├── mipmap-xhdpi/ic_launcher.png (96x96)
├── mipmap-xxhdpi/ic_launcher.png (144x144)
└── mipmap-xxxhdpi/ic_launcher.png (192x192)
```

Use an image resizing tool or online service to create these from `apple-touch-icon.png`.

## Building the App

### Debug Build (For Testing)

#### Using Android Studio UI:
1. Click **Build → Build Bundle(s) / APK(s) → Build APK(s)**
2. Wait for build to complete (2-5 minutes first time)
3. Click **"locate"** in the notification that appears
4. APK will be at: `app/build/outputs/apk/debug/app-debug.apk`

#### Using Terminal:
```bash
cd d:\Github\stagehand-app
gradlew.bat assembleDebug
```

### Release Build (For Distribution)

#### Step 1: Create Signing Key (One-time setup)

Open Terminal in project root and run:

```bash
keytool -genkey -v -keystore stagehand-release.keystore -alias stagehand -keyalg RSA -keysize 2048 -validity 10000
```

You'll be prompted for:
- **Keystore password**: Choose a strong password (SAVE THIS!)
- **Re-enter password**: Enter same password
- **First and last name**: Your name or app name
- **Organizational unit**: (Optional, press Enter to skip)
- **Organization**: (Optional, press Enter to skip)
- **City/Locality**: (Optional, press Enter to skip)
- **State/Province**: (Optional, press Enter to skip)
- **Country code**: (Optional, press Enter to skip)
- **Correct?**: Type `yes`
- **Key password**: Press Enter to use same as keystore password

This creates `stagehand-release.keystore` in your project root.

**IMPORTANT**: Backup this keystore file and remember the password!

#### Step 2: Configure Signing

Create a file named `keystore.properties` in the project root:

```properties
storePassword=YOUR_KEYSTORE_PASSWORD
keyPassword=YOUR_KEY_PASSWORD
keyAlias=stagehand
storeFile=../stagehand-release.keystore
```

Replace `YOUR_KEYSTORE_PASSWORD` and `YOUR_KEY_PASSWORD` with your actual passwords.

Add this to `.gitignore` to keep passwords secret:
```
keystore.properties
stagehand-release.keystore
```

#### Step 3: Update build.gradle.kts

Add this code to `app/build.gradle.kts` just after the `android {` line:

```kotlin
// Load keystore properties
val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = java.util.Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(java.io.FileInputStream(keystorePropertiesFile))
}
```

Then add this inside `android { }` block:

```kotlin
signingConfigs {
    create("release") {
        keyAlias = keystoreProperties["keyAlias"] as String
        keyPassword = keystoreProperties["keyPassword"] as String
        storeFile = file(keystoreProperties["storeFile"] as String)
        storePassword = keystoreProperties["storePassword"] as String
    }
}
```

And update the `buildTypes` section:

```kotlin
buildTypes {
    release {
        signingConfig = signingConfigs.getByName("release")
        isMinifyEnabled = true
        proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )
        buildConfigField("String", "API_BASE_URL", "\"https://stagehand.theprestream.com/api/\"")
    }
    // ... rest of buildTypes
}
```

#### Step 4: Build Release APK

##### Using Android Studio:
1. **Build → Generate Signed Bundle / APK**
2. Select **APK**, click **Next**
3. **Key store path**: Click **Choose existing** → select `stagehand-release.keystore`
4. Enter **Key store password**
5. **Key alias**: `stagehand`
6. Enter **Key password**
7. Click **Next**
8. Select **release** build variant
9. Check both **V1** and **V2** signature versions
10. Click **Finish**

##### Using Terminal:
```bash
gradlew.bat assembleRelease
```

Release APK will be at: `app/build/outputs/apk/release/app-release.apk`

## Testing the App

### Using Android Emulator

1. In Android Studio, click **Device Manager** (phone icon in toolbar)
2. Click **Create Device**
3. Select **Pixel 6** or any device
4. Select **API 31** (Android 12) or **API 34** (Android 14)
5. Click **Next**, then **Finish**
6. Click ▶️ (play button) next to the device

The emulator will start and the app will automatically install and launch.

### Using Physical Device

1. Enable Developer Mode on your Android device:
   - **Settings → About Phone**
   - Tap **Build Number** 7 times
   - Go back to **Settings → System → Developer Options**
   - Enable **USB Debugging**

2. Connect device via USB

3. In Android Studio, click the green ▶️ **Run** button

4. Select your device from the list

### Installing APK Manually

#### On Emulator:
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

#### On Physical Device:
1. Copy APK to device (via USB, email, cloud storage)
2. Open file on device
3. Allow "Install from Unknown Sources" if prompted
4. Tap **Install**

## Troubleshooting

### Gradle Sync Failed

**Solution**:
- Check internet connection
- **File → Invalidate Caches → Invalidate and Restart**
- Wait and retry

### SDK Not Found

**Solution**:
- **File → Settings → Android SDK**
- Ensure Android 12 (API 31) or Android 14 (API 34) is installed
- Click **Apply**

### Build Failed: "Keystore not found"

**Solution**:
- Make sure you created `stagehand-release.keystore`
- Check `keystore.properties` has correct path
- For debug builds, keystore is not needed

### App Crashes on Launch

**Check**:
1. App icons are properly generated
2. API URL is correct in `build.gradle.kts`
3. Device has internet connection
4. Check Logcat in Android Studio for error messages

### "Unable to resolve dependency"

**Solution**:
```bash
gradlew.bat --refresh-dependencies
```

Then sync project again.

## Distribution

### Sharing the APK

Once built, share the APK file:

1. **Direct**: Email or cloud storage (Dropbox, Google Drive)
2. **Firebase App Distribution** (recommended):
   ```bash
   npm install -g firebase-tools
   firebase login
   firebase init appdistribution
   firebase appdistribution:distribute app/build/outputs/apk/release/app-release.apk --app YOUR_FIREBASE_APP_ID
   ```

### APK Size

- Debug APK: ~15-20 MB
- Release APK (minified): ~10-15 MB

## Next Steps

Once app is running:

1. Test all features:
   - ✅ URL list loads
   - ✅ Search works
   - ✅ Filters work
   - ✅ Can view URL details
   - ✅ Can categorize URLs
   - ✅ Can create/edit categories

2. Report any issues

3. Request features or improvements

## Additional Resources

- **Android Developer Docs**: https://developer.android.com/docs
- **Jetpack Compose Guide**: https://developer.android.com/jetpack/compose
- **Gradle Build Guide**: https://developer.android.com/studio/build

## Support

For issues:
1. Check Logcat in Android Studio (View → Tool Windows → Logcat)
2. Review build output for errors
3. Ensure API is accessible: https://stagehand.theprestream.com/api/version
