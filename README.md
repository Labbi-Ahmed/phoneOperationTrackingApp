# Phone Operation Tracking App

An Android application that monitors all incoming and outgoing network requests, logs them locally, and automatically uploads them to Google Sheets at regular intervals.

## Features


Android application that monitors device network traffic (basic IPv4 TCP/UDP metadata) via a local VPN service, logs requests locally, and periodically (every 10 minutes) uploads the logs to a Google Sheets spreadsheet. After a successful upload, the local log is cleared.

## Features

- Track outgoing/incoming IPv4 packets (source/destination IP + ports, protocol) using `VpnService` (no root required)
- Classify common service ports (HTTP, HTTPS, DNS, etc.)
- Local CSV log stored at: `Android/data/<package>/files/network_logs.csv`
- Manual upload button to Google Sheets
- Automatic upload every 10 minutes (Alarm + WorkManager execution)
- Logs cleared after successful upload
- Toggle buttons to enable/disable tracking & scheduler
- Foreground services with persistent notifications while active

## Architecture Overview

| Layer | Components |
|-------|------------|
| UI | `MainActivity`, ViewBinding layout `activity_main.xml` |
| Services | `NetworkTrackingVpnService`, `NetworkMonitorService` |
| Scheduling | `AlarmScheduler`, `UploadAlarmReceiver`, `GoogleSheetsUploadWorker` |
| Logging | `NetworkLogManager`, `NetworkRequest` model |
| Cloud Sync | `GoogleSheetsManager` (Sheets API v4) |
| Preferences | `PreferenceManager` |

## Important Limitations

1. Deep packet inspection (payload/HTTPS domains) is NOT implemented—only IP/port metadata.
2. HTTPS domain names (SNI) are not extracted (needs TLS parsing – out of scope here).
3. VPN service implementation is simplified; production-grade parsers should validate headers thoroughly.
4. Google Sheets upload requires you to provide a Spreadsheet ID and authenticate with a Google account (OAuth user credential flow not fully automated here—see setup steps).

## Prerequisites

- Android Studio Giraffe/ newer
- Android device/emulator API 21+ (VPN + WorkManager)
- A Google Cloud project with:
  - Sheets API enabled
  - OAuth consent screen (External/Internal) configured
  - OAuth Client ID (type: Android or use installed application for testing) – for production you would integrate full sign-in (placeholder here relies on credential wiring you must finalize)

## Google Sheets Setup

1. Create a new Google Sheet manually.
2. Copy its Spreadsheet ID (the long hash in the URL):
   `https://docs.google.com/spreadsheets/d/<SPREADSHEET_ID>/edit#gid=0`
3. Open the app (after install) and (temporary approach):
   - Open `GoogleSheetsManager.kt` and replace `YOUR_SPREADSHEET_ID_HERE` with the ID.
   - (Optional) Implement a UI dialog to set this dynamically (future enhancement).

## Building the APK

From repository root (Gradle wrapper included):

```bash
gradle :app:assembleDebug
```

APK output (typical path):
```
app/build/outputs/apk/debug/app-debug.apk
```

## Installing on a Device

Either drag & drop the APK into an emulator, or:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Ensure USB debugging is enabled on the device.

### Build via GitHub Actions (CI)

1. Push your branch to GitHub.
2. Navigate to Actions tab → workflow "Build Android APK".
3. Download artifact `app-debug-apk` (debug build) after it finishes.
4. Install on device: `adb install -r app-debug.apk` (rename the downloaded file if needed).

## First Run Flow

1. Launch the app.
2. Tap “Start Tracking” → accept VPN consent dialog.
3. Tap “Start Scheduler” to enable 10‑minute periodic uploads.
4. (Optional) Tap “Upload Now” for an immediate upload.

## File Format (CSV)

Header row:
```
Timestamp,Protocol,Source,Destination,URL,Service
```
Example row:
```
2025-09-29 10:42:11,TCP,10.0.0.2:443,142.250.185.78:443,https://142.250.185.78,HTTPS
```

## How Upload Works

1. Alarm triggers every ~10 minutes → fires `UploadAlarmReceiver`.
2. Receiver enqueues a one-time `GoogleSheetsUploadWorker` (WorkManager ensures network + retries).
3. Worker reads `network_logs.csv`, prepends a session timestamp header row, appends all rows to Sheet1.
4. On success: local log cleared; on failure: WorkManager may retry based on policy.

