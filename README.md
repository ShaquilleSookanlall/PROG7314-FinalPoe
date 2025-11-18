# Weather App 🌤️

A modern Android weather application built with Kotlin that provides real-time weather forecasts, location management, and personalized weather alerts. Features biometric authentication, multi-language support, and offline caching capabilities.

## 📱 Features

### Core Functionality
- **Real-time Weather Data**: Fetches current weather conditions, hourly forecasts, and 7-day forecasts using the Open-Meteo API
- **Location Search**: Search and save multiple cities worldwide
- **Saved Locations**: Manage favorite locations with the ability to set a default location
- **Weather Sharing**: Share current weather conditions with friends via any sharing app
- **Offline Mode**: Cached weather data available when offline
- **Weather Alerts**: Periodic background notifications for severe weather conditions

### User Experience
- **Biometric Authentication**: Secure app access with fingerprint/face recognition
- **Multi-language Support**: Available in English, French, and Afrikaans
- **Temperature Units**: Toggle between Celsius and Fahrenheit
- **Modern UI**: Material Design 3 with gradient backgrounds and smooth animations
- **Dark/Light Themes**: Respects system theme preferences

### Technical Features
- **Firebase Authentication**: Email/password and Google Sign-In support
- **Local Database**: Room database for offline caching and saved locations
- **Background Workers**: WorkManager for periodic weather alerts
- **Session Management**: Biometric lock when app goes to background

---

## 🎯 How It Works

### Authentication Flow
1. **Landing Screen**: First-time users see a landing page with "Get Started" button
2. **Sign In/Sign Up**: Users can create an account or sign in with:
   - Email and password
   - Google account
3. **Biometric Prompt**: After successful authentication, users set up biometric lock for future sessions

### Main Weather Screen
- **Header Section**: Displays current temperature, weather condition, and wind speed for the default location
- **Search**: Tap the search icon (top-right) to search for cities
  - Live autocomplete with weather preview
  - Tap any suggestion to view full forecast
- **Hourly Forecast**: Swipe horizontally through hourly temperatures
  - Toggle between Yesterday, Today, and Tomorrow using chips
- **Weekly Forecast**: Scroll vertically through 5-day forecast with high/low temperatures
- **Share Button**: Top-right menu to share current weather

### Navigation Drawer (Hamburger Menu)
Access via the hamburger icon (top-left):
- **🏠 Home**: Return to main weather screen
- **📍 Saved Locations**: View and manage saved cities
- **⚙️ Settings**: Configure app preferences
- **🔓 Sign Out**: Log out of the app

### Saved Locations Screen
- **View All Locations**: See all saved cities with current weather
- **Set Default**: Tap any location to set it as default (loads on app start)
- **Delete Locations**: 
  - Long-press any location to delete
  - Or swipe left/right to delete
- **Live Weather Data**: Each saved location shows current temperature and conditions

### Settings
- **Temperature Units**: Switch between Celsius (°C) and Fahrenheit (°F)
- **Language**: Choose from:
  - English
  - French (Français)
  - Afrikaans
  - *Note: Changing language restarts the app*

---

## 🚀 How to Use

### Getting Started
1. **Install the app** on your Android device
2. **Launch the app** - you'll see the landing screen
3. **Tap "Get Started"** to create an account or sign in
4. **Set up biometric authentication** (fingerprint/face) when prompted

### Searching for Weather
1. **Tap the search icon** (magnifying glass) in the top-right corner
2. **Type a city name** - suggestions appear as you type with live weather previews
3. **Select a city** from suggestions
4. **View the forecast** - hourly and weekly data loads automatically
5. **Save the location** *(optional)*:
   - The location is automatically saved when you search for it
   - Access saved locations from the hamburger menu → Saved Locations

### Managing Locations
1. **Open hamburger menu** (☰) → Select "Saved locations"
2. **View all saved cities** with current weather
3. **Set default location**: Tap any city to set it as default
4. **Delete locations**:
   - **Long-press** any location, or
   - **Swipe left/right** on a location

### Viewing Hourly Forecast
1. Below the main weather display, find the **chip selector**:
   - Yesterday | Today | Tomorrow
2. **Tap any chip** to view hourly temperatures for that day
3. **Scroll horizontally** through the hourly forecast

