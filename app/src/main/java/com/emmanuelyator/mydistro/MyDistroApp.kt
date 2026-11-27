package com.emmanuelyator.mydistro

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyDistroApp : Application() {
    override fun onCreate() {
        super.onCreate()
        refuseMockDataInRelease()
    }

    companion object {
        /**
         * Release builds must never run with mock repositories. Debug may opt in
         * via local.properties USE_MOCK_DATA=true.
         */
        fun refuseMockDataInRelease(
            useMockData: Boolean = BuildConfig.USE_MOCK_DATA,
            debuggable: Boolean = BuildConfig.DEBUG,
        ) {
            if (!debuggable && useMockData) {
                throw IllegalStateException(
                    "USE_MOCK_DATA must be false in release builds. " +
                        "Mock/demo data is blocked for production."
                )
            }
        }
    }
}
