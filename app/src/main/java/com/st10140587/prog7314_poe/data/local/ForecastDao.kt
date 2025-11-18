package com.st10140587.prog7314_poe.data.local

import androidx.room.*

@Dao
interface ForecastDao {
    @Query("SELECT * FROM forecast_cache WHERE locationName = :name LIMIT 1")
    suspend fun getByName(name: String): ForecastEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: ForecastEntity)

    @Query("DELETE FROM forecast_cache WHERE locationName = :name")
    suspend fun deleteByName(name: String)

    @Query("DELETE FROM forecast_cache WHERE updatedAt < :olderThan")
    suspend fun purgeOlderThan(olderThan: Long)
}
