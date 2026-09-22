package com.emmanuelyator.mydistro.core.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {

    @Query("SELECT * FROM cached_trips ORDER BY cachedAtEpochMillis DESC")
    fun observeTrips(): Flow<List<TripEntity>>

    @Query("SELECT * FROM cached_trips WHERE id = :tripId")
    suspend fun getTrip(tripId: String): TripEntity?

    @Upsert
    suspend fun upsertAll(trips: List<TripEntity>)

    @Query("DELETE FROM cached_trips")
    suspend fun clear()

    /** Replaces the whole cache atomically so the list is never partially stale. */
    @Transaction
    suspend fun replaceAll(trips: List<TripEntity>) {
        clear()
        upsertAll(trips)
    }
}