## Modifying Spreadsheet ID at Runtime (Optional Enhancement)

Add a simple dialog in `MainActivity` to call:
```
PreferenceManager.setSpreadsheetId("<ID>")
```
Then ensure `GoogleSheetsManager` reads from preferences first.

## Security & Privacy Notes

- The VPN captures metadata only as implemented—extend parsing carefully if you add payload inspection.
- Always disclose to users that traffic is being monitored (legal compliance varies by jurisdiction).
- Don’t upload sensitive data to cloud spreadsheets unless encrypted/anonymized.

## Future Improvements

- Proper Google Sign-In / token refresh integration.
- Better packet parsing (IPv6, TLS SNI, HTTP Host headers via socket inspection / proxy approach).
- In-app configuration for Spreadsheet ID & account picker.
- Log size rotation & compression.
- Export to JSON + encryption at rest.

## Troubleshooting

| Issue | Cause | Fix |
|-------|-------|-----|
| No data logged | VPN permission denied | Re-tap Start Tracking, accept dialog |
| Upload fails | Missing Spreadsheet ID | Set real ID in `GoogleSheetsManager` |
| Upload fails (401) | Auth not configured | Integrate proper OAuth / Google Sign-In |
| Scheduler not firing | OEM battery optimizations | Exempt app from battery optimization |

## Disclaimer

This code is a functional prototype for educational purposes. Production network monitoring requires rigorous security, privacy compliance, and robust packet parsing.

---

MIT License (add LICENSE file as needed).

## Architecture

### Core Components

1. **MainActivity**: Main UI with control buttons
2. **NetworkTrackingVpnService**: VPN service for intercepting network traffic
3. **NetworkMonitorService**: Foreground service for continuous monitoring
4. **GoogleSheetsUploadWorker**: WorkManager worker for scheduled uploads
5. **NetworkLogManager**: Handles local log file operations
6. **GoogleSheetsManager**: Manages Google Sheets API interactions

### Data Flow

```
Network Traffic → VPN Service → Parse Packets → Log to CSV → WorkManager → Google Sheets
```

## Prerequisites

Before building and installing the app, you need:

### 1. Android Development Environment
- Android Studio (latest version recommended)
- Android SDK API level 21+ (Android 5.0)
- Java 8 or higher

### 2. Google Cloud Console Setup

#### Step 1: Create a Google Cloud Project
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select an existing one
3. Enable the Google Sheets API and Google Drive API

#### Step 2: Create Credentials
1. Go to "APIs & Services" → "Credentials"
2. Click "Create Credentials" → "OAuth 2.0 Client ID"
3. Select "Android" as application type
4. Add your app's package name: `com.phonetracker.networkmonitor`
5. Add SHA-1 fingerprint of your signing certificate

#### Step 3: Get SHA-1 Fingerprint
For debug builds:
```bash
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
```

For release builds, use your release keystore.

### 3. Google Sheets Setup

