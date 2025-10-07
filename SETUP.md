# Setup Instructions

Follow these steps carefully to set up and build your Phone Operation Tracking App.

## 1. Google Cloud Console Setup

### Create Project and Enable APIs
```bash
# 1. Go to https://console.cloud.google.com/
# 2. Create a new project or select existing one
# 3. Enable these APIs:
#    - Google Sheets API
#    - Google Drive API
```

### Create Android OAuth Credentials
1. Go to "APIs & Services" → "Credentials"
2. Click "Create Credentials" → "OAuth 2.0 Client ID"
3. Select "Android"
4. Package name: `com.phonetracker.networkmonitor`
5. Get SHA-1 fingerprint:

```bash
# For debug builds (development)
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android

# For release builds (production)
keytool -list -v -keystore /path/to/your/release.keystore -alias your_alias_name
```

## 2. Google Sheets Setup

### Create Spreadsheet
1. Go to https://sheets.google.com/
2. Create new spreadsheet
3. Name it "Phone Network Logs" (or your preference)
4. Copy the spreadsheet ID from URL:
   ```
   https://docs.google.com/spreadsheets/d/{SPREADSHEET_ID}/edit
   ```

### Update Configuration
Edit `app/src/main/java/com/phonetracker/networkmonitor/utils/GoogleSheetsManager.kt`:

```kotlin
// Replace with your actual spreadsheet ID
private const val DEFAULT_SPREADSHEET_ID = "YOUR_SPREADSHEET_ID_HERE"
```

## 3. Build Instructions

### Prerequisites
- Android Studio 2023.1.1 or later
- JDK 8 or higher
- Android SDK (API level 21+)

### Build Steps

#### Option A: Android Studio
1. Open project in Android Studio
2. Wait for Gradle sync
3. Build → Generate Signed Bundle/APK
4. Select APK, create/use signing key
5. Choose release variant
6. Generate APK

#### Option B: Command Line
```bash
# Debug build (for testing)
./gradlew assembleDebug

# Release build (for production)
./gradlew assembleRelease
```

## 4. Installation

### Install APK
```bash
# Via ADB
adb install app/build/outputs/apk/release/app-release.apk

# Or copy to phone and install manually
```

### Grant Permissions
1. Open app
2. Allow VPN permission when prompted
3. Sign in with Google account
4. Grant storage permissions if requested

## 5. Configuration

### First Run
1. Start the app
2. Tap "Start Tracking" - this will request VPN permission
3. Accept VPN permission dialog
4. Tap "Start Scheduler" to enable automatic uploads
5. Test with "Upload Now" to verify Google Sheets integration

### Verify Setup
1. Generate some network traffic (browse web, use apps)
2. Check that tracking status shows "Enabled"
3. Trigger manual upload
4. Verify data appears in your Google Spreadsheet

## 6. Troubleshooting

### Common Issues

#### VPN Permission Denied
```bash
# Check app permissions
adb shell dumpsys package com.phonetracker.networkmonitor | grep permission
```

#### Google Sheets Upload Fails
- Verify spreadsheet ID is correct
- Check Google account has access to the spreadsheet
- Ensure APIs are enabled in Google Cloud Console
- Check internet connectivity

#### App Keeps Stopping
- Disable battery optimization for the app
- Check if app has all required permissions
- Review app logs: `adb logcat | grep NetworkTrackingVpn`

### Debug Commands
```bash
# View app logs
adb logcat | grep "phonetracker\|NetworkTrackingVpn\|GoogleSheetsManager"

# Check running services
adb shell dumpsys activity services | grep NetworkTrackingVpnService

# Monitor network usage
adb shell dumpsys netstats | grep com.phonetracker.networkmonitor
```

## 7. Security Notes

### App Permissions
- VPN: Used only for monitoring, not redirecting traffic
- Internet: Required for Google Sheets uploads
- Storage: For local log file storage
- Accounts: For Google authentication

### Data Privacy
- Only metadata is logged (no packet contents)
- Data is stored locally until uploaded
- Logs are deleted after successful upload
- No third-party data sharing

## 8. Customization

### Upload Frequency
Edit `MainActivity.kt` to change upload interval:
```kotlin
val uploadWorkRequest = PeriodicWorkRequestBuilder<GoogleSheetsUploadWorker>(
    15, TimeUnit.MINUTES // Change from 10 to desired minutes
)
```

### Log Format
Modify `NetworkRequest.kt` to change logged data:
```kotlin
fun toCSVRow(): String {
    // Add or modify logged fields here
}
```

### Spreadsheet Format
Update `GoogleSheetsManager.kt` for custom spreadsheet layout:
```kotlin
private suspend fun setupHeaders(spreadsheetId: String) {
    val headers = listOf(
        listOf("Timestamp", "Protocol", "Source", "Destination", "URL", "Service", "Custom_Field")
    )
    // Add custom headers
}
```

## 9. Production Deployment

### Release Build
1. Create release keystore if not exists:
```bash
keytool -genkey -v -keystore release-key.keystore -alias release -keyalg RSA -keysize 2048 -validity 10000
```

2. Update `app/build.gradle` for signing:
```gradle
android {
    signingConfigs {
        release {
            storeFile file('path/to/release-key.keystore')
            storePassword 'your_store_password'
            keyAlias 'release'
            keyPassword 'your_key_password'
        }
    }
}
```

3. Build release APK:
```bash
./gradlew assembleRelease
```

### Distribution
- Upload APK to Google Play Store (recommended)
- Or distribute directly (enable "Unknown sources" on target devices)

## 10. Support

For issues or questions:
1. Check logs using provided debug commands
2. Review Google Cloud Console for API quota/errors
3. Test with different network conditions
4. Create issue with detailed error information

---

Remember to keep your Google Cloud credentials secure and never commit them to version control!