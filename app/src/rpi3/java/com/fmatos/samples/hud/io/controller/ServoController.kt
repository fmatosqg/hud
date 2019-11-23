package com.fmatos.samples.hud.io.controller

import android.util.Log
import com.google.android.things.pio.PeripheralManager
import com.google.android.things.pio.Pwm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException

/**
 * Created by fabio.goncalves on 23/01/2017.
 */

class ServoController(
    pinName: String,
    private val periodMs: Float,
    private val maxTimeMs: Float,
    private val minTimeMs: Float
) {

    private val pin: Pwm


    private var pulseLenghtMs = 0f

    constructor(pin: String) : this(pin, 20f, 2.5f, 0.5f)

    init {
        val service = PeripheralManager.getInstance()
        pin = service.openPwm(pinName)
        setup(pinName)

    }

    private fun setup(pinName: String) {
        try {


            pin.setPwmFrequencyHz(1000.0 / periodMs)
            setPosition(90f)
            pin.setEnabled(true)


            Log.i("Servo Controller", "Initialize pin $pinName")

        } catch (e: IOException) {
            Log.e("Servo Controller", "Can't initialize pin $pinName")
            e.printStackTrace()
        }

    }

    /**
     * assumes that min position is 0 degrees and max is 180
     */
    fun setPosition(degrees: Float) {

        if (degrees > 90) {
            pulseLenghtMs = minTimeMs
        } else {
            pulseLenghtMs = maxTimeMs
        }

        pulseLenghtMs = degrees / 180f * (maxTimeMs - minTimeMs) + minTimeMs

        if (pulseLenghtMs < minTimeMs) {
            pulseLenghtMs = minTimeMs
        } else if (pulseLenghtMs > maxTimeMs) {
            pulseLenghtMs = maxTimeMs
        }

        val dutyCycle = pulseLenghtMs / periodMs * 100f

        Timber.v(
            "Degrees %s Duty cycle %s - pulse lenght =  %s",
            degrees.format(2),
            dutyCycle.format(2),
            pulseLenghtMs.format(2)
        )


        try {
            pin.setPwmDutyCycle(dutyCycle.toDouble())
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    companion object {

        private val TAG = ServoController::class.java.simpleName


        val pwm0Pin: String
            get() = "PWM0"

        val pwm1Pin: String
            get() = "PWM1"
    }
}

fun Float.format(digits: Int) = java.lang.String.format("%.${digits}f", this)