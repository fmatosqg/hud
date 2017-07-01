package com.fmatos.samples.hud.service.model.amazingwallpapers

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Downloads models from www.amazingdomain.net
 * Created by fmatos on 1/07/2017.
 */

interface AmazingWallpapersService {

    @GET("album/customrss?")
    fun getAlbum(@Query("url") url: String): Observable<Album>
}