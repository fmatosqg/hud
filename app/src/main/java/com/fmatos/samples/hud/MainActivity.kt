package com.fmatos.samples.hud

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.format.Formatter
import android.util.Log
import com.bumptech.glide.Glide
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.Timed
import kotlinx.android.synthetic.main.activity_main.*
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private var TAG: String = MainActivity::class.java.simpleName
    private val INTERVAL_1_SECOND_MS: Long = 1000

    private var time: String = ""
    private var date: String = ""
    private var ip: String = ""
    private var textBlink: Boolean = true
    private var test: String = ""

    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Glide.with(this)
                .load("https://s-media-cache-ak0.pinimg.com/originals/28/47/ed/2847edeab41b3d90a849c68340b5be3a.jpg")
                .centerCrop()
                .error(R.drawable.rocket_diamonds)
                .into(background_img);

        var clock1Sec = Observable.interval(INTERVAL_1_SECOND_MS, TimeUnit.MILLISECONDS)
                .timeInterval()
                .subscribeOn(Schedulers.computation())

        clock1Sec
                .observeOn(Schedulers.computation())
                .subscribe({ longTimed -> updateModel(longTimed) })

        clock1Sec
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ updateScreen() })

    }

    override fun onDestroy() {
        disposables.clear()
        super.onDestroy()
    }

    private fun updateScreen() {

        textBlink = !textBlink

        var timeView: String = time

//        timeView = time.replace(' ', '!')

        if (textBlink) {
            timeView = timeView.replace(':', ' ')
        }

        clock_date_text.text = date

        clock_time_text.text = timeView

        ip_text.text = ip

        test_text.text = test

        Log.d(TAG, "Message = " + timeView)
    }

    private fun updateModel(longTimed: Timed<Long>) {

//        val dateTime = DateTime.now().toDateTime(DateTimeZone.forOffsetHours(-3))
//        val locale = java.util.Locale("es", "AR")
        val dateTime = DateTime.now().toDateTime(DateTimeZone.forOffsetHours(+10))
        val locale = java.util.Locale("en", "AU")

//        time = DateTimeFormat.forPattern("h:mm a").withLocale(locale).print(dateTime)
        time = DateTimeFormat.forPattern("h:mm").withLocale(locale).print(dateTime)
        date = DateTimeFormat.forPattern("EEEEE, dd MMMM yyyy").withLocale(locale).print(dateTime)

        test = "${longTimed.value()}"
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

