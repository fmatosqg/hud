package com.fmatos.samples.hud.service

import com.fmatos.samples.hud.service.model.amazingwallpapers.WallpaperApi
import org.koin.standalone.KoinComponent
import timber.log.Timber

/**
 * Created by fmatos on 25/06/2017.
 * Builds observable that emits urls to wallpapers
 */
class WallpaperService(private val wallpaperApi: WallpaperApi) : KoinComponent {

    private val ALBUM_INSTAGRAM_SCOTT_KELBY = "http://instatom.freelancis.net/scottkelby"
    private val ALBUM_INSTAGRAM_INSTAGOOD = "http://instatom.freelancis.net/instagood"
    private val ALBUM_500PX_FRESH = "https://500px.com/fresh.rss?period=today"
    //    private val ALBUM_SPACEX_TWITTER = "https://www.flickr.com/photos/spacex/"
    private val ALBUM_SPACEX_FLICKR =
        "https://www.flickr.com/services/feeds/photos_public.gne?id=130608600@N05"
    private val ALBUM_REDDIT_EARTH_P = "https://www.reddit.com/r/EarthPorn/.rss"
    private val ALBUM_PIXELART = "https://www.reddit.com/r/PixelArt/.rss"

    suspend fun getList(): List<String> {

        val albumUrl = ALBUM_PIXELART

        return wallpaperApi.getAlbum(albumUrl)
            .body()
            ?.also {
                Timber.i(
                    "On fetch list %s -- %s images found",
                    it.niceName,
                    it.photos.size
                )
            }
            ?.photos
            ?.mapNotNull { it.url }
            ?: listOf<String>()

    }


}