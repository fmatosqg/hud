package com.fmatos.samples.hud

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.format.Formatter
import android.view.View
import com.bumptech.glide.Glide
import com.fmatos.samples.hud.service.AlertService
import com.fmatos.samples.hud.service.CountdownService
import com.fmatos.samples.hud.service.FontCache
import com.fmatos.samples.hud.service.WallpaperService
import com.fmatos.samples.hud.utils.AndroidLogger
import com.fmatos.samples.hud.utils.dagger.HudApplication
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
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
    private var alertText: String? = ""
    private var countdownText: String? = ""
    private var isAm: Boolean? = null // true means AM, false means PM, null means don't show

    private val disposables = CompositeDisposable()

    @Inject lateinit
    var androidLogger: AndroidLogger

    @Inject lateinit
    var wallpaperService: WallpaperService

    @Inject lateinit
    var fontCache: FontCache

    @Inject lateinit var
            timezone: DateTimeZone

    @Inject lateinit var
            alertService: AlertService

    @Inject lateinit var
            countdownService: CountdownService

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
            val typeface = fontCache.get("fonts/dseg7classic_light.ttf",this)

            clock_time_text.typeface = typeface
        } catch (e: RuntimeException) {
            androidLogger.e(TAG, "Can't set custom font: %s", e.localizedMessage)
        }

        addWallpapers()
    }

    private fun addWallpapers() {

        var wallpaperObserver: Observable<String> = wallpaperService
                .getObservable()

        disposables.add(wallpaperObserver.subscribeBy(
                onNext = { url ->
                    androidLogger.i(TAG, "On glide url ${url}")
                    Glide.with(this)
                            .load(url)
                            .centerCrop()
                            .error(R.drawable.rocket_diamonds)
                            .into(background_img)
                },
                onComplete = { androidLogger.i(TAG, "On glide Found complete") },
                onError = { androidLogger.i(TAG, "On glide error") }

        ))
    }


    override fun onDestroy() {
        disposables.clear()
        super.onDestroy()
    }

    private fun updateScreen() {

        textBlink = !textBlink

        var timeView: String = time

        if (textBlink) {
            timeView = timeView.replace(':', ' ')
        }

        clock_date_text.text = date

        clock_time_text.text = timeView

        ip_text.text = ip

        test_text.text = test

        if (isAm == null) {
            label_am.visibility = View.GONE
            label_pm.visibility = View.GONE

        } else if (isAm ?: true) {
            label_am.visibility = View.VISIBLE
            label_pm.visibility = View.INVISIBLE

        } else {
            label_am.visibility = View.INVISIBLE
            label_pm.visibility = View.VISIBLE

        }

        if (alertText != null) {
            alert_text.text = alertText
            alert_text.visibility =
                    if (textBlink) View.INVISIBLE
                    else View.VISIBLE

            countdown_text.visibility = View.GONE
        } else {
            alert_text.visibility = View.GONE

            if (countdownText?.isNotEmpty() ?: false) {
                countdown_text.text = countdownText
                countdown_text.visibility = View.VISIBLE
            } else {
                countdown_text.visibility = View.GONE
            }
        }
    }


    private fun updateModel(longTimed: Timed<Long>) {

//        val dateTime = DateTime.now().toDateTime(DateTimeZone.forOffsetHours(-3))
//        val locale = java.util.Locale("es", "AR")
        val dateTime = DateTime.now().toDateTime(timezone)
        val locale = java.util.Locale("en", "AU")

//        time = DateTimeFormat.forPattern("h:mm a").withLocale(locale).print(dateTime)
        time = DateTimeFormat.forPattern("h:mm").withLocale(locale).print(dateTime)
        date = DateTimeFormat.forPattern("EEEEE, dd MMMM yyyy").withLocale(locale).print(dateTime)

        val ampm = DateTimeFormat.forPattern("a").withLocale(locale).print(dateTime)

        isAm = ampm?.equals("AM")

        test = "${longTimed.value()}"
        getWifiIp()

        alertText = alertService.getAlert()

        countdownText = countdownService.getText()

    }

    private fun getWifiIp() {
        val wifiMgr = getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiMgr.connectionInfo
        ip = wifiInfo.ssid

        ip += Formatter.formatIpAddress(wifiInfo.ipAddress)

    }
}

