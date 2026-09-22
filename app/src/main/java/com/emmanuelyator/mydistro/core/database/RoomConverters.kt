package com.emmanuelyator.mydistro.core.database

import androidx.room.TypeConverter
import com.emmanuelyator.mydistro.core.model.TripItem
import com.emmanuelyator.mydistro.core.model.TripStatus
import com.emmanuelyator.mydistro.core.model.TripStop
import com.emmanuelyator.mydistro.core.model.TripType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Gson is already on the classpath for Retrofit, so it is reused here instead of
 * pulling in a second serialisation library.
 */
class RoomConverters {

    @TypeConverter
    fun fromTripType(value: TripType): String = value.name

    @TypeConverter
    fun toTripType(value: String): TripType =
        runCatching { TripType.valueOf(value) }.getOrDefault(TripType.DELIVERY)

    @TypeConverter
    fun fromTripStatus(value: TripStatus): String = value.name

    @TypeConverter
    fun toTripStatus(value: String): TripStatus =
        runCatching { TripStatus.valueOf(value) }.getOrDefault(TripStatus.SCHEDULED)

    @TypeConverter
    fun fromStopList(value: List<TripStop>): String = gson.toJson(value)

    @TypeConverter
    fun toStopList(value: String): List<TripStop> =
        runCatching { gson.fromJson<List<TripStop>>(value, stopListType) }
            .getOrNull() ?: emptyList()

    @TypeConverter
    fun fromItemList(value: List<TripItem>): String = gson.toJson(value)

    @TypeConverter
    fun toItemList(value: String): List<TripItem> =
        runCatching { gson.fromJson<List<TripItem>>(value, itemListType) }
            .getOrNull() ?: emptyList()

    private companion object {
        val gson = Gson()
        val stopListType = object : TypeToken<List<TripStop>>() {}.type
        val itemListType = object : TypeToken<List<TripItem>>() {}.type
    }
}
