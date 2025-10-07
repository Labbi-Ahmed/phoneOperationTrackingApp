#!/bin/bash

# Build script for Phone Operation Tracking App
# This script automates the building process

echo "========================================="
echo "Phone Operation Tracking App Build Script"
echo "========================================="

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Please install JDK 8 or higher."
    exit 1
fi

# Check if Android SDK is available (if ANDROID_HOME is set)
if [ -z "$ANDROID_HOME" ]; then
    echo "⚠️  ANDROID_HOME is not set. Make sure Android SDK is properly configured."
else
    echo "✅ Android SDK found at: $ANDROID_HOME"
fi

# Make gradlew executable
chmod +x ./gradlew

echo ""
echo "Choose build type:"
echo "1) Debug build (for development/testing)"
echo "2) Release build (for production)"
read -p "Enter choice (1-2): " choice

case $choice in
    1)
        echo ""
        echo "🔨 Building debug APK..."
        ./gradlew assembleDebug
        
        if [ $? -eq 0 ]; then
            echo "✅ Debug build successful!"
            echo "📱 APK location: app/build/outputs/apk/debug/app-debug.apk"
            echo ""
            echo "To install on connected device:"
            echo "adb install app/build/outputs/apk/debug/app-debug.apk"
        else
            echo "❌ Build failed. Check the error messages above."
            exit 1
        fi
        ;;
    2)
        echo ""
        echo "🔨 Building release APK..."
        echo "⚠️  Make sure you have configured signing in app/build.gradle"
        ./gradlew assembleRelease
        
        if [ $? -eq 0 ]; then
            echo "✅ Release build successful!"
            echo "📱 APK location: app/build/outputs/apk/release/app-release.apk"
            echo ""
            echo "To install on device:"
            echo "adb install app/build/outputs/apk/release/app-release.apk"
        else
            echo "❌ Build failed. Check the error messages above."
            exit 1
        fi
        ;;
    *)
        echo "❌ Invalid choice. Exiting."
        exit 1
        ;;
esac

echo ""
echo "📋 Next steps:"
echo "1. Install the APK on your Android device"
echo "2. Grant VPN permission when prompted"
echo "3. Sign in with your Google account"
echo "4. Configure your Google Sheets ID in GoogleSheetsManager.kt"
echo "5. Test the network tracking functionality"
echo ""
echo "📖 See README.md and SETUP.md for detailed instructions."