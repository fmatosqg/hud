package com.fmatos.samples.hud.service.model.amazingwallpapers


import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

/**
 * Downloads models from www.amazingdomain.net
 * Created by fmatos on 1/07/2017.
 */

interface WallpaperApi {

    @Headers("User-Agent: AndroidThings-Rpi3-HUD")
    @GET("album/customrss?")
    suspend fun getAlbum(@Query("url") url: String): Response<Album>
}