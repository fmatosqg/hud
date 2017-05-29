package com.fmatos.samples.hud

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.text.format.Formatter
import android.util.Log
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*
import org.joda.time.DateTime
import org.joda.time.DateTimeZone


class MainActivity : AppCompatActivity() {

    private var TAG: String = MainActivity::class.java.simpleName
    private val INTERVAL_1_SECOND_MS: Long = 1000

    private var handler: Handler? = null
    private var time: String = ""
    private var date: String = ""
    private var ip: String = ""
    private var textBlink: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        handler = Handler(mainLooper)

        Glide.with(this)
                .load("https://s-media-cache-ak0.pinimg.com/originals/28/47/ed/2847edeab41b3d90a849c68340b5be3a.jpg")
                .centerCrop()
                .error(R.drawable.rocket_diamonds)
                .into(background_img);

        updateScreen()
    }

    private fun updateScreen() {

        handler!!.postDelayed(this::updateScreen, INTERVAL_1_SECOND_MS)

        updateModel()
        textBlink = !textBlink

        var timeView: String = time

//        timeView = time.replace(' ', '!')

        if (textBlink) {
            timeView = timeView.replace(':', ' ')
        }

        clock_date_text.text = date

        clock_time_text.text = timeView

        ip_text.text = ip

        Log.d(TAG, "Message = " + timeView)
    }

    private fun updateModel() {

        val dateTime = DateTime.now().toDateTime(DateTimeZone.forOffsetHours(-3))

        time = dateTime.toString("h:mm a")
        date = dateTime.toString("EEEEE, dd MMM yyyy")

        getWifiIp()
    }

    private fun getWifiIp() {
        val wifiMgr = getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiMgr.connectionInfo
        ip = wifiInfo.ssid

        ip += Formatter.formatIpAddress(wifiInfo.ipAddress)

//        var host = InetAddress.getLocalHost() // uses network code, can't call on main thread
//        ip = host.hostAddress

    }
}

