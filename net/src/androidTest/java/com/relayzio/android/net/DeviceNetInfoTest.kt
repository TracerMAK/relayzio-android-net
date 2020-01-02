/**
 * For log output use the following adb command:
 *   adb logcat RelayzIO_android_net:D *:S
 */

package com.relayzio.net

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.relayzio.android.net.DeviceNetInfo
import org.junit.Assert
import org.junit.Assume
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class DeviceNetInfoTest {
    // Context should never be null so just use lazy initialization.
    private lateinit var context: Context
    private var connectionType: Int? = null

    /**
     * Determine if a connection is present before running tests. Save
     * the connection type for future tests. If not connection type is
     * available, tests won't run.
     */
    @Before
    fun getConnectionType() {
        // Context of the app under test.
        context = InstrumentationRegistry.getInstrumentation().targetContext
        // Get the service for network connections and check for active network.
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        connectionType = activeNetwork?.type
    }

    /**
     * Test that a mobile network can be found on the device as long as it is
     * enabled and wifi is off. If the mobile network is not enabled or wifi
     * is on, the test will be ignored.
     */
    @Test
    fun mobileConnectionPresent() {
        val info = DeviceNetInfo(context)
        Assert.assertNotNull(info.connectionType())
        Assume.assumeTrue(connectionType == ConnectivityManager.TYPE_MOBILE)

        Assert.assertEquals(connectionType, info.connectionType())
    }

    /**
     * Test that a wifi network can be found if enabled. If wifi is turned
     * off on the device, the test will be ignored.
     */
    @Test
    fun wifiConnectionPresent() {
        val info = DeviceNetInfo(context)
        Assert.assertNotNull(info.connectionType())
        Assume.assumeTrue(connectionType == ConnectivityManager.TYPE_WIFI)

        Assert.assertEquals(connectionType, info.connectionType())
    }

    /**
     * Check that a valid IP address can be obtained if a wifi or mobile network
     * is enabled and a connection to the network is present. If no data transfer
     * connection is found or both networks are disabled then a null String is
     * given and the test is ignored. The localhost IP is not considered acceptable.
     */
    @Test
    fun validIPConnection() {
        val info = DeviceNetInfo(context)
        Assume.assumeNotNull(info.connectionType())
        Assert.assertNotNull(info.ipAddress())
        Log.d("RelayzIO_android_net", "IP is ${info.ipAddress()}")
    }
}