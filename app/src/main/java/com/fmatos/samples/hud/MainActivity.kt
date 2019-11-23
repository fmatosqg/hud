package com.fmatos.samples.hud

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.fmatos.samples.hud.databinding.ActivityMainBinding
import com.fmatos.samples.hud.io.controller.ServoController
import com.fmatos.samples.hud.service.GlowHelper
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModel()
    private val servoViewModel: ServoViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)

        DataBindingUtil
            .setContentView<ActivityMainBinding>(this, R.layout.activity_main)
            .also {
                it.lifecycleOwner = this
                it.viewModel = mainViewModel
                it.servoViewModel = servoViewModel
            }

        addGlow()
    }


    private fun addGlow() {
        val gh = GlowHelper(this)

        gh.setBackgroundGlow(
            container,
            R.drawable.rounded_rectangle,
            resources.getColor(R.color.glowingYellow, null)
        )

    }
}

