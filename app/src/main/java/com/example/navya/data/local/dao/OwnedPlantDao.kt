package com.example.navya.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.navya.data.local.entity.OwnedPlantEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OwnedPlantDao {
    @Query("SELECT * FROM owned_plants WHERE user_id = :userId")
    fun getOwnedPlantsForUser(userId: String): Flow<List<OwnedPlantEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOwnedPlant(ownedPlant: OwnedPlantEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(ownedPlants: List<OwnedPlantEntity>)

    @Query("DELETE FROM owned_plants WHERE user_id = :userId")
    suspend fun clearOwnedPlantsForUser(userId: String)
}
