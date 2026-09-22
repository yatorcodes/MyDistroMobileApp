package com.emmanuelyator.mydistro.feature.driver.di

import com.emmanuelyator.mydistro.feature.driver.data.MockTripRepository
import com.emmanuelyator.mydistro.feature.driver.domain.TripRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Swap this binding to a Retrofit-backed repository once the trips API exists.
 * Singleton scope matters: the mock keeps trip state in memory, so the trip
 * list, details and confirmation screens must share one instance.
 */
@Module
@InstallIn(SingletonComponent::class)
interface DriverModule {

    @Binds
    @Singleton
    fun bindTripRepository(impl: MockTripRepository): TripRepository
}
