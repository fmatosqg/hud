package com.fmatos.samples.hud

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.format.Formatter
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.fmatos.samples.hud.io.controller.EyesController
import com.fmatos.samples.hud.io.controller.ServoController
import com.fmatos.samples.hud.io.controller.VibratorController
import com.fmatos.samples.hud.service.*
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

    private val TAG: String = MainActivity::class.java.simpleName
    private val INTERVAL_1_SECOND_MS: Long = 1000

    private var time: String = ""
    private var date: String = ""
    private var ip: String = ""
    private var textBlink: Boolean = true
    private var test: String = ""
    private var alertText: String? = ""
    private var countdownText: String? = ""
    private var isAm: Boolean? = null // true means AM, false means PM, null means don't show

    private var servoAngle = 90

    private val disposables = CompositeDisposable()

    @Inject
    lateinit
    var androidLogger: AndroidLogger

    @Inject
    lateinit
    var wallpaperService: WallpaperService

    @Inject
    lateinit
    var fontCache: FontCache

    @Inject
    lateinit var
            timezone: DateTimeZone

    @Inject
    lateinit var
            alertService: AlertService

    @Inject
    lateinit var
            countdownService: CountdownService

    @Inject
    lateinit var
            servoController: ServoController

    @Inject
    lateinit var eyesController: EyesController

    @Inject
    lateinit var vibratorController: VibratorController

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
            val typeface = fontCache.get("fonts/dseg7classic_light.ttf", this)

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

        servoAngle = getServoAngle()

        servoController.setPosition(servoAngle.toDouble())

        processVibration()

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

        antiBurnIn()
        addGlow()
    }

    /**
     * Moves the container slightly to avoid display burn in
     */
    private fun antiBurnIn() {
        val minute = DateTime
                .now()
                .toDateTime(timezone)
                .minuteOfHour()
                .get()

        val offsetX = ((minute % 10L) - 5) * 20f
        val offsetY = ((minute / 6) - 3) * 20f

        container
                .animate()
                .translationX(offsetX)
                .translationY(offsetY)
                .setDuration(300)
                .start()


    }

    private fun processVibration() {

        val dateTime = DateTime.now().toDateTime(timezone)
        val seconds = dateTime.secondOfMinute().get()

        if (seconds == 0) {
            vibratorController.buzz(2000)
        }
    }

    /**
     * Returns angle to the servo controller in degrees
     */
    private fun getServoAngle(): Int {

        // for 49 minutes it stays down
        // at minute 50 it slowly goes up
        // from minute 51 to 59 it will go down until it's all down

        val minute: Int = DateTime().minuteOfHour().get()
        val second: Int = DateTime().secondOfMinute().get()

        val minAngle = 30.toFloat()
        val maxAngle = 120.toFloat()

        val angle: Float = when {

            minute == 50 -> {
                val alpha = -(maxAngle - minAngle) / 60.0f
                minAngle + alpha * (second)
            }

            minute < 50 -> maxAngle
            minute > 59 -> maxAngle

            else -> {
                val alpha = (maxAngle - minAngle) / 10.0f // everything happens over 10 minutes
                minAngle + alpha * (minute - 50) // offset by 50 minutes
            }
        }

        val isEyesControllerRunning =
                when {
                    minute > 59 -> false
                    minute >= 50 -> true
                    else -> false
                }

        if (isEyesControllerRunning) {
            eyesController.start()
        } else {
            eyesController.stop()
        }

        Log.i(TAG, "Servo angle will be ${angle.toInt()} on minute $minute -- $isEyesControllerRunning")

        return angle.toInt()

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

    private fun addGlow() {
        val gh = GlowHelper(this)

        gh.setBackgroundGlow(container, R.drawable.rounded_rectangle, resources.getColor(R.color.glowingYellow, null))

    }
}

