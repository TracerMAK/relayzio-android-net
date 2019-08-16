package com.relayzio.android.net

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import java.net.InetAddress
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.NetworkInterface
import java.util.*

class DeviceNetInfo(appContext: Context) {
    // Current connection type
    private var connType: Int? = null
    // Current wifi/mobile IP address
    private var ipv4: String? = null
    private var ipv6: String? = null

    private val connManager = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    init {
        queryNetworkInfo()
    }

    /**
     * Returns the type of network enabled on the device or Null if no network is found.
     * The type will be ConnectivityManager.TYPE_MOBILE or ConnectivityManager.TYPE_WIFI.
     */
    fun connectionType(): Int? {
        return connType
    }

    /**
     * Returns a valid IP for network connections through wifi or mobile. Preference is given
     * for IPv4 over IPv6, if both exist.
     */
    fun ipAddress(): String? {
        if (ipv4 != null)
            return ipv4
        return ipv6
    }

    /**
     * Queries for and retrieves current network information on the device.
     */
    private fun queryNetworkInfo() {
        // Get connection type
        val activeNetwork: NetworkInfo? = connManager.activeNetworkInfo
        connType = activeNetwork?.type

        // Get IP if a network connection has been established
        if (activeNetwork?.isConnected == true) {
            val networks: Enumeration<NetworkInterface> = NetworkInterface.getNetworkInterfaces()
            for (network in networks) {
                val addresses: Enumeration<InetAddress> = network.inetAddresses
                for (address in addresses) {
                    if (!address.isLoopbackAddress) {
                        if (address is Inet4Address)
                            ipv4 = address.hostAddress
                        else if (address is Inet6Address)
                            ipv6 = address.hostAddress
                    }
                }
            }
        }
    }
}