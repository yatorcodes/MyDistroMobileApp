package com.emmanuelyator.mydistro

import org.junit.Assert.assertThrows
import org.junit.Test

class MyDistroAppMockGuardTest {

    @Test
    fun releaseRefusesMockData() {
        assertThrows(IllegalStateException::class.java) {
            MyDistroApp.refuseMockDataInRelease(useMockData = true, debuggable = false)
        }
    }

    @Test
    fun releaseAllowsRealData() {
        MyDistroApp.refuseMockDataInRelease(useMockData = false, debuggable = false)
    }

    @Test
    fun debugAllowsMockData() {
        MyDistroApp.refuseMockDataInRelease(useMockData = true, debuggable = true)
    }
}
