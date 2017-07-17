package com.fmatos.samples.hud.service.iot

import io.reactivex.Observable
import java.util.concurrent.TimeUnit
import com.google.android.things.pio.PeripheralManagerService
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.GpioCallback


/**
 * Created by fabio.goncalves on 17/7/17.
 */


class TiltService {


    private var mButtonGpio: Gpio? = null
    private var value: Boolean = false

    fun getTiltObservable(): Observable<Boolean> {

        val service = PeripheralManagerService()
        mButtonGpio = service.openGpio("BCM21")
        mButtonGpio?.setDirection(Gpio.DIRECTION_IN)
        mButtonGpio?.setEdgeTriggerType(Gpio.EDGE_FALLING)
        mButtonGpio?.registerGpioCallback(object : GpioCallback() {
            override fun onGpioEdge(gpio: Gpio?): Boolean {
//                Log.i(TAG, "GPIO changed, button pressed")
                // Return true to continue listening to events
                value = gpio?.value ?: false

                return true
            }
        })


        var clockEmmitImageUrl = io.reactivex.Observable.interval(0, 1,
                TimeUnit.SECONDS)
                .timeInterval()

        return clockEmmitImageUrl.map {
            value
        }

    }
}