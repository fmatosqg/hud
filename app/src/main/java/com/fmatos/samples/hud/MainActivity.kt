package com.fmatos.samples.hud

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife

class MainActivity : AppCompatActivity() {

    private var TAG: String = MainActivity::class.java.simpleName
    private var handler: Handler? = null

    @BindView(R.id.clock_text)
    protected var clockText: TextView? = null

    var text: String = ""
    var textBlink: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        ButterKnife.bind(this)

        clockText = findViewById(R.id.clock_text)

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

        clockText?.text = textOutput


        Log.d(TAG,"Message = " + textOutput)
    }
}

