package com.fmatos.samples.hud.service

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat
import javax.inject.Inject

/**
 * Created by fmatos on 17/09/2017.
 */
class CountdownService {

    private var timezone: DateTimeZone

    private val countdownList: MutableList<CountdownMessage> = mutableListOf()


    @Inject
    constructor(timezone: DateTimeZone) {
        this.timezone = timezone

        safeAddCountdown("12/10/2017", "Birthday 1")
        safeAddCountdown("31/11/2017", "Birthday 2")


    }

    private fun safeAddCountdown(date: String, message: String) {

        try {
            countdownList.add(CountdownMessage(date, message))
        } catch (e: RuntimeException) {
            e.printStackTrace()
        }
    }

    fun getText(): String? {


        val response = countdownList.stream()
                .map { countdown -> checkCountdown(countdown) }
                .filter { message -> message != null }
                .findFirst()


        if (response.isPresent) {
            return response.get()
        } else {
            return null
        }

        return "1 day to something"
    }

    private fun checkCountdown(countdown: CountdownMessage): String? {

        val now = DateTime.now()

        val countMillis = countdown.localDate.millis - now.millis

        val flDays = countMillis / 1000.0 / 60.0 / 60.0 / 24.0

        val days: Long = Math.round(flDays)

        return "${days} days left for ${countdown.message}"
    }

}

data class CountdownMessage(val message: String) {

    private val dateFormat = DateTimeFormat.forPattern("dd/MM/YYYY")

    lateinit var localDate: DateTime

    constructor(date: String, message: String) : this(message) {

        localDate = DateTime.parse(date, dateFormat)
    }
}