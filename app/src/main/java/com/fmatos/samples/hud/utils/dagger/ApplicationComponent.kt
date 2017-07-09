package com.fmatos.samples.hud.utils.dagger

import com.fmatos.samples.hud.MainActivity
import dagger.Component
import javax.inject.Singleton

/**
 * Created by fmatos on 8/07/2017.
 */
@Singleton
@Component(modules = arrayOf(ActivityModule::class))
interface ApplicationComponent {
    fun inject(application: HudApplication)

    fun inject(mainActivity: MainActivity)
}