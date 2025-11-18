package com.st10140587.prog7314_poe

import android.content.Context
import android.content.Intent
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.core.widget.doOnTextChanged
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.st10140587.prog7314_poe.data.WeatherRepository
import com.st10140587.prog7314_poe.databinding.ActivityMainBinding
import com.st10140587.prog7314_poe.ui.DailyAdapter
import com.st10140587.prog7314_poe.ui.DayUi
import com.st10140587.prog7314_poe.ui.HourAdapter
import com.st10140587.prog7314_poe.ui.HourUi
import com.st10140587.prog7314_poe.ui.WeatherIcons
import com.st10140587.prog7314_poe.data.model.ForecastResponse
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import timber.log.Timber
import kotlin.math.roundToInt

// Biometrics session lock
import androidx.fragment.app.FragmentActivity
import com.st10140587.prog7314_poe.auth.BiometricGate
import com.st10140587.prog7314_poe.SessionLock

// Offline cache (Room helper)
import com.st10140587.prog7314_poe.data.local.LocalCache

// Alerts: WorkManager
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.st10140587.prog7314_poe.notify.WeatherAlertWorker

class MainActivity : AppCompatActivity() {
    // 🔹 Make MainActivity use the saved language
    override fun attachBaseContext(newBase: Context) {
        val wrapped = LocaleUtils.wrapContext(newBase)
        super.attachBaseContext(wrapped)
    }

    private lateinit var binding: ActivityMainBinding
    private val repo = WeatherRepository()
    private lateinit var settings: SettingsStore

    private val hourAdapter = HourAdapter()
    private val dailyAdapter = DailyAdapter()
    private lateinit var suggestionAdapter: SuggestionAdapter

    private var searchJob: Job? = null

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    // cache for re-rendering unit changes
    private var lastForecast: ForecastResponse? = null
    private var lastUseCelsius: Boolean = true

    // local cache instance (Room)
    private lateinit var cache: LocalCache

    // remember last fetched coords so we can "Save location"
    private var lastLat: Double? = null
    private var lastLon: Double? = null

