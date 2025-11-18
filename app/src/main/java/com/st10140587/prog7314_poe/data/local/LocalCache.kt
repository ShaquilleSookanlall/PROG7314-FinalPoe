package com.st10140587.prog7314_poe.data.local

import android.content.Context
import com.google.gson.Gson
import com.st10140587.prog7314_poe.data.model.ForecastResponse

class LocalCache(context: Context) {

    private val db = AppDb.get(context)
    private val locations = db.locationDao()
    private val forecasts = db.forecastDao()
    private val gson = Gson()

    // ----- Locations -----
    suspend fun upsertLocation(name: String, lat: Double, lon: Double, makeDefault: Boolean = false) {
        val existing = locations.getByName(name)
        if (existing == null) {
            val id = locations.insert(LocationEntity(name = name, latitude = lat, longitude = lon, isDefault = makeDefault))
            if (makeDefault && id > 0) {
                val created = locations.getByName(name) ?: return
                locations.setAsDefault(created)
            }
        } else {
            val updated = existing.copy(latitude = lat, longitude = lon, isDefault = existing.isDefault || makeDefault)
            locations.update(updated)
            if (makeDefault) locations.setAsDefault(updated.copy(isDefault = true))
        }
    }

    suspend fun setDefault(name: String) {
        val target = locations.getByName(name) ?: return
        locations.setAsDefault(target.copy(isDefault = true))
    }

    suspend fun getDefaultLocation(): LocationEntity? = locations.getDefault()
    suspend fun getAllLocations(): List<LocationEntity> = locations.getAll()
    suspend fun deleteLocation(name: String) {
        locations.getByName(name)?.let { locations.delete(it) }
        forecasts.deleteByName(name)
    }

    // ===== THIS IS THE NEW FUNCTION YOU NEED TO ADD =====
    /**
     * Finds a location by its exact name (used by MainActivity to check if saved).
     */
    suspend fun getByName(name: String): LocationEntity? = locations.getByName(name)
    // ====================================================

    // ----- Forecast cache -----
    suspend fun saveForecast(name: String, lat: Double, lon: Double, response: ForecastResponse) {
        val json = gson.toJson(response)
        forecasts.upsert(ForecastEntity(locationName = name, latitude = lat, longitude = lon, forecastJson = json))
    }

    suspend fun getForecast(name: String): ForecastResponse? {
        val row = forecasts.getByName(name) ?: return null
        return try { gson.fromJson(row.forecastJson, ForecastResponse::class.java) } catch (_: Exception) { null }
    }

    suspend fun purgeOlderThan(ms: Long) {
        forecasts.purgeOlderThan(ms)
    }
}