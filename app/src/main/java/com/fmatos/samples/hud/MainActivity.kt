package com.fmatos.samples.hud

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var TAG: String = MainActivity::class.java.simpleName

    private var handler: Handler? = null
    private var text: String = ""
    private var textBlink: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        handler = Handler(mainLooper)

        text = " 8:11"
        updateScreen()
    }

    private fun updateScreen() {

        handler!!.postDelayed(this::updateScreen, 1000)

        textBlink = !textBlink

        var textOutput: String

        textOutput = text.replace(' ', '!')

        if (textBlink) {
            textOutput = textOutput.replace(':', ' ')
        }

        clock_text.text = textOutput

        Log.d(TAG,"Message = " + textOutput)
    }
}

