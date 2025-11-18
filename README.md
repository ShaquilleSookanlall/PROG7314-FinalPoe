# Weather App ☀️

A modern Android weather application built with Kotlin that provides real-time weather forecasts, location management, and personalized weather alerts. Features biometric authentication, multi-language support, and offline caching capabilities.

**Student:** Shaquille Sookanlall  
**Student Number:** ST10140587  
**Institution:** The Independent Institute of Education (IIE)  
**Course:** PROG7312 - Programming 3B

---

## 📱 Features

### Core Functionality
- **Real-time Weather Data:** Fetches current weather conditions, hourly forecasts, and 7-day forecasts using the Open-Meteo API
- **Location Search:** Search and save multiple cities worldwide
- **Saved Locations:** Manage favorite locations with the ability to set a default location
- **Location Search:** Intelligent search with autocomplete suggestions showing live weather previews
- **Offline Caching:** View previously searched locations even without internet connection
- **Weather Sharing:** Share current weather conditions via any installed app

### Security & Authentication
- **Firebase Authentication:** Email/password and Google Sign-In support
- **Biometric Authentication:** Fingerprint/face unlock for secure app access
- **Session Management:** Automatic session locking when app goes to background

### Personalization
- **Multi-language Support:** English, Zulu (isiZulu), and Afrikaans
- **Temperature Units:** Switch between Celsius and Fahrenheit
- **Weather Alerts:** Periodic notifications for severe weather conditions

### User Interface
- **Material Design 3:** Modern, clean interface with smooth animations
- **Edge-to-Edge Display:** Immersive full-screen experience
- **Background Blur Effects:** Visual depth on Android 12+ devices
- **Responsive Navigation:** Drawer menu with intuitive navigation
- **Weather Icons:** Custom icons for different weather conditions

---

## 🛠️ Technical Stack

### Architecture & Patterns
- **MVVM Architecture:** Separation of concerns with ViewModels
- **Repository Pattern:** Clean data layer abstraction
- **Coroutines & Flow:** Asynchronous programming with reactive data streams

### Android Components
- **Jetpack Libraries:**
  - Room Database (offline caching)
  - DataStore (preferences)
  - WorkManager (background tasks)
  - Biometric API (authentication)
  - Lifecycle & ViewModel
  
### Networking & Data
- **Retrofit:** REST API communication
- **Gson:** JSON serialization/deserialization
- **OkHttp:** HTTP client with logging interceptor
- **Open-Meteo API:** Free weather data (no API key required)

### Firebase Integration
- **Firebase Authentication:** User authentication
- **Google Sign-In:** OAuth integration
- **Cloud Services:** Backend authentication management

### Other Technologies
- **Timber:** Logging framework
- **Material Components:** UI components
- **Kotlin DSL:** Gradle build configuration

---

## 📋 Requirements

### Minimum Requirements
- **Android Version:** 7.0 (Nougat, API 24) or higher
- **Storage:** ~15-20 MB
- **Internet Connection:** Required for weather data (cached data available offline)
- **Permissions:**
  - `INTERNET` - Fetch weather data
  - `ACCESS_NETWORK_STATE` - Check connectivity
  - `ACCESS_FINE_LOCATION` - Current location weather (optional)
  - `USE_BIOMETRIC` - Biometric authentication (optional)
  - `POST_NOTIFICATIONS` - Weather alerts (Android 13+, optional)

### Recommended
- **Android Version:** 12+ for best visual effects (blur, edge-to-edge)
- **Biometric Hardware:** Fingerprint sensor or face unlock
- **Google Play Services:** For Google Sign-In feature

---

## 🚀 Installation

### Option 1: Download APK (Recommended for Grading)