#### Create a Spreadsheet
1. Go to [Google Sheets](https://sheets.google.com/)
2. Create a new spreadsheet
3. Name it "Network Logs" (or any name you prefer)
4. Copy the spreadsheet ID from the URL
   - URL format: `https://docs.google.com/spreadsheets/d/{SPREADSHEET_ID}/edit`
5. Update `GoogleSheetsManager.kt` with your spreadsheet ID:
   ```kotlin
   private const val DEFAULT_SPREADSHEET_ID = "YOUR_ACTUAL_SPREADSHEET_ID"
   ```

## Building the App

### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/phoneOperationTrackingApp.git
cd phoneOperationTrackingApp
```

### 2. Configure Google API
1. Download the `google-services.json` file from your Firebase Console (if using Firebase)
2. Place it in the `app/` directory
3. Update the spreadsheet ID in `GoogleSheetsManager.kt`

### 3. Build the APK

#### Option A: Using Android Studio
1. Open the project in Android Studio
2. Wait for Gradle sync to complete
3. Go to Build → Generate Signed Bundle/APK
4. Choose APK and follow the signing process
5. The APK will be generated in `app/build/outputs/apk/`

#### Option B: Using Command Line
```bash
# Debug build (for testing)
./gradlew assembleDebug

# Release build (for production)
./gradlew assembleRelease
```

### 4. Install on Device

#### Via ADB
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

#### Via File Manager
1. Copy the APK file to your phone
2. Enable "Install from unknown sources" in Settings
3. Tap the APK file to install

## Configuration

### 1. First-Time Setup
1. Install and open the app
2. Grant VPN permission when prompted
3. Sign in to your Google account
4. The app will automatically create headers in your Google Sheet

### 2. Permissions Required
- **VPN Service**: To monitor network traffic
- **Internet**: To upload data to Google Sheets
- **Foreground Service**: To run continuously
- **Network State**: To check connectivity
- **Storage**: To store local log files

## Usage

### Control Panel
The app provides three main controls:

1. **Start/Stop Tracking**
   - Enables/disables network monitoring
   - Requires VPN permission
   - Shows current status

2. **Start/Stop Scheduler**
   - Enables/disables automatic uploads every 10 minutes
   - Uses WorkManager for reliable scheduling
   - Works even when app is closed

3. **Upload Now**
   - Triggers immediate upload to Google Sheets
   - Uploads current logs and clears local storage
   - Requires internet connection

### Data Format

The app logs the following information:
- **Timestamp**: Date and time of the request
- **Protocol**: TCP or UDP
- **Source**: Source IP and port
- **Destination**: Destination IP and port
- **URL**: Reconstructed URL (when possible)
- **Service**: Identified service (HTTP, HTTPS, DNS, etc.)

### Google Sheets Output

Data is uploaded to Google Sheets with:
- Session headers with upload timestamp
- CSV-formatted network request data
- Automatic row insertion
- Data separation between upload sessions

## Troubleshooting

### Common Issues

#### 1. VPN Permission Denied
- Go to Settings → Apps → Phone Operation Tracking App
- Enable all required permissions
- Restart the app

#### 2. Google Sheets Upload Fails
- Check internet connection
- Verify Google account permissions
- Ensure spreadsheet ID is correct
- Check Google API quotas

#### 3. App Stops Monitoring
- Check if battery optimization is disabled for the app
- Ensure the app has permission to run in background
- Verify VPN service is active

#### 4. No Data in Logs
- Ensure tracking is enabled
- Check if VPN service is running
- Verify network activity on the device

### Debug Information

#### Check Service Status
```bash
adb shell dumpsys activity services | grep NetworkTrackingVpnService
```

#### View App Logs
```bash
adb logcat | grep "NetworkTrackingVpn\|GoogleSheetsManager\|NetworkLogManager"
```

## Security Considerations

### Privacy
- All network traffic is analyzed locally
- Only metadata is uploaded to Google Sheets
- No packet content is stored or transmitted
- Local logs are automatically deleted after upload

### Permissions
- VPN permission is used only for monitoring, not redirecting traffic
- Google account access is limited to Sheets API
- No data is shared with third parties

## Development

### Project Structure
```
app/
├── src/main/
│   ├── java/com/phonetracker/networkmonitor/
│   │   ├── MainActivity.kt
│   │   ├── service/
│   │   │   ├── NetworkTrackingVpnService.kt
│   │   │   └── NetworkMonitorService.kt
│   │   ├── worker/
│   │   │   └── GoogleSheetsUploadWorker.kt
│   │   ├── data/
│   │   │   └── NetworkLogManager.kt
│   │   ├── model/
│   │   │   └── NetworkRequest.kt
│   │   └── utils/
│   │       ├── GoogleSheetsManager.kt
│   │       └── PreferenceManager.kt
│   ├── res/
│   └── AndroidManifest.xml
├── build.gradle
└── proguard-rules.pro
```

### Adding New Features
1. Network request filtering
2. Data encryption
3. Multiple spreadsheet support
4. Export functionality
5. Real-time monitoring dashboard

### Testing
```bash
# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest
```

## API Limits

### Google Sheets API
- 100 requests per 100 seconds per user
- 1000 requests per 100 seconds
- Consider these limits when configuring upload frequency

### Workarounds for High Traffic
- Increase upload interval to 30+ minutes
- Implement local batching
- Use multiple spreadsheets for data distribution

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## Support

If you encounter issues:
1. Check the troubleshooting section
2. Review the logs using ADB
3. Create an issue with detailed information
4. Include device model and Android version

## Changelog

### Version 1.0.0
- Initial release
- Network traffic monitoring via VPN
- Google Sheets integration
- Automatic scheduling with WorkManager
- Manual upload functionality
- Local CSV logging

---

**Important**: This app requires careful setup of Google Cloud credentials and proper permissions. Follow the setup instructions carefully for optimal performance.
