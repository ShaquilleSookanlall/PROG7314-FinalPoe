package com.st10140587.prog7314_poe.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Provides Retrofit instances for the two Open-Meteo hosts:
 *  - Forecast:   https://api.open-meteo.com/
 *  - Geocoding:  https://geocoding-api.open-meteo.com/
 */
object RetrofitInstance {

    private fun client(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
            .build()

    private fun retrofit(baseUrl: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val forecastApi: WeatherApiService by lazy {
        retrofit("https://api.open-meteo.com/").create(WeatherApiService::class.java)
    }

    val geocodingApi: GeocodingApiService by lazy {
        retrofit("https://geocoding-api.open-meteo.com/").create(GeocodingApiService::class.java)
    }
}