### Sharing Weather
1. **Tap the share icon** (↗️) in the top-right corner
2. **Choose your sharing app** (WhatsApp, Email, SMS, etc.)
3. Weather summary is automatically formatted for sharing

### Changing Settings
1. **Open hamburger menu** (☰) → Select "Settings"
2. **Toggle temperature units** with the Celsius/Fahrenheit switch
3. **Change language**:
   - Tap "Preferred Language"
   - Select your language
   - App will restart with the new language

---

## 🔔 Weather Alerts

The app monitors your default location and sends notifications for:
- ⛈️ Thunderstorms
- 🌧️ Rain showers
- 🌫️ Drizzle
- 💨 High winds (≥50 km/h)

**Note**: Alerts check every 15 minutes when connected to the internet.

---

## 🛠️ Developer Setup

### Prerequisites
- **Android Studio**: Arctic Fox (2020.3.1) or newer
- **JDK**: 11 or higher
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
- **Kotlin**: 1.9.0 or higher

### Required API Keys & Services

#### 1. Firebase Setup
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a new project (or use existing)
3. Add an Android app:
   - Package name: `com.st10140587.prog7314_poe`
   - Download `google-services.json`
   - Place it in `app/` directory
4. Enable Authentication:
   - Go to **Authentication** → **Sign-in method**
   - Enable **Email/Password**
   - Enable **Google** sign-in
5. For Google Sign-In:
   - Get your app's SHA-1 fingerprint:
     ```bash
     keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
     ```
   - Add SHA-1 to Firebase project settings → Android app

