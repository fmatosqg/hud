package com.fmatos.samples.hud.utils.dagger

import android.app.Application


/**
 * Created by fmatos on 8/07/2017.
 */

class HudApplication : Application() {

    companion object {
        //  platformStatic allow access it from java code
        @JvmStatic lateinit var graph: ApplicationComponent
    }

    override fun onCreate() {
        super.onCreate()

        graph = DaggerApplicationComponent
                .builder()
                .activityModule(ActivityModule(this))
                .build()

        graph.inject(this)
    }
}