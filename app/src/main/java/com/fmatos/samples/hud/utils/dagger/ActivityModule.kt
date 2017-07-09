package com.fmatos.samples.hud.utils.dagger

import android.app.Application
import android.content.Context
import com.fmatos.samples.hud.service.WallpaperService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by fmatos on 8/07/2017.
 */

@Module
class ActivityModule(private val application: Application) {

    @Provides
    @Singleton
    fun provideApplicationContext(): Context {
        return application
    }

    @Provides
    @Singleton
    fun providesWallpaperService(): WallpaperService {
        return WallpaperService()
    }

}