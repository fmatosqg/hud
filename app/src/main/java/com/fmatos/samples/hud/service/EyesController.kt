package com.fmatos.samples.hud.service

import com.google.android.things.pio.Gpio
import com.google.android.things.pio.PeripheralManagerService

// add a cable to pin, followed by 330 ohm and 2 red leds
class EyesController {

    private lateinit var pin: Gpio
    private var pinValue = true

    private var dutyCycle = 100 // percentage, ranges from 0 to 100

    constructor() {

        setup()
    }

    fun setup() {
        val service = PeripheralManagerService()
        pin = service.openGpio("BCM26")
        pin.setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH)


        Thread({

            worker()

        })
                .start()
    }

    private fun worker() {

        var counter = 0

        dutyCycle = 80

        while (true) {
            Thread.sleep(10)

            when {
                counter == dutyCycle -> pin.value = false
                counter > 100 -> {
                    counter = 0
                    pin.value = true
                }
            }

            counter++

        }


    }
}