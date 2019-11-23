package com.fmatos.samples.hud

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fmatos.samples.hud.io.controller.ServoController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import timber.log.Timber

/**
 * @author : Fabio de Matos
 * @since : 23/11/2019
 **/
class ServoViewModel(private val servoController: ServoController) : ViewModel() {

    private val windowLength = 5
    private val windowStart = 3


    private val defaultAngle = 170f

    init {
        kickoff()
    }

    private fun kickoff() {

        viewModelScope
            .launch {

                warmUp()

                while (true) {

                    updatePosition()
                    delay(2_000)
                }
            }
    }

    private suspend fun warmUp() {

        repeat(2) {

            servoController.setPosition(10f)
            delay(500)
            servoController.setPosition(120f)
            delay(500)

        }
    }

    private fun updatePosition() {

        val dateTime = DateTime.now()

        val minute = dateTime.minuteOfHour().get()
        val elapsedMinute = minute - windowStart

        val angle = when {
            elapsedMinute >= windowLength -> defaultAngle
            elapsedMinute < 0 -> defaultAngle
            else ->
                proportional(
                    elapsedMinute,
                    dateTime.secondOfMinute().get()
                )

        }


        servoController.setPosition(angle)

    }

    private fun proportional(minutes: Int, seconds: Int): Float {
        val startAngle = 40f
        val endAngle = defaultAngle

        val totalSecondsLength = windowLength * 60
        val elapsedSeconds = minutes * 60 + seconds

        val dAngle =
            (endAngle - startAngle) * (elapsedSeconds.toFloat() / totalSecondsLength.toFloat())
        val angle = startAngle + dAngle

        Timber.d("Elapsed %s - angle %s", elapsedSeconds, angle)
        return angle

    }
}