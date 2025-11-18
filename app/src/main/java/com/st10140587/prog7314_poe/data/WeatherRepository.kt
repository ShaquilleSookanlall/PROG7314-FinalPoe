package com.st10140587.prog7314_poe.data

import com.st10140587.prog7314_poe.data.network.RetrofitInstance

class WeatherRepository {
    private val geo = RetrofitInstance.geocodingApi
    private val api = RetrofitInstance.forecastApi

    suspend fun searchPlaces(q: String) = geo.searchPlaces(q).results.orEmpty()
    suspend fun getForecast(lat: Double, lon: Double) = api.getForecast(lat, lon)
}
