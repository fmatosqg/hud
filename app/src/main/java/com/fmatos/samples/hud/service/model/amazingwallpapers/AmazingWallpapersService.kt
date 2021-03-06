package com.fmatos.samples.hud.service.model.amazingwallpapers

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

/**
 * Downloads models from www.amazingdomain.net
 * Created by fmatos on 1/07/2017.
 */

interface AmazingWallpapersService {

    @Headers("User-Agent: AndroidThings-Rpi3-HUD")
    @GET("album/customrss?")
    fun getAlbum(@Query("url") url: String): Single<Album>
}