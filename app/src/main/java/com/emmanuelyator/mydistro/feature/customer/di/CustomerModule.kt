package com.emmanuelyator.mydistro.feature.customer.di

import com.emmanuelyator.mydistro.BuildConfig
import com.emmanuelyator.mydistro.feature.customer.data.InMemoryCartRepository
import com.emmanuelyator.mydistro.feature.customer.data.MockAddressRepository
import com.emmanuelyator.mydistro.feature.customer.data.MockCatalogRepository
import com.emmanuelyator.mydistro.feature.customer.data.NetworkAddressRepository
import com.emmanuelyator.mydistro.feature.customer.data.MockOrderRepository
import com.emmanuelyator.mydistro.feature.customer.data.NetworkCatalogRepository
import com.emmanuelyator.mydistro.feature.customer.data.NetworkOrderRepository
import com.emmanuelyator.mydistro.feature.customer.domain.AddressRepository
import com.emmanuelyator.mydistro.feature.customer.domain.CartRepository
import com.emmanuelyator.mydistro.feature.customer.domain.CatalogRepository
import com.emmanuelyator.mydistro.feature.customer.domain.OrderRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Catalog and orders follow [BuildConfig.USE_MOCK_DATA]. Cart stays in-memory.
 */
@Module
@InstallIn(SingletonComponent::class)
object CustomerModule {

    @Provides
    @Singleton
    fun provideCatalogRepository(
        mock: MockCatalogRepository,
        network: NetworkCatalogRepository
    ): CatalogRepository = if (BuildConfig.USE_MOCK_DATA) mock else network

    @Provides
    @Singleton
    fun provideOrderRepository(
        mock: MockOrderRepository,
        network: NetworkOrderRepository
    ): OrderRepository = if (BuildConfig.USE_MOCK_DATA) mock else network

    @Provides
    @Singleton
    fun provideAddressRepository(
        mock: MockAddressRepository,
        network: NetworkAddressRepository
    ): AddressRepository = if (BuildConfig.USE_MOCK_DATA) mock else network

    @Provides
    @Singleton
    fun provideCartRepository(impl: InMemoryCartRepository): CartRepository = impl
}
