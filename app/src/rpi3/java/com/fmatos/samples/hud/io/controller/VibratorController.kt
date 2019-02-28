package com.fmatos.samples.hud.io.controller

import android.util.Log
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.PeripheralManager
import java.io.IOException
import java.lang.Thread.sleep

class VibratorController {

    companion object {
        const val PIN_NAME = "BCM21"
    }

    private lateinit var pin: Gpio

    constructor() {

        setup(PIN_NAME)
    }

    private fun setup(pinName: String) {
        try {

            val service = PeripheralManager.getInstance()
            pin = service.openGpio(pinName)
            pin.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)

        } catch (e: IOException) {
            Log.e("Vibration Controller", "Can't initialize pin $pinName")
            e.printStackTrace()
        }

    }

    fun buzz(lenghtMs: Long = 100) {

        val th = Thread {
            pin.value = true
            Log.i("VC", "Buzz start")

            sleep(lenghtMs)

            pin.value = false
            Log.i("VC", "Buzz end")
        }

        th.start()
    }

}