package com.emmanuelyator.mydistro.core.network

import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.emmanuelyator.mydistro.BuildConfig
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            // BODY logging prints auth headers and, once M-Pesa is wired up,
            // payment payloads. It must never be on in a shipped build.
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BASIC
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            // Kenyan mobile data can be slow; fail in a bounded time rather
            // than leaving a driver staring at a spinner on the roadside.
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        // Omit nulls so Paystack initiate does not send phoneNumber: null.
        val gson = GsonBuilder().create()
        val baseUrl = BuildConfig.BASE_URL.let { if (it.endsWith("/")) it else "$it/" }
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideTripApiService(retrofit: Retrofit): com.emmanuelyator.mydistro.feature.driver.data.TripApiService =
        retrofit.create(com.emmanuelyator.mydistro.feature.driver.data.TripApiService::class.java)

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): com.emmanuelyator.mydistro.feature.auth.data.AuthApiService =
        retrofit.create(com.emmanuelyator.mydistro.feature.auth.data.AuthApiService::class.java)

    @Provides
    @Singleton
    fun provideCustomerApiService(retrofit: Retrofit): com.emmanuelyator.mydistro.feature.customer.data.CustomerApiService =
        retrofit.create(com.emmanuelyator.mydistro.feature.customer.data.CustomerApiService::class.java)
}