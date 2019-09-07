package com.fmatos.samples.hud

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.fmatos.samples.hud.databinding.ActivityMainBinding
import com.fmatos.samples.hud.service.GlowHelper
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private var servoAngle = 90


    private val mainViewModel: MainViewModel by viewModel()
//    private val wallpaperService: WallpaperService by inject()


    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)

        DataBindingUtil
                .setContentView<ActivityMainBinding>(this, R.layout.activity_main)
                .also { it.lifecycleOwner = this }
                .also { it.viewModel = mainViewModel }

    }


    private fun addGlow() {
        val gh = GlowHelper(this)

        gh.setBackgroundGlow(container, R.drawable.rounded_rectangle, resources.getColor(R.color.glowingYellow, null))

    }
}

