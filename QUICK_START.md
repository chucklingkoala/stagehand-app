# Stagehand Android - Quick Start Guide

## ⚡ TL;DR - Get Running in 5 Steps

1. **Install Android Studio** → https://developer.android.com/studio
2. **Open Project** → Open `d:\Github\stagehand-app` in Android Studio
3. **Wait for Sync** → Let Gradle download dependencies (5-15 min)
4. **Create App Icons** → Right-click `app/src/main/res` → New → Image Asset → Use `apple-touch-icon.png`
5. **Run** → Click green ▶️ button

## 📱 First Time Setup Checklist

### Before You Start
- [ ] Android Studio installed
- [ ] Internet connection available
- [ ] 10GB free disk space
- [ ] This repository on your computer

### In Android Studio (First Launch)

1. **Open Project**
   - Launch Android Studio
   - Click "Open"
   - Navigate to `d:\Github\stagehand-app`
   - Click OK

2. **Wait for Gradle Sync**
   - Bottom status bar shows progress
   - First sync: 5-15 minutes
   - Downloads ~2GB of dependencies
   - ☕ Go grab coffee!

3. **Verify Sync Success**
   - Check for ✅ "Gradle sync finished" message
   - No red errors in code
   - "Build" menu is enabled

4. **Create Launcher Icons** (REQUIRED!)
   - Right-click `app/src/main/res` in Project panel
   - Select `New → Image Asset`
   - **Icon Type**: Launcher Icons
   - **Path**: Click folder icon → Select `apple-touch-icon.png`
   - Click **Next** → **Finish**

5. **Create Emulator** (if you don't have a device)
   - Click device icon in toolbar (Device Manager)
   - Click **Create Device**
   - Select **Pixel 6**
   - Choose **API 31** or **API 34**
   - Click **Next** → **Finish**
   - Wait for download (~500MB)

6. **Run the App!**
   - Click green ▶️ (Run) button
   - Select your emulator or device
   - Wait for build (2-5 min first time)
   - App launches automatically

## 🎯 What to Test First

Once the app launches:

### 1. Dashboard Loads ✅
- You should see a list of URLs
- Each URL card shows title, posted by, time

### 2. Search Works ✅
- Tap search bar at top
- Type anything (e.g., "news")
- Results filter in real-time

### 3. Filters Work ✅
- Tap filter chips below search
- Try "On Show", "Dump", categories
- List updates immediately

### 4. View URL Details ✅
- Tap any URL card
- See full details, link preview
- Try "Open in Browser"

### 5. Categorize URLs ✅
- In URL detail screen
- Tap category dropdown
- Select a category
- Click "Save Changes"

### 6. Manage Categories ✅
- Tap menu (⋮) in top right
- Select "Categories"
- Try creating a new category
- Pick a color, enter name
- Click "Create"

## 🐛 Common Issues & Fixes

### "Gradle sync failed"
**Fix**:
- Check internet connection
- In menu: File → Invalidate Caches → Invalidate and Restart
- Try again

### "SDK not found" or "API 31 not installed"
**Fix**:
- File → Settings → Android SDK
- Check "Android 12 (API 31)" or "Android 14 (API 34)"
- Click Apply → Wait for download
- Restart Android Studio

### App crashes immediately on launch
**Fix**:
- Did you create app icons? (Step 4 above)
- Check Logcat (View → Tool Windows → Logcat) for errors
- Try "Build → Clean Project" then run again

### "Unable to locate adb" or "Emulator won't start"
**Fix**:
- Tools → SDK Manager → SDK Tools tab
- Check "Android SDK Platform-Tools"
- Click Apply

### "App installed but shows blank white screen"
**Fix**:
- This usually means icons are missing
- Complete Step 4 (Create Launcher Icons) above
- Uninstall app from device/emulator
- Run again

### List shows "No URLs found"
**Check**:
- Internet connection is active
- API is up: https://stagehand.theprestream.com/api/version
- Device/emulator has network access

## 📚 Next Steps

Once everything works:

1. **Read Full Docs**:
   - [README.md](README.md) - Project overview
   - [BUILD_INSTRUCTIONS.md](BUILD_INSTRUCTIONS.md) - Detailed build guide
   - [ANDROID_APP_SPECIFICATION.md](ANDROID_APP_SPECIFICATION.md) - Complete spec

2. **Build Release APK**:
   - See [BUILD_INSTRUCTIONS.md](BUILD_INSTRUCTIONS.md) section "Release Build"
   - Create keystore
   - Generate signed APK
   - Distribute to users

3. **Customize**:
   - Change colors: `app/src/main/java/.../presentation/theme/Color.kt`
   - Modify API URL: `app/build.gradle.kts` → `buildConfigField`
   - Update strings: `app/src/main/res/values/strings.xml`

## 🆘 Still Stuck?

### Diagnostic Checklist

- [ ] Android Studio version is 2023.1.1 or newer?
- [ ] Java version 17+ installed? (Check: `java -version`)
- [ ] Internet connection stable?
- [ ] Firewall allows Android Studio/Gradle?
- [ ] 10GB+ disk space available?
- [ ] Gradle sync completed without errors?
- [ ] App icons created via Image Asset Studio?
- [ ] API accessible: https://stagehand.theprestream.com/api/version

### View Build Logs

If build fails:
1. Click "Build" at bottom of Android Studio
2. Look for first error (red text)
3. Common errors:
   - Missing dependency → Sync project again
   - Compilation error → Check code for typos
   - Resource not found → Ensure icons created

### Clean Build

If nothing works, try clean build:
```bash
gradlew clean
```

Then:
- Build → Clean Project
- Build → Rebuild Project
- Run app again

## 📱 Testing on Real Device

Want to test on your actual Android phone?

1. **Enable Developer Mode**:
   - Settings → About Phone
   - Tap "Build Number" 7 times
   - Go back to Settings → System → Developer Options
   - Enable "USB Debugging"

2. **Connect via USB**:
   - Plug phone into computer
   - Allow USB debugging prompt on phone
   - In Android Studio, select your device from dropdown
   - Click Run

3. **Install APK Directly**:
   - Build debug APK: `gradlew assembleDebug`
   - Find APK: `app/build/outputs/apk/debug/app-debug.apk`
   - Transfer to phone (USB, email, cloud)
   - Open file on phone
   - Tap "Install"

## 🎉 Success!

If you can:
- ✅ See URL list
- ✅ Search URLs
- ✅ Filter by category
- ✅ View URL details
- ✅ Create categories

**You're all set!** The app is working correctly.

## 📞 Help Resources

- **Android Studio Guide**: https://developer.android.com/studio/intro
- **Emulator Docs**: https://developer.android.com/studio/run/emulator
- **Gradle Build**: https://developer.android.com/studio/build
- **Logcat Debugging**: https://developer.android.com/studio/debug/logcat

---

**Welcome to Stagehand Mobile! 🎙️**
