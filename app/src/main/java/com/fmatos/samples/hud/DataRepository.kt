package com.fmatos.samples.hud

import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager

/**
 * @author : Fabio de Matos
 * @since : 23/11/2019
 **/
class DataRepository(context: Context) {

    private val applicationContext = context.applicationContext


      fun getWifiInfo(): WifiInfo? {
        return applicationContext.getSystemService(WIFI_SERVICE)
            .let { it as? WifiManager }
            ?.connectionInfo

    }

}