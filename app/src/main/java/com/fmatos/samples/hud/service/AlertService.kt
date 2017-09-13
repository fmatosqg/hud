package com.fmatos.samples.hud.service

import org.joda.time.DateTimeZone
import org.joda.time.LocalTime
import javax.inject.Inject

/**
 * Created by fmatos on 13/09/2017.
 */
class AlertService {

    private val timezone: DateTimeZone
    private val alertList: MutableList<Alert> = mutableListOf()

    @Inject
    constructor(timezone: DateTimeZone) {
        this.timezone = timezone

        alertList.add(Alert(20, 30, "Shower"))
        alertList.add(Alert(22, 0, "Bed Time"))
        alertList.add(Alert(12, 0, "Test"))
        alertList.add(Alert(17, 30, "Test"))

    }

    fun getAlert(): String? {
        return scanAlerts()
    }


    private fun scanAlerts(): String? {

        val response = alertList.stream()
                .map { alert -> checkAlert(alert) }
                .filter { message -> message != null }
                .findFirst()


        if (response.isPresent) {
            return response.get()
        } else {
            return null
        }
    }

    private fun checkAlert(alert: Alert): String? {

        val time = LocalTime(alert.hour, alert.minute)

        return if (isAlertTime(time, alert.marginMinutes)) {
            alert.message
        } else {
            null
        }
    }

    private fun isAlertTime(time: LocalTime, marginMinutes: Int): Boolean {

        val now = LocalTime(timezone)

        val marginTime = time.minusMinutes(marginMinutes)

        return now.isAfter(marginTime) && now.isBefore(time)

    }

}

data class Alert(val hour: Int, val minute: Int, val marginMinutes: Int, val message: String) {

    constructor(hour: Int, minute: Int, message: String) : this(hour, minute, 10, message)
}