# Configuration Template

## Google Sheets Configuration

### 1. Replace Spreadsheet ID
In `app/src/main/java/com/phonetracker/networkmonitor/utils/GoogleSheetsManager.kt`:

```kotlin
// Line 26: Replace with your actual Google Sheets ID
private const val DEFAULT_SPREADSHEET_ID = "PASTE_YOUR_SPREADSHEET_ID_HERE"
```

### 2. How to Get Spreadsheet ID
1. Create a Google Sheets document at https://sheets.google.com/
2. From the URL: `https://docs.google.com/spreadsheets/d/{THIS_IS_YOUR_ID}/edit`
3. Copy the ID part and replace in the code above

## Google Cloud Console OAuth Setup

### Package Name
```
com.phonetracker.networkmonitor
```

### SHA-1 Fingerprint Commands

#### For Development (Debug)
```bash
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
```

#### For Production (Release)
```bash
keytool -list -v -keystore /path/to/your/release.keystore -alias your_alias_name
```

### Required APIs to Enable
- Google Sheets API v4
- Google Drive API v3

## Optional Customizations

### Change Upload Frequency
In `MainActivity.kt`, line 81:
```kotlin
val uploadWorkRequest = PeriodicWorkRequestBuilder<GoogleSheetsUploadWorker>(
    10, TimeUnit.MINUTES // Change this number
)
```

### Modify Logged Data
In `NetworkRequest.kt`, modify the `toCSVRow()` function to add/remove fields:
```kotlin
fun toCSVRow(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val formattedTime = dateFormat.format(Date(timestamp))
    
    // Customize this line to change what data is logged
    return "$formattedTime,$protocol,$sourceAddress:$sourcePort,$destAddress:$destPort,$url,$service"
}
```

### App Name and Package
If you want to change the app name or package:

1. **Package Name**: Rename directories in `app/src/main/java/`
2. **App Name**: Edit `app/src/main/res/values/strings.xml`:
```xml
<string name="app_name">Your Custom App Name</string>
```
3. **Application ID**: Edit `app/build.gradle`:
```gradle
defaultConfig {
    applicationId "your.custom.package.name"
    // ...
}
```

## Build Configuration

### Debug Build (Development)
```bash
./gradlew assembleDebug
```
- Uses debug keystore
- Enables logging
- Allows USB debugging

### Release Build (Production)
```bash
./gradlew assembleRelease
```
- Requires release keystore
- Optimized and minified
- Ready for distribution

## Directory Structure After Setup
```
phoneOperationTrackingApp/
├── app/
│   ├── build.gradle (configured)
│   ├── src/main/
│   │   ├── java/com/phonetracker/networkmonitor/
│   │   │   ├── MainActivity.kt
│   │   │   ├── service/
│   │   │   ├── worker/
│   │   │   ├── data/
│   │   │   ├── model/
│   │   │   └── utils/
│   │   │       └── GoogleSheetsManager.kt (UPDATE SPREADSHEET ID HERE)
│   │   ├── res/
│   │   └── AndroidManifest.xml
│   └── google-services.json (if using Firebase - optional)
├── build.gradle
├── settings.gradle
├── gradle.properties
├── gradlew
├── gradlew.bat
├── README.md
├── SETUP.md
└── CONFIG.md (this file)
```

## Quick Start Checklist

- [ ] Created Google Cloud Project
- [ ] Enabled Google Sheets API and Google Drive API  
- [ ] Created Android OAuth 2.0 credentials
- [ ] Added SHA-1 fingerprint to OAuth credentials
- [ ] Created Google Sheets document
- [ ] Copied spreadsheet ID to `GoogleSheetsManager.kt`
- [ ] Built APK using `./gradlew assembleDebug` or Android Studio
- [ ] Installed APK on Android device
- [ ] Granted VPN permission
- [ ] Signed in with Google account
- [ ] Tested tracking and upload functionality

## Verification Steps

1. **Network Monitoring**: Start tracking, use some apps, check if requests are being logged
2. **Local Storage**: Verify log file is created in app's external files directory
3. **Google Sheets**: Trigger manual upload, check if data appears in your spreadsheet
4. **Automatic Upload**: Enable scheduler, wait 10 minutes, verify automatic upload works
5. **Permissions**: Ensure app works after device restart

---

**Important**: Keep your Google Cloud credentials and keystore files secure and never commit them to public repositories.