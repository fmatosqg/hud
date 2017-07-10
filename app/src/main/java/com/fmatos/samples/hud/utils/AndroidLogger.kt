package com.fmatos.samples.hud.utils

import android.util.Log

/**
 * Created by fmatos on 9/07/2017.
 */

class AndroidLogger {

    fun i(tag: String, msg: String, vararg args: String?) {
        Log.i(tag, String.format(msg, *args))
    }

    fun e(tag: String, msg: String, vararg args: String?) {
        Log.e(tag, String.format(msg, *args))
    }

    fun v(tag: String, msg: String, vararg args: String?) {
        Log.v(tag, String.format(msg, *args))
    }

    fun d(tag: String, msg: String, vararg args: String?) {
        Log.d(tag, String.format(msg, *args))
    }
}