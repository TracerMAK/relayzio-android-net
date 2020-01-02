package com.relayzio.android.net

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import java.io.BufferedInputStream
import java.io.InputStream
import java.net.*
import java.util.*
import kotlin.text.Charsets.UTF_8

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

        if (connectionIsAvailable()) {
            when (connType) {
                ConnectivityManager.TYPE_WIFI -> getWifiIP()
                ConnectivityManager.TYPE_MOBILE -> getMobileIP()
            }
        }
    }

    /**
     * Checks for a network connection on the device and returns true if the
     * connection is ready for data transfer.
     */
    private fun connectionIsAvailable() : Boolean {
        // Get connection type
        val activeNetwork: NetworkInfo? = connManager.activeNetworkInfo
        connType = activeNetwork?.type

        return (activeNetwork?.isConnected == true)
    }

    /**
     * Gets the mobile IP of the device assuming no wifi connection is available.
     */
    private fun getMobileIP() {
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

    /**
     * Provides methods to retrieve the public wifi IP of the device. Options may change depending on
     * newly discovered ways to get the information.
     */
    private fun getWifiIP() {
        // Option 1 : Send a request to http://checkip.amazonaws.com/ which returns the public IP.
        try {
            queryCheckAWS()
        } catch (e: UnknownHostException) {

        }
    }

    /**
     * Queries the checkip service provided by AWS to get the public IP of the request.
     */
    private fun queryCheckAWS() {
        val url = URL("http://checkip.amazonaws.com/")
        val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
        val buffer = mutableListOf<Byte>()

        try {
            val input: InputStream = BufferedInputStream(urlConnection.inputStream)
            input.use {
                var next = it.read()
                while (next > -1) {
                    buffer.add(next.toByte())
                    next = it.read()
                }
            }
        } finally {
            urlConnection.disconnect()
        }

        // Remove trailing new line
        buffer.removeAt(buffer.size - 1)
        val s = buffer.toByteArray().toString(UTF_8)
        val address = InetAddress.getByName(s)

        if (address is Inet4Address)
            ipv4 = address.hostAddress
        else if (address is Inet6Address)
            ipv6 = address.hostAddress
    }
}