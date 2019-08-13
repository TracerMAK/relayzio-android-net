package com.relayzio.net

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assume
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class DeviceNetInfoTest {
    private var connectionType: Int? = null

    /**
     * Determine if a connection is present before running tests. Save
     * the connection type for future tests. If not connection type is
     * available, tests won't run.
     */
    @Before
    fun getConnectionType() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        // Get the service for network connections and check for active network.
        val cm = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        connectionType = activeNetwork?.type
    }

    @Test
    fun mobileConnectionPresent() {
        //val info = DeviceNetInfo()
        //Assert.assertNotNull(info)

        Assume.assumeTrue(connectionType == ConnectivityManager.TYPE_MOBILE)
    }

    @Test
    fun wifiConnectionPresent() {
        Assume.assumeTrue(connectionType == ConnectivityManager.TYPE_WIFI)
    }
}