1. **Download the signed APK:**
   - Go to [GitHub Releases](https://github.com/ShaquilleSookanlall/PROG7314-FinalPoe/releases)
   - Download `weather-app-v1.0.0.apk`

2. **Enable Unknown Sources:**
   - **Android 8.0+:** Settings → Apps → Special access → Install unknown apps → Select your browser → Allow
   - **Android 7.x:** Settings → Security → Unknown sources → Enable

3. **Install:**
   - Open the downloaded APK file
   - Tap "Install"
   - Wait for installation to complete
   - Tap "Open" to launch

### Option 2: Build from Source

```bash
# Clone the repository
git clone https://github.com/ShaquilleSookanlall/PROG7314-FinalPoe.git
cd PROG7314-FinalPoe

# Open in Android Studio
# Build → Select Build Variant → release
# Build → Build Bundle(s) / APK(s) → Build APK(s)

# APK location: app/build/outputs/apk/release/app-release.apk
```

---

## 🎯 Usage Guide

### First Launch

1. **Create Account:**
   - Choose "Sign Up" on the landing page
   - Enter email and password (min 6 characters)
   - Or use "Sign in with Google"

2. **Biometric Setup (Optional):**
   - If your device supports biometrics, you'll be prompted to enable it
   - This adds an extra layer of security

3. **Search for Weather:**
   - Tap the search icon (🔍) in the top bar
   - Type a city name
   - Select from autocomplete suggestions
   - View current weather and forecasts

### Key Features

#### 📍 Managing Locations

**Save a Location:**
1. Search for a city
2. Once weather loads, tap "Save Location" button
3. Location is added to your saved list

**View Saved Locations:**
1. Open drawer menu (☰)
2. Select "Saved Locations"
3. Tap a location to set it as default
4. Swipe left/right to delete a location

**Default Location:**
- Set a default location to load automatically on app start
- Default location is marked with a star (⭐)

#### ⚙️ Settings

**Change Temperature Units:**
- Settings → Toggle "Use Celsius"
- Switches between °C and °F

**Change Language:**
- Settings → Preferred Language
- Choose: English, isiZulu, or Afrikaans
- App restarts to apply changes

#### 🔔 Weather Alerts

**Enable Notifications (Android 13+):**
- Grant "Post Notifications" permission when prompted
- Alerts run every 15 minutes for your default location
- Notifies about:
  - Thunderstorms
  - Heavy rain
  - High winds (≥50 km/h)

#### 📤 Share Weather

1. Search for any location
2. Tap share icon (📤) in top bar
3. Choose app to share via (WhatsApp, Email, etc.)
4. Weather summary is formatted and ready to send

---

## 🏗️ Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/st10140587/prog7314_poe/
│   │   │   ├── auth/                    # Biometric authentication
│   │   │   │   └── BiometricGate.kt
│   │   │   ├── data/                    # Data layer
│   │   │   │   ├── local/              # Room database
│   │   │   │   │   ├── AppDb.kt
│   │   │   │   │   ├── LocalCache.kt
│   │   │   │   │   ├── LocationDao.kt
│   │   │   │   │   ├── LocationEntity.kt
│   │   │   │   │   ├── ForecastDao.kt
│   │   │   │   │   ├── ForecastEntity.kt
│   │   │   │   │   └── Converters.kt
│   │   │   │   ├── model/              # Data models
│   │   │   │   │   └── WeatherDtos.kt
│   │   │   │   ├── network/            # API services
│   │   │   │   │   ├── RetrofitInstance.kt
│   │   │   │   │   └── WeatherApiService.kt
│   │   │   │   └── WeatherRepository.kt
│   │   │   ├── notify/                  # Notifications
│   │   │   │   ├── NotificationHelper.kt
│   │   │   │   └── WeatherAlertWorker.kt
│   │   │   ├── ui/                      # UI components
│   │   │   │   ├── DailyAdapter.kt
│   │   │   │   ├── HourAdapter.kt
│   │   │   │   └── WeatherIcons.kt
│   │   │   ├── App.kt                   # Application class
│   │   │   ├── LandingActivity.kt       # Entry point
│   │   │   ├── SignInActivity.kt        # Authentication
│   │   │   ├── SignUpActivity.kt        # Registration
│   │   │   ├── MainActivity.kt          # Main weather screen
│   │   │   ├── LocationsActivity.kt     # Saved locations
│   │   │   ├── SettingsActivity.kt      # App settings
│   │   │   ├── SettingsStore.kt         # Preferences
│   │   │   ├── LocaleUtils.kt           # Language management
│   │   │   ├── SessionLock.kt           # Session management
│   │   │   └── WeatherUtils.kt          # Utilities
│   │   ├── res/                         # Resources
│   │   │   ├── layout/                  # XML layouts
│   │   │   ├── drawable/                # Icons & images
│   │   │   ├── values/                  # Strings, colors
│   │   │   ├── values-zu/               # Zulu translations
│   │   │   └── values-af/               # Afrikaans translations
│   │   └── AndroidManifest.xml
│   └── test/
│       └── java/.../WeatherAppTests.kt  # Unit tests
├── build.gradle.kts                     # App-level build config
└── google-services.json                 # Firebase config
```

---

## 🧪 Testing

### Unit Tests

The project includes comprehensive unit tests covering core functionality:

**Run tests locally:**
```bash
./gradlew test
```

**Run tests in Android Studio:**
- Right-click on `WeatherAppTests.kt`
- Select "Run 'WeatherAppTests'"

**Test Coverage:**
- ✅ Temperature conversion (Celsius ↔ Fahrenheit)
- ✅ Weather description mapping
- ✅ API response parsing
- ✅ Location entity creation
- ✅ Forecast caching logic
- ✅ Search query validation
- ✅ Session lock behavior
- ✅ Settings persistence
- ✅ Date formatting
- ✅ Wind speed calculations

**Automated Testing (GitHub Actions):**
- Tests run automatically on every push to `main`, `master`, or `develop`
- View results: [Actions Tab](https://github.com/ShaquilleSookanlall/PROG7314-FinalPoe/actions)

### Manual Testing Checklist

**Authentication:**
- [ ] Sign up with email
- [ ] Sign in with email
- [ ] Google Sign-In
- [ ] Biometric authentication
- [ ] Session lock on background

**Weather Features:**
- [ ] Search for city
- [ ] View current weather
- [ ] View hourly forecast (Yesterday/Today/Tomorrow)
- [ ] View 7-day forecast
- [ ] Save location
- [ ] View saved locations
- [ ] Set default location
- [ ] Delete location
- [ ] Offline cache access

**Settings:**
- [ ] Toggle temperature units
- [ ] Change language
- [ ] Sign out

**Notifications:**
- [ ] Receive weather alerts

---

## 🔒 Security & Privacy

### Data Collection
- **Location Data:** Only collected with user permission, used solely for weather data
- **Search History:** Stored locally on device, not transmitted
- **User Authentication:** Managed by Firebase, email/password encrypted

### Data Storage
- **Local Storage:** All data stored on device using Room Database
- **No Third-Party Analytics:** No tracking or analytics services
- **Firebase Authentication:** Secure, industry-standard authentication

### Permissions
- **Optional Permissions:** Location and Biometric are optional
- **Minimal Data:** Only essential data collected
- **User Control:** Clear app data anytime via Settings

---

## 📖 API Documentation

### Open-Meteo API

**Base URLs:**
- Weather: `https://api.open-meteo.com/`
- Geocoding: `https://geocoding-api.open-meteo.com/`

**Key Endpoints:**

```kotlin
// Search for locations
GET /v1/search?name={query}&count=10

// Get weather forecast
GET /v1/forecast?latitude={lat}&longitude={lon}
  &current_weather=true
  &hourly=temperature_2m,weathercode
  &daily=temperature_2m_max,temperature_2m_min,precipitation_sum
  &past_days=1
  &forecast_days=7
```

**No API Key Required!** Open-Meteo is free and doesn't require registration.

**Documentation:** https://open-meteo.com/en/docs

---

## 🐛 Known Issues & Limitations

### Current Limitations
1. **Weather Alerts:** Only check default location (not all saved locations)
2. **Forecast Days:** Limited to 7 days by API
3. **Location Search:** Requires exact city names (no fuzzy matching)
4. **Offline Mode:** Can only view previously cached locations

### Potential Issues
1. **Google Sign-In:** Requires Google Play Services (not available on all devices)
2. **Biometrics:** May not work on devices without proper hardware
3. **Notifications:** User must grant permission on Android 13+
4. **Background Blur:** Only available on Android 12+ (gracefully degrades)

---

## 🔄 Version History

### Version 1.0.0 (Current)
**Release Date:** November 18, 2025

**Features:**
- Initial release
- Real-time weather data from Open-Meteo
- Location search and management
- Saved locations with default setting
- Firebase authentication (Email & Google)
- Biometric authentication
- Multi-language support (EN, ZU, AF)
- Offline caching
- Weather alerts
- Share functionality
- Unit tests with CI/CD

---

## 🚧 Future Enhancements

### Planned Features
- [ ] Widget support for home screen
- [ ] Weather maps and radar
- [ ] Extended forecast (14 days)
- [ ] Historical weather data
- [ ] Weather comparison between cities
- [ ] More granular alert customization
- [ ] Dark mode toggle
- [ ] Weather-based wallpapers
- [ ] Integration with calendar for weather planning

### Technical Improvements
- [ ] Jetpack Compose UI migration
- [ ] Pagination for saved locations
- [ ] Background sync for all saved locations
- [ ] More comprehensive unit tests
- [ ] UI/Instrumentation tests
- [ ] Performance monitoring
- [ ] Crash reporting

---

## 👨‍💻 Development

### Building the Project

**Prerequisites:**
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK API 35
- Gradle 8.11.1

**Setup:**
```bash
# Clone repository
git clone https://github.com/ShaquilleSookanlall/PROG7314-FinalPoe.git

# Open in Android Studio
# Sync Gradle files
# Run on emulator or device
```

**Debug Build:**
```bash
./gradlew assembleDebug
./gradlew installDebug
```

**Release Build:**
```bash
# Requires keystore configuration
./gradlew assembleRelease
```

### Code Style
- **Kotlin:** Official Kotlin code style
- **Formatting:** Android Studio default formatter
- **Naming:** Descriptive, camelCase for variables, PascalCase for classes

### Contributing
This is an academic project for coursework submission. Contributions are not being accepted at this time.

---

## 📄 License & Credits

### Project License
This project is submitted as academic coursework for PROG7312 at The Independent Institute of Education (IIE). All rights reserved by the student.

### Third-Party Libraries

**Android Jetpack**
- Copyright © Google LLC
- Apache License 2.0
- https://developer.android.com/jetpack

**Retrofit**
- Copyright © 2013 Square, Inc.
- Apache License 2.0
- https://square.github.io/retrofit/

**OkHttp**
- Copyright © 2019 Square, Inc.
- Apache License 2.0
- https://square.github.io/okhttp/

**Gson**
- Copyright © 2008 Google Inc.
- Apache License 2.0
- https://github.com/google/gson

**Timber**
- Copyright © 2013 Jake Wharton
- Apache License 2.0
- https://github.com/JakeWharton/timber

**Firebase**
- Copyright © Google LLC
- Apache License 2.0
- https://firebase.google.com/

**Material Components**
- Copyright © Google LLC
- Apache License 2.0
- https://material.io/develop/android

### Data Sources

**Open-Meteo API**
- Free weather API (no API key required)
- Data licensed under CC BY 4.0
- https://open-meteo.com/

### Assets & Icons
- Weather icons: Custom designed for this project
- App icon: Custom designed for this project

---

## 📞 Support & Contact

### For Grading/Academic Inquiries
**Student:** Shaquille Sookanlall  
**Student Number:** ST10140587  
**Email:** st10140587@vcconnect.edu.za  
**Institution:** The Independent Institute of Education (IIE)

### Technical Issues
For bugs or technical issues with the app:
1. Check [Known Issues](#-known-issues--limitations) section
2. Review [Installation](#-installation) instructions
3. Contact via email above

### Project Repository
**GitHub:** https://github.com/ShaquilleSookanlall/PROG7314-FinalPoe

---

## 🎓 Academic Declaration

This project was completed as part of the PROG7312 - Programming 3B module at The Independent Institute of Education (IIE) for the academic year 2025.

### AI Usage Declaration
AI tools (GitHub Copilot, Claude AI) were consulted during development for:
- Code completion suggestions
- Debugging assistance
- Documentation formatting
- Explaining complex Android APIs

All core application logic, architecture decisions, and implementation were done independently by the student with full understanding of the codebase.

### References

1. Android Developers. (2025). *App Architecture Guide*. https://developer.android.com/topic/architecture
2. Android Developers. (2025). *WorkManager Documentation*. https://developer.android.com/topic/libraries/architecture/workmanager
3. Cormen, T. H., Leiserson, C. E., Rivest, R. L., & Stein, C. (2009). *Introduction to Algorithms* (3rd ed.). MIT Press.
4. Firebase. (2025). *Firebase Authentication Documentation*. https://firebase.google.com/docs/auth
5. Google. (2025). *Material Design 3*. https://m3.material.io/
6. Open-Meteo. (2025). *Weather API Documentation*. https://open-meteo.com/en/docs
7. Square. (2025). *Retrofit Documentation*. https://square.github.io/retrofit/

---

## 📸 Screenshots

### Main Weather Screen
- Current temperature and conditions
- Hourly forecast chips (Yesterday/Today/Tomorrow)
- 7-day forecast list
- Search and save functionality

### Saved Locations
- List of saved cities with coordinates
- Default location marked with star
- Swipe to delete
- Tap to set as default

### Settings
- Temperature unit toggle
- Language selection
- Sign out option

### Authentication
- Email/password sign-in
- Google Sign-In option
- Biometric prompt
- 
---

*Last Updated: November 18, 2025*  
*Version: 1.0.0*  
*Build: Release*
