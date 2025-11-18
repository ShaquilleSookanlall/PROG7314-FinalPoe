package com.st10140587.prog7314_poe.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "locations")
data class LocationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,          // e.g., "Durban, South Africa"
    val latitude: Double,
    val longitude: Double,
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
