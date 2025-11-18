package com.st10140587.prog7314_poe.data.local

import androidx.room.*

@Dao
interface LocationDao {

    @Query("SELECT * FROM locations ORDER BY isDefault DESC, createdAt DESC")
    suspend fun getAll(): List<LocationEntity>

    @Query("SELECT * FROM locations WHERE isDefault = 1 LIMIT 1")
    suspend fun getDefault(): LocationEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: LocationEntity): Long

    @Update
    suspend fun update(entity: LocationEntity)

    @Delete
    suspend fun delete(entity: LocationEntity)

    @Query("UPDATE locations SET isDefault = 0 WHERE isDefault = 1")
    suspend fun clearDefault()

    @Transaction
    suspend fun setAsDefault(entity: LocationEntity) {
        clearDefault()
        update(entity.copy(isDefault = true))
    }

    @Query("SELECT * FROM locations WHERE name = :name LIMIT 1")
    suspend fun getByName(name: String): LocationEntity?
}
