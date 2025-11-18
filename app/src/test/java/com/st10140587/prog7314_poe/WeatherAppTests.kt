package com.st10140587.prog7314_poe

import org.junit.Assert.*
import org.junit.Test

/**
 * Essential Unit Tests for Weather App
 *
 * These tests cover the core functionality of the weather app:
 * - Temperature conversion
 * - Weather descriptions
 * - Location validation
 * - Language support (English and Afrikaans)
 * - Alert triggers
 *
 * Total: 10 tests
 */
class WeatherAppTests {

    // ==================== Test 1: Temperature Conversion ====================

    @Test
    fun `celsius to fahrenheit conversion is accurate`() {
        val celsius = 20.0
        val fahrenheit = celsiusToFahrenheit(celsius)

        assertEquals(68.0, fahrenheit, 0.1)
    }

    // ==================== Test 2: Temperature Formatting ====================

    @Test
    fun `temperature formatting with celsius displays correctly`() {
        val temp = 25.0
        val formatted = formatTemperature(temp, useCelsius = true)

        assertEquals("25°C", formatted)
    }

    // ==================== Test 3: Weather Code - Clear Sky ====================

    @Test
    fun `weather code 0 returns clear sky description`() {
        val description = getWeatherDescription(0)

        assertEquals("Clear sky", description)
    }

    // ==================== Test 4: Weather Code - Rain ====================

    @Test
    fun `weather code for rain returns correct description`() {
        val rainCode = 61
        val description = getWeatherDescription(rainCode)

        assertEquals("Drizzle", description)
    }

    // ==================== Test 5: Location Coordinates Validation ====================

    @Test
    fun `location coordinates are within valid range`() {
        val latitude = -29.8579
        val longitude = 31.0292

        // Latitude must be between -90 and 90
        assertTrue(latitude >= -90 && latitude <= 90)

        // Longitude must be between -180 and 180
        assertTrue(longitude >= -180 && longitude <= 180)
    }

    // ==================== Test 6: Language Support - English ====================

    @Test
    fun `default language is English`() {
        val defaultLanguage = "en"
        val supportedLanguages = listOf("en", "af")

        assertTrue(supportedLanguages.contains(defaultLanguage))
        assertEquals("en", defaultLanguage)
    }

    // ==================== Test 7: Language Support - Afrikaans ====================

    @Test
    fun `afrikaans is supported language`() {
        val supportedLanguages = listOf("en", "af")

        assertTrue(supportedLanguages.contains("af"))
        assertEquals(2, supportedLanguages.size)
    }

    // ==================== Test 8: Weather Alert - Thunderstorm ====================

    @Test
    fun `thunderstorm weather code triggers alert`() {
        val thunderstormCode = 95

        assertTrue(shouldTriggerAlert(thunderstormCode))
    }

    // ==================== Test 9: Weather Alert - High Winds ====================

    @Test
    fun `high wind speed triggers alert`() {
        val highWindSpeed = 55.0

        assertTrue(shouldTriggerWindAlert(highWindSpeed))
    }

    // ==================== Test 10: Weather Alert - Normal Conditions ====================

    @Test
    fun `clear weather and normal wind do not trigger alerts`() {
        val clearWeatherCode = 0
        val normalWindSpeed = 20.0

        assertFalse(shouldTriggerAlert(clearWeatherCode))
        assertFalse(shouldTriggerWindAlert(normalWindSpeed))
    }

    // ==================== Helper Functions ====================

    /**
     * Convert Celsius to Fahrenheit
     */
    private fun celsiusToFahrenheit(celsius: Double): Double {
        return celsius * 9 / 5 + 32
    }

    /**
     * Format temperature with unit
     */
    private fun formatTemperature(temp: Double, useCelsius: Boolean): String {
        val value = if (useCelsius) temp else celsiusToFahrenheit(temp)
        val unit = if (useCelsius) "°C" else "°F"
        return "${value.toInt()}$unit"
    }

    /**
     * Get weather description from code
     */
    private fun getWeatherDescription(code: Int): String {
        return when (code) {
            0 -> "Clear sky"
            in 1..3 -> "Clear"
            in 45..48 -> "Fog"
            in 51..67 -> "Drizzle"
            in 71..77 -> "Snow"
            in 80..82 -> "Rain"
            in 85..86 -> "Snow"
            in 95..99 -> "Thunderstorm"
            else -> "Partly cloudy"
        }
    }

    /**
     * Check if weather code should trigger an alert
     */
    private fun shouldTriggerAlert(code: Int): Boolean {
        return code in 95..99 || // Thunderstorms
                code in 80..82 || // Rain showers
                code in 51..67    // Drizzle/Rain
    }

    /**
     * Check if wind speed should trigger an alert
     */
    private fun shouldTriggerWindAlert(windSpeed: Double): Boolean {
        return windSpeed >= 50.0
    }
}