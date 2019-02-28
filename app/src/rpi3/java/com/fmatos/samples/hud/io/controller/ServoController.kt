package com.fmatos.samples.hud.io.controller

import android.util.Log
import com.google.android.things.pio.PeripheralManager
import com.google.android.things.pio.Pwm
import java.io.IOException

/**
 * Created by fabio.goncalves on 23/01/2017.
 */

class ServoController {

    private lateinit var pin: Pwm

    private val periodMs: Double
    private val maxTimeMs: Double
    private val minTimeMs: Double

    private var pulseLenghtMs: Double = 0.toDouble()

    constructor(pin: String) : this(pin, 20.0, 2.5, 0.5)

    constructor(pin: String, periodMs: Double, maxTimeMs: Double, minTimeMs: Double) {

        this.periodMs = periodMs
        this.maxTimeMs = maxTimeMs
        this.minTimeMs = minTimeMs

        setup(pin)
    }

    private fun setup(pinName: String) {
        try {

            val service = PeripheralManager.getInstance()
            pin = service.openPwm(pinName)

            pin.setPwmFrequencyHz(1000.0 / periodMs)
            setPosition(90.0)
            pin.setEnabled(true)

        } catch (e: IOException) {
            Log.e("Servo Controller", "Can't initialize pin $pinName")
            e.printStackTrace()
        }

    }

    /**
     * assumes that min position is 0 degrees and max is 180
     */
    fun setPosition(degrees: Double) {

        if (degrees > 90) {
            pulseLenghtMs = minTimeMs
        } else {
            pulseLenghtMs = maxTimeMs
        }

        pulseLenghtMs = degrees / 180.0 * (maxTimeMs - minTimeMs) + minTimeMs

        if (pulseLenghtMs < minTimeMs) {
            pulseLenghtMs = minTimeMs
        } else if (pulseLenghtMs > maxTimeMs) {
            pulseLenghtMs = maxTimeMs
        }

        val dutyCycle = pulseLenghtMs / periodMs * 100.0

        Log.i(TAG, "Duty cycle = ${dutyCycle.format(2)} pulse lenght = ${pulseLenghtMs.format(2)}")

        try {
            pin.setPwmDutyCycle(dutyCycle)
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

fun Double.format(digits: Int) = java.lang.String.format("%.${digits}f", this)