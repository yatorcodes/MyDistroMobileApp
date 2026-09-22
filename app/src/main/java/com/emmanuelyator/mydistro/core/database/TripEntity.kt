package com.emmanuelyator.mydistro.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.emmanuelyator.mydistro.core.model.Trip
import com.emmanuelyator.mydistro.core.model.TripItem
import com.emmanuelyator.mydistro.core.model.TripStatus
import com.emmanuelyator.mydistro.core.model.TripStop
import com.emmanuelyator.mydistro.core.model.TripType

/**
 * Offline cache of the driver's trips so the dashboard can render instantly on
 * launch and survive a dead network. Stops and items are stored as serialised
 * columns rather than child tables: this is a read-through cache that is always
 * replaced wholesale, so there is nothing to gain from joins.
 *
 * The backend remains the source of truth. Never treat cached rows as
 * authoritative for delivery confirmation.
 */
@Entity(tableName = "cached_trips")
data class TripEntity(
    @PrimaryKey val id: String,
    val tripNumber: String,
    val type: TripType,
    val status: TripStatus,
    val originName: String,
    val originAddress: String,
    val originLatitude: Double,
    val originLongitude: Double,
    val scheduledStartLabel: String,
    val totalDistanceKm: Int,
    val stops: List<TripStop>,
    val itemsOnBoard: List<TripItem>,
    /** Lets us show "last updated" and expire stale cache entries. */
    val cachedAtEpochMillis: Long
)

fun TripEntity.toDomain(): Trip = Trip(
    id = id,
    tripNumber = tripNumber,
    type = type,
    status = status,
    origin = com.emmanuelyator.mydistro.core.model.TripLocation(
        name = originName,
        address = originAddress,
        latitude = originLatitude,
        longitude = originLongitude
    ),
    stops = stops.sortedBy { it.sequence },
    itemsOnBoard = itemsOnBoard,
    scheduledStartLabel = scheduledStartLabel,
    totalDistanceKm = totalDistanceKm
)

fun Trip.toEntity(cachedAtEpochMillis: Long): TripEntity = TripEntity(
    id = id,
    tripNumber = tripNumber,
    type = type,
    status = status,
    originName = origin.name,
    originAddress = origin.address,
    originLatitude = origin.latitude,
    originLongitude = origin.longitude,
    scheduledStartLabel = scheduledStartLabel,
    totalDistanceKm = totalDistanceKm,
    stops = stops,
    itemsOnBoard = itemsOnBoard,
    cachedAtEpochMillis = cachedAtEpochMillis
)
