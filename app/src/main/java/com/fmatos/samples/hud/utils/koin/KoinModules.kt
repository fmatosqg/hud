package com.fmatos.samples.hud.utils.koin

import android.app.Application
import android.util.Log
import com.fmatos.samples.hud.MainViewModel
import com.fmatos.samples.hud.io.controller.EyesController
import com.fmatos.samples.hud.io.controller.ServoController
import com.fmatos.samples.hud.io.controller.VibratorController
import com.fmatos.samples.hud.service.WallpaperService
import com.fmatos.samples.hud.service.model.amazingwallpapers.AmazingWallpapersService
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.joda.time.DateTimeZone
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.Module
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * @author : Fabio de Matos
 * @since : 07/09/2019
 **/
class KoinModules {

    companion object {
        fun getInstance(): KoinModules {
            return KoinModules()
        }

    }

    private constructor()

    fun getAllModules(): List<Module> {

        return listOf(uiModule, domainModule, networkModule)
    }


    /**
     * Creates all injected objects necessary for activities, fragments and custom views
     */
    private val uiModule = module {
        viewModel { MainViewModel() }
    }

    /**
     * Creates all injected objects necessary for domain layer
     */
    private val domainModule = module {

        factory { WallpaperService(get()) }

        /**
         * https://garygregory.wordpress.com/2013/06/18/what-are-the-java-timezone-ids/
         */
        factory { DateTimeZone.forID("Australia/Melbourne") }

    }

    /**
     * Creates all injected objects necessary for network layer
     */
    private val networkModule = module {

        val SERVER_HOSTNAME = "http://amazingdomain.net/"

        factory { Gson() }


        factory {
            val cacheSize: Long = 10 * 1024 * 1024
            val cache = Cache(androidContext().cacheDir, cacheSize)
            Log.d("NetModule", "App cache dir is " + androidContext().cacheDir)
            cache
        }


        factory {
            val client = OkHttpClient.Builder()
            client.cache(get())
            client.build()
        }

        factory {
            val retrofit = Retrofit.Builder()
                    .baseUrl(SERVER_HOSTNAME)
                    .addConverterFactory(GsonConverterFactory.create(get()))
                    .client(get())
                    .build()

            retrofit.create(AmazingWallpapersService::class.java)
        }


    }

}