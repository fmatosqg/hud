package com.fmatos.samples.hud.utils.dagger

import android.app.Application
import com.fmatos.samples.hud.service.WallpaperService
import com.fmatos.samples.hud.utils.AndroidLogger
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


/**
 * Created by fmatos on 8/07/2017.
 */

@Module
class ActivityModule(private val application: Application) {

    @Provides @Singleton
    fun provideApplication() = application

    @Provides
    @Singleton
    fun providesWallpaperService(androidLogger: AndroidLogger): WallpaperService {
        return WallpaperService(androidLogger)
    }

    @Provides
    @Singleton
    fun providesAndroidLogger(): AndroidLogger {
        return AndroidLogger()
    }

}