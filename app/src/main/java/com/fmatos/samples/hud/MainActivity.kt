package com.fmatos.samples.hud

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.text.format.Formatter
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import org.joda.time.LocalDate
import org.joda.time.LocalTime


class MainActivity : AppCompatActivity() {

    private var TAG: String = MainActivity::class.java.simpleName

    private var handler: Handler? = null
    private var time: String = ""
    private var date: String = ""
    private var ip: String = ""
    private var textBlink: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        handler = Handler(mainLooper)

        updateScreen()
    }

    private fun updateScreen() {

        handler!!.postDelayed(this::updateScreen, 1000)

        updateModel()
        textBlink = !textBlink

        var textOutput: String

        textOutput = time.replace(' ', '!')

        if (textBlink) {
            textOutput = textOutput.replace(':', ' ')
        }

        clock_time_text.text = textOutput
        clock_date_text.text = date
        ip_text.text = ip

        Log.d(TAG,"Message = " + textOutput)
    }

    private fun updateModel() {

        var localTime = LocalTime.now()
        time = localTime.toString("HH:mm")

        var localDate = LocalDate.now()
        date = localDate.toString("dd/MM/yyyy")

        getWifiIp()
    }

    private fun getWifiIp() {
        val wifiMgr = getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiMgr.connectionInfo
        ip = wifiInfo.ssid

        ip += "\n"
        ip += Formatter.formatIpAddress(wifiInfo.ipAddress)

//        var host = InetAddress.getLocalHost() // uses network code, can't call on main thread
//        ip = host.hostAddress

    }
}

