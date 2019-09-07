package com.fmatos.samples.hud.utils.koin

import android.app.Application
import com.fmatos.samples.hud.BuildConfig
import org.koin.android.ext.android.startKoin
import org.koin.android.logger.AndroidLogger
import org.koin.log.EmptyLogger
import timber.log.Timber


/**
 * Created by fmatos on 8/07/2017.
 */

class HudApplication : Application() {


    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        val koinLogger = if (BuildConfig.DEBUG) {
            AndroidLogger()
        } else {
            EmptyLogger()
        }

        startKoin(androidContext = this,
                modules = KoinModules.getInstance().getAllModules(),
                logger = koinLogger)
    }
}