    // Launcher for LocationsActivity result — API-gate the reload to silence lint on minSdk 24
    private val locationsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { res ->
        val changed = res.resultCode == RESULT_OK &&
                (res.data?.getBooleanExtra("defaultChanged", false) == true)
        if (changed) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                reloadDefault()
            } else {
                Timber.w("reloadDefault skipped on API < 26")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        settings = SettingsStore(this)
        cache = LocalCache(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Status bar inset for toolbar
        ViewCompat.setOnApplyWindowInsetsListener(binding.topAppBar) { v, insets ->
            v.updatePadding(top = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top)
            insets
        }

        // Toolbar + Drawer
        setSupportActionBar(binding.topAppBar)
        drawerLayout = binding.drawerLayout
        navView = binding.navigationView
        binding.topAppBar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        navView.layoutParams = navView.layoutParams.apply {
            width = (resources.displayMetrics.widthPixels * 0.75f).toInt()
        }
        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_settings -> startActivity(Intent(this, SettingsActivity::class.java))
                R.id.nav_sign_out -> {
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this, SignInActivity::class.java))
                    finish()
                }
            }
            drawerLayout.closeDrawers(); true
        }

        // Lists
        binding.rvHourly.apply {
            layoutManager = LinearLayoutManager(
                this@MainActivity, LinearLayoutManager.HORIZONTAL, false
            )
            adapter = hourAdapter
        }
        binding.rvWeekly.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = dailyAdapter
        }

        // Header defaults
        binding.tvHeaderTemp.text = "--°"
        binding.tvHeaderCond.text = getString(R.string.search_city_hint)
        binding.tvHeaderWind.text = getString(R.string.wind_placeholder)

        // ===== Overlay wiring =====
        suggestionAdapter = SuggestionAdapter { clicked ->
            hideSearchOverlay()
            searchAndShow(clicked.displayName)
        }
        binding.rvSuggestions.layoutManager = LinearLayoutManager(this)
        binding.rvSuggestions.adapter = suggestionAdapter

        val dropdownAdapter = ArrayAdapter<String>(
            this, android.R.layout.simple_dropdown_item_1line
        )
        binding.overlayEtCity.setAdapter(dropdownAdapter)

        // Live autocomplete
        binding.overlayEtCity.doOnTextChanged { text, _, _, _ ->
            searchJob?.cancel()
            val query = text?.toString()?.trim().orEmpty()
            if (query.length < 2) {
                dropdownAdapter.clear()
                suggestionAdapter.submitList(emptyList())
                return@doOnTextChanged
            }
            searchJob = lifecycleScope.launch {
                delay(300)
                try {
                    val results = withContext(Dispatchers.IO) { repo.searchPlaces(query) }

                    val names = results.map { it.toString() }
                    dropdownAdapter.clear()
                    dropdownAdapter.addAll(names)
                    if (!binding.overlayEtCity.isPopupShowing) binding.overlayEtCity.showDropDown()

                    val cards = results.map { place ->
                        val lat = place.latitude ?: 0.0
                        val lon = place.longitude ?: 0.0
                        val fc = try {
                            withContext(Dispatchers.IO) { repo.getForecast(lat, lon) }
                        } catch (_: Exception) { null }

                        PlaceSuggestion(
                            displayName = place.toString(),
                            subline = place.country ?: "",
                            weatherCode = fc?.current?.weathercode
                        )
                    }
                    suggestionAdapter.submitList(cards)
                } catch (e: Exception) {
                    Timber.e(e, "Autocomplete failed")
                }
            }
        }

        // End icon triggers search
        binding.overlayTilCity.setEndIconOnClickListener {
            val q = binding.overlayEtCity.text?.toString()?.trim().orEmpty()
            if (q.isNotEmpty()) {
                hideSearchOverlay(); searchAndShow(q)
            } else binding.overlayEtCity.error = getString(R.string.search_city_hint)
        }

        // Keyboard action "Search"
        binding.overlayEtCity.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val q = v.text?.toString()?.trim().orEmpty()
                if (q.isNotEmpty()) {
                    hideSearchOverlay(); searchAndShow(q)
                } else binding.overlayEtCity.error = getString(R.string.search_city_hint)
                true
            } else false
        }

        // Tap on scrim to close
        binding.overlayScrim.setOnClickListener { hideSearchOverlay() }

        // observe unit changes and re-render from cache
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                settings.useCelsius.collectLatest { useC ->
                    lastUseCelsius = useC
                    lastForecast?.let { bindForecast(it, useC) }
                }
            }
        }

        // Try load default location at cold start (harmless if none)
        lifecycleScope.launch {
            try {
                val def = withContext(Dispatchers.IO) { cache.getDefaultLocation() }
                def?.let {
                    try {
                        showLoading(true)
                        val fc = withContext(Dispatchers.IO) { repo.getForecast(it.latitude, it.longitude) }
                        lastForecast = fc
                        lastUseCelsius = withContext(Dispatchers.IO) { settings.useCelsius.first() }
                        bindForecast(fc, lastUseCelsius)
                        binding.topAppBar.title = it.name
                        lastLat = it.latitude; lastLon = it.longitude
                        withContext(Dispatchers.IO) { cache.saveForecast(it.name, it.latitude, it.longitude, fc) }
                    } catch (_: Exception) {
                        val cached = withContext(Dispatchers.IO) { cache.getForecast(it.name) }
                        cached?.let { c ->
                            lastForecast = c
                            lastUseCelsius = withContext(Dispatchers.IO) { settings.useCelsius.first() }
                            bindForecast(c, lastUseCelsius)
                            binding.topAppBar.title = it.name
                            lastLat = it.latitude; lastLon = it.longitude
                        }
                    } finally {
                        showLoading(false)
                    }
                }
            } catch (_: Exception) { /* ignore */ }
        }

        // Notifications: ask permission (13+) + schedule periodic alerts
        requestPostNotificationsIfNeeded()
        scheduleWeatherAlerts()
    }

    override fun onResume() {
        super.onResume()
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null && !SessionLock.unlocked) {
            val gate = BiometricGate(
                activity = this as FragmentActivity,
                onSuccess = { SessionLock.unlock() },
                onFailOrCancel = { Timber.w("Biometric cancelled/failed in MainActivity: $it") }
            )
            if (gate.isAvailable(this)) gate.show() else SessionLock.unlock()
        }
    }

    /** Overlay show/hide + background blur (Android 12+) */
    private fun showSearchOverlay() {
        binding.searchOverlay.visibility = View.VISIBLE
        applyBackgroundBlur(true)
        binding.overlayEtCity.setText("")
        binding.overlayEtCity.requestFocus()
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .showSoftInput(binding.overlayEtCity, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideSearchOverlay() {
        binding.searchOverlay.visibility = View.GONE
        applyBackgroundBlur(false)
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(binding.searchOverlay.windowToken, 0)
    }

    private fun applyBackgroundBlur(enable: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            binding.blurHost.setRenderEffect(
                if (enable)
                    RenderEffect.createBlurEffect(30f, 30f, Shader.TileMode.CLAMP)
                else null
            )
        }
    }

    private fun showLoading(show: Boolean) {
        binding.progress.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun cToF(c: Double) = c * 9 / 5 + 32
    private fun formatTemp(c: Double, useC: Boolean): String {
        val v = if (useC) c else cToF(c)
        val unit = if (useC) "°C" else "°F"
        return "${v.roundToInt()}$unit"
    }

    private fun weatherDesc(code: Int): Int = when (code) {
        0 -> R.string.weather_clear_sky
        in 1..3 -> R.string.weather_clear_sky // Using "Clear" for "Mainly Clear"
        in 45..48 -> R.string.weather_fog
        in 51..67 -> R.string.weather_drizzle
        in 71..77 -> R.string.weather_snow
        in 80..82 -> R.string.weather_rain // Using "Rain" for "Showers"
        in 85..86 -> R.string.weather_snow // Using "Snow" for "Snow Showers"
        in 95..99 -> R.string.weather_thunderstorm
        else -> R.string.weather_partly_cloudy
    }

    // search -> cache -> render (with offline fallback)
    @RequiresApi(Build.VERSION_CODES.O)
    private fun searchAndShow(query: String) {
        lifecycleScope.launch {
            try {
                showLoading(true)

                val places = withContext(Dispatchers.IO) { repo.searchPlaces(query) }
                if (places.isEmpty()) {
                    binding.topAppBar.title = ""
                    binding.tvHeaderTemp.text = "--°"
                    binding.tvHeaderCond.text = getString(R.string.no_results, query)
                    binding.tvHeaderWind.text = getString(R.string.wind_placeholder)
                    hourAdapter.submit(emptyList())
                    dailyAdapter.submit(emptyList())
                    return@launch
                }
                val p = places.first()
                val lat = p.latitude ?: 0.0
                val lon = p.longitude ?: 0.0
                val name = p.toString()
                binding.topAppBar.title = p.name ?: ""

                // Try network first
                val fc = withContext(Dispatchers.IO) { repo.getForecast(lat, lon) }
                lastForecast = fc
                lastUseCelsius = withContext(Dispatchers.IO) { settings.useCelsius.first() }
                lastLat = lat; lastLon = lon

                bindForecast(fc, lastUseCelsius)

                // Save to cache + upsert location, purge old
                withContext(Dispatchers.IO) {
                    cache.upsertLocation(name = name, lat = lat, lon = lon, makeDefault = false)
                    cache.saveForecast(name = name, lat = lat, lon = lon, response = fc)
                    cache.purgeOlderThan(System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000)
                }

            } catch (e: Exception) {
                Timber.e(e, "Search/forecast failed; attempting offline cache")

                val cachedPair = withContext(Dispatchers.IO) {
                    cache.getForecast(query)?.let { Pair(query, it) } ?: run {
                        val all = cache.getAllLocations()
                        val q = query.trim()
                        val exact = all.firstOrNull { it.name.equals(q, ignoreCase = true) }
                        val prefix = exact ?: all.firstOrNull { it.name.startsWith(q, ignoreCase = true) }
                        val contains = prefix ?: all.firstOrNull { it.name.contains(q, ignoreCase = true) }
                        val best = exact ?: prefix ?: contains
                        best?.let { loc ->
                            cache.getForecast(loc.name)?.let { Pair(loc.name, it) }
                        }
                    }
                }

                if (cachedPair != null) {
                    val (cachedName, cached) = cachedPair
                    lastForecast = cached
                    lastUseCelsius = withContext(Dispatchers.IO) { settings.useCelsius.first() }
                    bindForecast(cached, lastUseCelsius)
                    binding.topAppBar.title = cachedName
                } else {
                    binding.topAppBar.title = ""
                    binding.tvHeaderTemp.text = "--°"
                    binding.tvHeaderCond.text = e.localizedMessage ?: "Error"
                    binding.tvHeaderWind.text = getString(R.string.wind_placeholder)
                    hourAdapter.submit(emptyList())
                    dailyAdapter.submit(emptyList())
                }
            } finally {
                showLoading(false)
            }
        }
    }

    // reload default after LocationsActivity signals a change
    @RequiresApi(Build.VERSION_CODES.O)
    private fun reloadDefault() {
        lifecycleScope.launch {
            try {
                showLoading(true)
                val def = withContext(Dispatchers.IO) { cache.getDefaultLocation() }
                if (def != null) {
                    try {
                        val fc = withContext(Dispatchers.IO) {
                            repo.getForecast(def.latitude, def.longitude)
                        }
                        lastForecast = fc
                        lastUseCelsius = withContext(Dispatchers.IO) { settings.useCelsius.first() }
                        bindForecast(fc, lastUseCelsius)
                        binding.topAppBar.title = def.name
                        lastLat = def.latitude; lastLon = def.longitude
                        withContext(Dispatchers.IO) { cache.saveForecast(def.name, def.latitude, def.longitude, fc) }
                    } catch (_: Exception) {
                        val cached = withContext(Dispatchers.IO) { cache.getForecast(def.name) }
                        cached?.let { c ->
                            lastForecast = c
                            lastUseCelsius = withContext(Dispatchers.IO) { settings.useCelsius.first() }
                            bindForecast(c, lastUseCelsius)
                            binding.topAppBar.title = def.name
                            lastLat = def.latitude; lastLon = def.longitude
                        }
                    }
                }
            } finally {
                showLoading(false)
            }
        }
    }

    // single place to (re)paint the UI
    @RequiresApi(Build.VERSION_CODES.O)
    private fun bindForecast(fc: ForecastResponse, useC: Boolean) {
        // Header (current)
        fc.current?.let { cur ->
            val t = cur.temperature ?: 0.0
            binding.tvHeaderTemp.text = formatTemp(t, useC)
            binding.tvHeaderCond.text = getString(weatherDesc(cur.weathercode ?: 0))
            val wind = (cur.windspeed ?: 0.0).roundToInt()
            binding.tvHeaderWind.text = getString(R.string.wind_kmh, wind)
        } ?: run {
            binding.tvHeaderTemp.text = "--°"
            binding.tvHeaderCond.text = getString(R.string.search_city_hint)
            binding.tvHeaderWind.text = getString(R.string.wind_placeholder)
        }

        // Hourly lists
        fun pickDay(dayOffset: Int): List<HourUi> {
            val targetDate = java.time.LocalDate.now().plusDays(dayOffset.toLong()).toString()
            val nowHour = java.time.LocalDateTime.now().hour

            val times = fc.hourly?.time.orEmpty()
            val temps = fc.hourly?.temperature.orEmpty()

            return times.mapIndexedNotNull { i, ts ->
                if (!ts.startsWith(targetDate)) return@mapIndexedNotNull null
                val hour = java.time.LocalDateTime.parse(ts).hour
                val label = if (hour == nowHour) "Now" else ts.substringAfter('T').substring(0, 5)
                val tempC = if (hour == nowHour && fc.current?.temperature != null)
                    fc.current.temperature!! else temps.getOrNull(i) ?: 0.0
                HourUi(time = label, temp = formatTemp(tempC, useC))
            }
        }

        binding.chipYesterday.setOnClickListener { hourAdapter.submit(pickDay(-1)) }
        binding.chipToday.setOnClickListener { hourAdapter.submit(pickDay(0)) }
        binding.chipTomorrow.setOnClickListener { hourAdapter.submit(pickDay(1)) }
        hourAdapter.submit(pickDay(0))

        // Weekly
        val week: List<DayUi> =
            fc.daily?.let { d ->
                if (d.time != null && d.tMax != null && d.tMin != null) {
                    d.time.indices
                        .drop(1)
                        .take(5)
                        .map { i ->
                            val date = java.time.LocalDate.parse(d.time[i])

                            // Create a formatter that uses the phone's default language
                            val dayFormatter = java.time.format.DateTimeFormatter
                                .ofPattern("EEEE", java.util.Locale.getDefault())

                            // Format the date to get the localized day name
                            val label = date.format(dayFormatter)
                                .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }


                            DayUi(
                                label = label,
                                hi = formatTemp(d.tMax[i], useC),
                                lo = formatTemp(d.tMin[i], useC)
                            )
                        }
                } else emptyList()
            } ?: emptyList()

        dailyAdapter.submit(week)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                showSearchOverlay();
                true
            }

            // Save current city
            R.id.action_save_location -> {
                val name = binding.topAppBar.title?.toString()?.trim().orEmpty()
                val lat = lastLat; val lon = lastLon
                if (name.isNotEmpty() && lat != null && lon != null) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        cache.upsertLocation(name = name, lat = lat, lon = lon, makeDefault = false)
                    }
                    Toast.makeText(this, "Saved \"$name\"", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Search a city first to save it", Toast.LENGTH_SHORT).show()
                }
                true
            }

            // Open saved locations screen FOR RESULT
            R.id.action_saved_locations -> {
                locationsLauncher.launch(Intent(this, LocationsActivity::class.java))
                true
            }

            // NEW: share current weather
            R.id.action_share -> {
                shareCurrentWeather()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    // ===== Helpers: notifications + scheduling =====

    private fun requestPostNotificationsIfNeeded() {
        if (Build.VERSION.SDK_INT >= 33) {
            val perm = android.Manifest.permission.POST_NOTIFICATIONS
            if (checkSelfPermission(perm) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(perm), 1002)
            }
        }
    }

    private fun scheduleWeatherAlerts() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = PeriodicWorkRequestBuilder<WeatherAlertWorker>(
            15, java.util.concurrent.TimeUnit.MINUTES
        ).setConstraints(constraints).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "weather-alerts",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    // ===== NEW: build and fire share intent =====
    private fun shareCurrentWeather() {
        val city = binding.topAppBar.title?.toString()?.trim().orEmpty()
        val current = lastForecast?.current

        if (city.isBlank() || current == null) {
            Toast.makeText(this, "Search a city first before sharing", Toast.LENGTH_SHORT).show()
            return
        }

        val tempText = formatTemp(current.temperature ?: 0.0, lastUseCelsius)
        val desc = getString(weatherDesc(current.weathercode ?: 0))
        val wind = (current.windspeed ?: 0.0).roundToInt()

        val text = buildString {
            append("Weather in $city: $tempText, $desc, wind $wind km/h.")
            append("\n\nShared from my Weather app.")
        }

        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        startActivity(Intent.createChooser(sendIntent, "Share via"))
    }
}

