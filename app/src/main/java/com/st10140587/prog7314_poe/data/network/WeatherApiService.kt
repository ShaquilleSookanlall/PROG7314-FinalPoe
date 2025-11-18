package com.st10140587.prog7314_poe.data.network

import com.st10140587.prog7314_poe.data.model.ForecastResponse
import com.st10140587.prog7314_poe.data.model.GeocodingResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Forecast endpoints — Open-Meteo (no API key required)
 * Docs: https://open-meteo.com/en/docs
 */
interface WeatherApiService {
    @GET("v1/forecast")
    suspend fun getForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current_weather") currentWeather: Boolean = true,
        @Query("hourly") hourly: String = "temperature_2m,weathercode",
        @Query("daily") daily: String = "temperature_2m_max,temperature_2m_min,precipitation_sum",
        @Query("past_days") pastDays: Int = 1,
        @Query("forecast_days") forecastDays: Int = 7,   // ✅ was 2
        @Query("timezone") timezone: String = "auto"
    ): ForecastResponse

}

/**
 * Geocoding endpoints — Open-Meteo
 * Docs: https://open-meteo.com/en/docs/geocoding-api
 */
interface GeocodingApiService {
    @GET("v1/search")
    suspend fun searchPlaces(
        @Query("name") query: String,
        @Query("count") count: Int = 10,
        @Query("language") language: String = "en",
        @Query("format") format: String = "json"
    ): GeocodingResponse
}
