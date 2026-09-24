package com.emmanuelyator.mydistro.feature.customer.di

import com.emmanuelyator.mydistro.feature.customer.data.InMemoryCartRepository
import com.emmanuelyator.mydistro.feature.customer.data.MockCatalogRepository
import com.emmanuelyator.mydistro.feature.customer.domain.CartRepository
import com.emmanuelyator.mydistro.feature.customer.domain.CatalogRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface CustomerModule {

    @Binds
    @Singleton
    fun bindCatalogRepository(impl: MockCatalogRepository): CatalogRepository

    @Binds
    @Singleton
    fun bindCartRepository(impl: InMemoryCartRepository): CartRepository
}
