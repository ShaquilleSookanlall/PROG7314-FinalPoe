package com.st10140587.prog7314_poe.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "forecast_cache")
data class ForecastEntity(
    @PrimaryKey val locationName: String, // use location display name as key
    val latitude: Double,
    val longitude: Double,
    val forecastJson: String,             // store the whole ForecastResponse as JSON
    val updatedAt: Long = System.currentTimeMillis()
)