/* ===== Overlay Suggestions ===== */

data class PlaceSuggestion(
    val displayName: String,
    val subline: String,
    val weatherCode: Int?
)

private class SuggestionAdapter(
    private val onClick: (PlaceSuggestion) -> Unit
) : RecyclerView.Adapter<SuggestionVH>() {

    private val items = mutableListOf<PlaceSuggestion>()

    fun submitList(list: List<PlaceSuggestion>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    // ===== FIX 2: Corrected 'android.view.ViewGroup' =====
    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): SuggestionVH {
        val v = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_suggestion, parent, false)
        return SuggestionVH(v, onClick)
    }

    override fun onBindViewHolder(holder: SuggestionVH, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}

private class SuggestionVH(
    itemView: android.view.View,
    private val onClick: (PlaceSuggestion) -> Unit
) : RecyclerView.ViewHolder(itemView) {

    private val title = itemView.findViewById<android.widget.TextView>(R.id.tvTitle)
    private val sub = itemView.findViewById<android.widget.TextView>(R.id.tvSubtitle)
    private val weatherIcon = itemView.findViewById<ImageView>(R.id.ivWeather)

    fun bind(item: PlaceSuggestion) {
        title.text = item.displayName
        sub.text = item.subline
        val iconRes = WeatherIcons.fromCode(item.weatherCode)
        weatherIcon.setImageResource(iconRes)
        itemView.setOnClickListener { onClick(item) }
    }
}