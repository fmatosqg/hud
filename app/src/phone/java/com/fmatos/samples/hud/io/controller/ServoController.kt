package com.fmatos.samples.hud.io.controller

import android.util.Log

/**
 * Created by fabio.goncalves on 23/01/2017.
 */

class ServoController {

    private val periodMs: Double
    private val maxTimeMs: Double
    private val minTimeMs: Double

    private var pulseLenghtMs: Double = 0.toDouble()

    constructor(pin: String) : this(pin, 20.0, 2.5, 0.5)

    constructor(pin: String, periodMs: Double, maxTimeMs: Double, minTimeMs: Double) {
        Log.i(TAG, "Class running in simulated mode")

        this.periodMs = periodMs
        this.maxTimeMs = maxTimeMs
        this.minTimeMs = minTimeMs
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

//        Log.i(TAG, "Simulated Duty cycle = $dutyCycle pulse lenght = $pulseLenghtMs")

    }

    companion object {

        private val TAG = ServoController::class.java.simpleName


        val pwm0Pin: String
            get() = "PWM0"

        val pwm1Pin: String
            get() = "PWM1"
    }
}