package com.emmanuelyator.mydistro.feature.auth.di

import com.emmanuelyator.mydistro.feature.auth.data.MockAuthRepository
import com.emmanuelyator.mydistro.feature.auth.domain.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * The single seam between mock and real authentication.
 *
 * When the backend contract lands, add a `RetrofitAuthRepository` and point this
 * binding at it. That one-line change switches the whole app over.
 */
@Module
@InstallIn(SingletonComponent::class)
interface AuthModule {

    @Binds
    @Singleton
    fun bindAuthRepository(impl: MockAuthRepository): AuthRepository
}
