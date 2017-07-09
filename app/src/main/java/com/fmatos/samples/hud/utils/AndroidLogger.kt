package com.fmatos.samples.hud.utils

import android.util.Log

/**
 * Created by fmatos on 9/07/2017.
 */

class AndroidLogger {

    fun i(tag: String, msg: String, vararg args: Any) {
        Log.i(tag, String.format(msg, *args))
    }

    fun e(tag: String, msg: String, vararg args: Any) {
        Log.e(tag, String.format(msg, *args))
    }

    fun v(tag: String, msg: String, vararg args: Any) {
        Log.v(tag, String.format(msg, *args))
    }

    fun d(tag: String, msg: String, vararg args: Any) {
        Log.d(tag, String.format(msg, *args))
    }
}