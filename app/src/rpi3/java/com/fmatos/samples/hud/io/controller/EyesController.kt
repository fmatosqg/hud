package com.fmatos.samples.hud.io.controller

import android.util.Log
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.PeripheralManager


/**
 * add a cable to pin BCM 26, followed by 330 ohm and 2 red leds
 */
class EyesController {


    companion object {
        const val PIN = "BCM26"
    }

    private val TAG: String = EyesController::class.java.simpleName

    private var pin: Gpio? = null

    private var dutyCycle = 100 // percentage, ranges from 0 to 100

    private var isRunning = false
        get() = field

    /**
     * Minimal overhead when called multiple times, and won't have side effects if it's already started
     */
    fun start() {

        if (!isRunning) {

            synchronized(isRunning) {
                Log.i(TAG, "EyesController start")
                isRunning = true

                val service = PeripheralManager.getInstance()
                pin = service.openGpio(PIN)
                        .also { it.setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH) }

                Thread({ worker() })
                        .start()
            }
        }

    }


    /**
     * Minimal overhead when called multiple times, and won't have side effects if it's already stopped
     */
    fun stop() {

        if (isRunning) {
            synchronized(isRunning) {
                Log.i(TAG, "EyesController stop")
                isRunning = false
                pin?.close()
                pin = null
            }
        }
    }

    private fun worker() {


        var counter = 0

        dutyCycle = 80

        while (isRunning) {
            Thread.sleep(10)

            when {
                counter == dutyCycle -> pin?.value = false
                counter > 100 -> {
                    counter = 0
                    pin?.value = true
                }
            }

            counter++

        }


    }
}