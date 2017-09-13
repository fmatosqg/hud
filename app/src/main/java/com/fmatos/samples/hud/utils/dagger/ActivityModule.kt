package com.fmatos.samples.hud.utils.dagger

import android.app.Application
import com.fmatos.samples.hud.service.WallpaperService
import com.fmatos.samples.hud.utils.AndroidLogger
import dagger.Module
import dagger.Provides
import org.joda.time.DateTimeZone
import retrofit2.Retrofit
import javax.inject.Singleton


/**
 * Created by fmatos on 8/07/2017.
 */

@Module
class ActivityModule(private val application: Application) {

    @Provides
    @Singleton
    fun provideApplication() = application

    @Provides
    @Singleton
    fun providesWallpaperService(androidLogger: AndroidLogger, retrofit: Retrofit): WallpaperService {
        return WallpaperService(androidLogger, retrofit)
    }

    @Provides
    @Singleton
    fun providesAndroidLogger(): AndroidLogger {
        return AndroidLogger()
    }

    @Provides
    @Singleton
    fun providesTimezone(): DateTimeZone {
        return DateTimeZone.forOffsetHours(+10)
    }
}