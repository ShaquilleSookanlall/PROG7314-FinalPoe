Weather App (PROG7314-POE)

A Kotlin Android weather app that lets users search cities and view current conditions, hourly and weekly forecasts. Users can sign in with Google, and switch Celsius/°C ↔ Fahrenheit/°F (the UI updates instantly).

Built for the PROG7314 POE.

✨ Features (current milestone)

🔍 City search with autocomplete overlay & suggestions

🌤 Header card: temperature, condition text, and wind speed

🕑 Hourly forecast (Yesterday / Today / Tomorrow chips)

📅 Weekly forecast (next 5 days)

⚙️ Settings: unit toggle (°C/°F) with live UI refresh

🔐 Firebase Auth – Google Sign-In (shared debug keystore so it works on every teammate’s machine)

🎨 Material 3 UI, gradient headers, cards, chips, and drawer menu

🧱 Tech Stack

Kotlin, AndroidX, Material 3

Retrofit2 + OkHttp (network)

DataStore (settings)

Firebase Auth + Google Sign-In

Coroutines/Flows

ViewBinding

🗂 Project Structure (high level)
app/
├─ src/main/
│   ├─ java/com/st10140587/prog7314_poe/
│   │   ├─ MainActivity.kt
│   │   ├─ SettingsActivity.kt
│   │   ├─ SignInActivity.kt
│   │   ├─ data/
│   │   │   ├─ WeatherRepository.kt
│   │   │   └─ model/ (GeocodingResponse, ForecastResponse, etc.)
│   │   └─ ui/ (adapters, icons)
│   └─ res/ (layouts, drawables, menu, values)
├─ build.gradle.kts (module)
└─ google-services.json

☁️ Weather API

Source: Open-Meteo (no API key required)

Geocoding: https://geocoding-api.open-meteo.com/v1/search

Forecast: https://api.open-meteo.com/v1/forecast

Parsed via Retrofit + Gson.

🚀 Getting Started
0) Requirements

Android Studio (latest)

Android Gradle Plugin compatible with Java/Kotlin 17

An Android device or emulator with Google Play services

1) Clone & open
   git clone <your-repo-url>
   open in Android Studio

2) Firebase setup (once per project)

The repo includes a shared debug keystore flow so Google Sign-In works for everyone.

Add shared SHA fingerprints (already done if you’re using the supplied google-services.json):

SHA-1 and SHA-256 from shared-debug.keystore

Enable Google provider in Firebase Console → Authentication → Sign-in method → Google → Enable.

Download the updated google-services.json and place it at:
app/google-services.json

Your code uses requestIdToken(getString(R.string.default_web_client_id)) which is defined from google-services.json.

3) Shared debug keystore (to ensure consistent SHA)

In app/build.gradle.kts, debug builds use:

signingConfigs {
create("sharedDebug") {
storeFile = file("${rootDir}/shared-debug.keystore")
storePassword = "android"
keyAlias = "androiddebugkey"
keyPassword = "android"
}
}
buildTypes {
debug { signingConfig = signingConfigs.getByName("sharedDebug") }
}


Verify:

./gradlew signingReport


Look for:

Variant: debug
Store: .../shared-debug.keystore
SHA1: 3745CA03...

4) Run the app

Build & run from Android Studio.

Tap Search in the top-right to open the overlay, type a city, choose a suggestion.

Open the drawer → Settings → toggle Celsius/°C ↔ Fahrenheit/°F (the screen updates instantly).

From the sign-in screen, use Sign in with Google.

If the app was previously installed and signed with a different debug key, uninstall first before running.

🔧 Troubleshooting
Google Sign-In shows DEVELOPER_ERROR (10)

The app isn’t signed with a keystore whose SHA-1/256 are in Firebase.

Run ./gradlew signingReport → ensure Variant: debug uses shared-debug.keystore.

In Firebase Project settings → Your apps → Android, confirm the SHA-1 and SHA-256 match the shared keystore.

Re-download google-services.json, replace in app/, and Sync Gradle.

Ensure Google provider is Enabled and OAuth consent screen has a Support email.

Unit toggle doesn’t change values

The app observes DataStore and re-renders from cache. If it doesn’t:

Check SettingsActivity is writing useCelsius to DataStore.

Ensure you’re on the latest MainActivity.kt with bindForecast() and the collectLatest observer.

Network issues / No results

Try different cities (API is case-insensitive).

Check connectivity; Open-Meteo requires internet.

🧪 Useful Commands

Print SHA-1/256 for the shared keystore (Windows – from project root if JDK is on PATH):

keytool -list -v -keystore shared-debug.keystore -alias androiddebugkey -storepass android -keypass android


Gradle signing report:

./gradlew signingReport

🗺 Roadmap (final version ideas)

Current location (Fused Location Provider)

Rain probability / precipitation charts

Saved cities & offline cache

In-app language preferences

Email/password sign-up flow with validation

Theming (dark mode) and more accessibility polish

📄 License / Academic Use

This project is for coursework (PROG7314). If you reuse or distribute, include attribution and respect any API terms (Open-Meteo) & Firebase policies.