#### 2. Open-Meteo API
- **No API key required!** ✅
- The app uses [Open-Meteo](https://open-meteo.com/) which is free and open-source
- Endpoints:
  - Weather: `https://api.open-meteo.com/`
  - Geocoding: `https://geocoding-api.open-meteo.com/`

### Installation Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/weather-app.git
   cd weather-app
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - File → Open → Select the project directory
   - Wait for Gradle sync to complete

3. **Add `google-services.json`**
   - Place your Firebase `google-services.json` file in the `app/` directory
   - If missing, you'll see build errors

4. **Sync Project**
   - File → Sync Project with Gradle Files
   - Resolve any dependency issues

5. **Run the App**
   - Connect an Android device (API 24+) or start an emulator
   - Click the Run button (▶️) or press `Shift + F10`
   - Select your device and click OK

### Project Structure

```
app/src/main/java/com/st10140587/prog7314_poe/
├── auth/
│   └── BiometricGate.kt              # Biometric authentication handler
├── data/
│   ├── local/
│   │   ├── AppDb.kt                  # Room database
│   │   ├── LocalCache.kt             # Cache management
│   │   ├── LocationDao.kt            # Location data access
│   │   ├── LocationEntity.kt         # Location model
│   │   ├── ForecastDao.kt            # Forecast data access
│   │   └── ForecastEntity.kt         # Forecast model
│   ├── model/
│   │   └── WeatherDtos.kt            # API response models
│   ├── network/
│   │   ├── RetrofitInstance.kt       # Retrofit configuration
│   │   └── WeatherApiService.kt      # API endpoints
│   └── WeatherRepository.kt          # Data repository
├── notify/
│   ├── NotificationHelper.kt         # Notification utilities
│   └── WeatherAlertWorker.kt         # Background worker
├── ui/
│   ├── DailyAdapter.kt               # Weekly forecast adapter
│   ├── HourAdapter.kt                # Hourly forecast adapter
│   └── WeatherIcons.kt               # Weather icon mapping
├── App.kt                            # Application class
├── LandingActivity.kt                # Landing screen
├── MainActivity.kt                   # Main weather screen
├── LocationsActivity.kt              # Saved locations screen
├── SettingsActivity.kt               # Settings screen
├── SignInActivity.kt                 # Sign in screen
├── SignUpActivity.kt                 # Sign up screen
├── LocaleUtils.kt                    # Language utilities
├── SessionLock.kt                    # Session management
└── SettingsStore.kt                  # User preferences
```

### Key Dependencies

```gradle
// Firebase
implementation 'com.google.firebase:firebase-auth-ktx:22.1.1'
implementation 'com.google.android.gms:play-services-auth:20.7.0'

// Networking
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
implementation 'com.squareup.okhttp3:logging-interceptor:4.11.0'

// Room Database
implementation 'androidx.room:room-runtime:2.5.2'
implementation 'androidx.room:room-ktx:2.5.2'
kapt 'androidx.room:room-compiler:2.5.2'

// WorkManager
implementation 'androidx.work:work-runtime-ktx:2.8.1'

// Biometric
implementation 'androidx.biometric:biometric:1.1.0'

// Coroutines
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'

// DataStore
implementation 'androidx.datastore:datastore-preferences:1.0.0'

// Material Design
implementation 'com.google.android.material:material:1.9.0'
```

### Build Configuration

**File: `app/build.gradle`**

```gradle
android {
    compileSdk 34
    
    defaultConfig {
        applicationId "com.st10140587.prog7314_poe"
        minSdk 24
        targetSdk 34
        versionCode 1
        versionName "1.0"
    }
    
    buildFeatures {
        viewBinding true
        dataBinding true
    }
    
    kotlinOptions {
        jvmTarget = '1.8'
    }
}
```

### Testing the App

#### Testing Authentication
1. **Email Sign-Up**: Create account with test email
2. **Google Sign-In**: Use your Google account
3. **Biometric**: Device must have fingerprint/face enrolled

#### Testing Weather Features
1. **Search**: Try "Durban", "London", "New York"
2. **Save Locations**: Save multiple cities
3. **Set Default**: Tap a saved location to set as default
4. **Offline Mode**: Enable airplane mode and relaunch app

#### Testing Notifications
Enable debug mode in `WeatherAlertWorker.kt`:
```kotlin
WeatherAlertWorker.DEBUG_ALWAYS_ALERT = true
```
This sends a notification every 15 minutes for testing.

---

## 🐛 Troubleshooting

### Common Issues

**Issue**: Google Sign-In fails with `DEVELOPER_ERROR`
- **Solution**: Verify SHA-1 fingerprint is added to Firebase console
- Check package name matches in Firebase and `build.gradle`

**Issue**: App crashes on startup
- **Solution**: Ensure `google-services.json` is in the `app/` directory
- Check Firebase configuration is correct

**Issue**: No weather data loads
- **Solution**: Check internet connection
- Verify device date/time is correct
- Check Logcat for API errors

**Issue**: Biometric prompt doesn't appear
- **Solution**: Ensure device has biometric authentication enabled
- Check device settings → Security → Fingerprint/Face unlock

**Issue**: Language doesn't change
- **Solution**: Restart the app completely (force stop and relaunch)
- Clear app data if issue persists

**Issue**: Notifications not received
- **Solution**: 
  - Android 13+: Grant POST_NOTIFICATIONS permission
  - Check notification settings for the app
  - Ensure background data is enabled

---

## 📋 Features Checklist

- ✅ User Authentication (Firebase)
- ✅ Biometric Security
- ✅ Weather Forecasting (Current, Hourly, Weekly)
- ✅ Location Search & Management
- ✅ Offline Caching
- ✅ Weather Sharing
- ✅ Background Notifications
- ✅ Multi-language Support (EN, FR, AF)
- ✅ Temperature Unit Conversion (°C/°F)
- ✅ Material Design 3 UI
- ✅ Session Management
- ✅ Swipe-to-Delete Gestures

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 👨‍💻 Developer

**Student Number**: ST10140587  
**Project**: PROG7314 POE - Weather Application

---

## 🙏 Acknowledgments

- [Open-Meteo](https://open-meteo.com/) - Free weather API
- [Firebase](https://firebase.google.com/) - Authentication services
- [Material Design](https://material.io/) - UI design guidelines
- [Timber](https://github.com/JakeWharton/timber) - Logging library

---

## 📞 Support

For issues, questions, or contributions:
1. Open an issue in the GitHub repository
2. Provide detailed steps to reproduce any bugs
3. Include device information and Android version

---

## 🔮 Future Enhancements

- [ ] Weather maps integration
- [ ] Radar imagery
- [ ] Extended 14-day forecasts
- [ ] Weather widgets
- [ ] Weather history graphs
- [ ] UV index and air quality data
- [ ] Custom notification preferences
- [ ] Location-based auto-detection

---

**Made with ❤️ and ☕ by a weather enthusiast**
