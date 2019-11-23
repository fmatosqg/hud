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

    private val windowLenght = 4
    private val windowStart = 44


    private val defaultAngle = 120f

    init {
        kickoff()
    }

    private fun kickoff() {

        viewModelScope
            .launch {

                warmUp()
                sweep()
                warmUp()

                while (true) {

                    updatePosition()
                    delay(2_000)
                }
            }
    }

    private suspend fun sweep() {

        for (i in 0..240) {
            delay(100)
            servoController.setPosition(i.toFloat())
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


        val angle = when {
            minute >= windowStart + windowLenght -> defaultAngle
            minute > windowStart ->
                proportional(
                    minute - windowStart,
                    dateTime.secondOfMinute().get()
                )
            else -> defaultAngle
        }


        servoController.setPosition(angle)

    }

    private fun proportional(minutes: Int, seconds: Int): Float {
        val startAngle = 40f
        val endAngle = defaultAngle

        val totalSecondsLength = windowLenght * 60
        val elapsedSeconds = minutes * 60 + seconds

        val dAngle =
            (endAngle - startAngle) * (elapsedSeconds.toFloat() / totalSecondsLength.toFloat())
        val angle = startAngle + dAngle

        Timber.d("Elapsed %s - angle %s", elapsedSeconds, angle)
        return angle

    }
}