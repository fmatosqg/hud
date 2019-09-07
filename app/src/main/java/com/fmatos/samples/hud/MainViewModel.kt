package com.fmatos.samples.hud

import androidx.lifecycle.ViewModel

/**
 * @author : Fabio de Matos
 * @since : 07/09/2019
 **/
class MainViewModel : ViewModel(){

    /*

    private fun updateScreen() {


        textBlink = !textBlink

        var timeView: String = time

        if (textBlink) {
            timeView = timeView.replace(':', ' ')
        }

        servoAngle = getServoAngle()

        servoController.setPosition(servoAngle.toDouble())

        processVibration()

        clock_date_text.text = date

        clock_time_text.text = timeView

        ip_text.text = ip

        test_text.text = test

        if (isAm == null) {
            label_am.visibility = View.GONE
            label_pm.visibility = View.GONE

        } else if (isAm ?: true) {
            label_am.visibility = View.VISIBLE
            label_pm.visibility = View.INVISIBLE

        } else {
            label_am.visibility = View.INVISIBLE
            label_pm.visibility = View.VISIBLE

        }

        if (alertText != null) {
            alert_text.text = alertText
            alert_text.visibility =
                    if (textBlink) View.INVISIBLE
                    else View.VISIBLE

            countdown_text.visibility = View.GONE
        } else {
            alert_text.visibility = View.GONE

            if (countdownText?.isNotEmpty() ?: false) {
                countdown_text.text = countdownText
                countdown_text.visibility = View.VISIBLE
            } else {
                countdown_text.visibility = View.GONE
            }
        }

        antiBurnIn()
        addGlow()
    }

    /**
     * Moves the container slightly to avoid display burn in
     */
    private fun antiBurnIn() {
        val minute = DateTime
                .now()
                .toDateTime(timezone)
                .minuteOfHour()
                .get()

        val offsetX = ((minute % 10L) - 5) * 20f
        val offsetY = ((minute / 6) - 3) * 20f

        container
                .animate()
                .translationX(offsetX)
                .translationY(offsetY)
                .setDuration(300)
                .start()


    }

    private fun processVibration() {

        val dateTime = DateTime.now().toDateTime(timezone)
        val seconds = dateTime.secondOfMinute().get()

        if (seconds == 0) {
            vibratorController.buzz(2000)
        }
    }

    /**
     * Returns angle to the servo controller in degrees
     */
    private fun getServoAngle(): Int {

        // for 49 minutes it stays down
        // at minute 50 it slowly goes up
        // from minute 51 to 59 it will go down until it's all down

        val minute: Int = DateTime().minuteOfHour().get()
        val second: Int = DateTime().secondOfMinute().get()

        val minAngle = 30.toFloat()
        val maxAngle = 120.toFloat()

        val angle: Float = when {

            minute == 50 -> {
                val alpha = -(maxAngle - minAngle) / 60.0f
                minAngle + alpha * (second)
            }

            minute < 50 -> maxAngle
            minute > 59 -> maxAngle

            else -> {
                val alpha = (maxAngle - minAngle) / 10.0f // everything happens over 10 minutes
                minAngle + alpha * (minute - 50) // offset by 50 minutes
            }
        }

        val isEyesControllerRunning =
                when {
                    minute > 59 -> false
                    minute >= 50 -> true
                    else -> false
                }

        if (isEyesControllerRunning) {
            eyesController.start()
        } else {
            eyesController.stop()
        }

        Log.i(TAG, "Servo angle will be ${angle.toInt()} on minute $minute -- $isEyesControllerRunning")

        return angle.toInt()

    }

    private fun updateModel(longTimed: Timed<Long>) {

//        val dateTime = DateTime.now().toDateTime(DateTimeZone.forOffsetHours(-3))
//        val locale = java.util.Locale("es", "AR")
        val dateTime = DateTime.now().toDateTime(timezone)
        val locale = java.util.Locale("en", "AU")

//        time = DateTimeFormat.forPattern("h:mm a").withLocale(locale).print(dateTime)
        time = DateTimeFormat.forPattern("h:mm").withLocale(locale).print(dateTime)
        date = DateTimeFormat.forPattern("EEEEE, dd MMMM yyyy").withLocale(locale).print(dateTime)

        val ampm = DateTimeFormat.forPattern("a").withLocale(locale).print(dateTime)

        isAm = ampm?.equals("AM")

        test = "${longTimed.value()}"
        getWifiIp()

        alertText = alertService.getAlert()

        countdownText = countdownService.getText()


    }

 private fun getWifiIp() {
        val wifiMgr = getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiMgr.connectionInfo
        ip = wifiInfo.ssid

        ip += Formatter.formatIpAddress(wifiInfo.ipAddress)

    }

     */
}