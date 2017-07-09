package com.fmatos.samples.hud

import android.content.Context
import android.graphics.Typeface
import android.net.wifi.WifiManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.format.Formatter
import android.util.Log
import com.bumptech.glide.Glide
import com.fmatos.samples.hud.service.WallpaperService
import com.fmatos.samples.hud.utils.dagger.HudApplication
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.Timed
import kotlinx.android.synthetic.main.activity_main.*
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    private var TAG: String = MainActivity::class.java.simpleName
    private val INTERVAL_1_SECOND_MS: Long = 1000

    private var time: String = ""
    private var date: String = ""
    private var ip: String = ""
    private var textBlink: Boolean = true
    private var test: String = ""

    private val disposables = CompositeDisposable()


    @Inject lateinit
    var wallpaperService: WallpaperService

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        HudApplication.graph.inject(this)


        var clock1Sec = Observable.interval(0, INTERVAL_1_SECOND_MS, TimeUnit.MILLISECONDS)
                .timeInterval()
                .subscribeOn(Schedulers.computation())

        disposables.add(
                clock1Sec
                        .observeOn(Schedulers.computation())
                        .subscribe({ longTimed -> updateModel(longTimed) })
        )

        disposables.add(
                clock1Sec
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ updateScreen() })
        )

        try {
            val face = Typeface.createFromAsset(assets,
                    "fonts/dseg7classic_light.ttf")

            clock_time_text.typeface = face
        } catch (e: RuntimeException) {
            Log.e(TAG, "Can't set custom font: ${e.localizedMessage}")
        }

        addWallpapers()
    }

    private fun addWallpapers() {

        var wallpaperObserver: Observable<String> = wallpaperService
                .getObservable()

        disposables.add(wallpaperObserver.subscribeBy(
                onNext = { url ->
                    Log.i(TAG,"On glide url ${url}")
                    Glide.with(this)
                            .load(url)
                            .centerCrop()
                            .error(R.drawable.rocket_diamonds)
                            .into(background_img)
                },
                onComplete = { Log.i(TAG, "On glide Found complete") },
                onError = { Log.i(TAG,"On glide error")}

        ))
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

    }
}

