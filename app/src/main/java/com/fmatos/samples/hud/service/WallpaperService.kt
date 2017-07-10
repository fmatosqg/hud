package com.fmatos.samples.hud.service

import com.fmatos.samples.hud.service.model.amazingwallpapers.Album
import com.fmatos.samples.hud.service.model.amazingwallpapers.AmazingWallpapersService
import com.fmatos.samples.hud.service.model.amazingwallpapers.Photo
import com.fmatos.samples.hud.utils.AndroidLogger
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.Timed
import retrofit2.Retrofit
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


/**
 * Created by fmatos on 25/06/2017.
 * Builds observable that emits urls to wallpapers
 */
class WallpaperService {

    private var TAG: String = WallpaperService::class.java.simpleName

    private val INTERVAL_1_SECOND_MS: Long = 1000


    private val ALBUM_INSTAGRAM_SCOTT_KELBY = "http://instatom.freelancis.net/scottkelby"
    private val ALBUM_INSTAGRAM_INSTAGOOD = "http://instatom.freelancis.net/instagood"
    private val ALBUM_500PX_FRESH = "https://500px.com/fresh.rss"

    private val androidLogger: AndroidLogger
    private val retrofit: Retrofit



    @Inject
    constructor(androidLogger: AndroidLogger, retrofit: Retrofit) {
        this.androidLogger = androidLogger
        this.retrofit = retrofit
    }

    /**
     * Returns an infinite stream of photo urls
     */
    fun getObservable(): Observable<String> {

        return updateListObservable()
    }

    private fun updateListObservable(): Observable<String> {

        updateList()

        var clockEmmitImageUrl = Observable.interval(0, 1 * INTERVAL_1_SECOND_MS, TimeUnit.MILLISECONDS)
                .timeInterval()

//        var zipper: BiFunction<in Timed<Long>, in String, out String>
        var zipper = BiFunction { time: Timed<Long>, url: String -> url }

        val urls = Observable
                .zip(clockEmmitImageUrl, buildListObservable(ALBUM_INSTAGRAM_INSTAGOOD), zipper)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())

        return urls

    }

    private fun updateList() {

        var clockUpdateImageList = Observable.interval(5 * INTERVAL_1_SECOND_MS, 30 * INTERVAL_1_SECOND_MS, TimeUnit.MILLISECONDS)
                .timeInterval()

        val oSubject: Observable<Observable<String>> = clockUpdateImageList
                .map { it ->
                    androidLogger.i(TAG, "On new observable")
                    buildListObservable(ALBUM_INSTAGRAM_INSTAGOOD)
                }

    }

    private fun buildListObservable(albumUrl: String): Observable<String> {

        val photos = fetchData(albumUrl)
                .observeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.newThread())
                .toObservable()
                .flatMap { album: Album ->
                    androidLogger.i(TAG, "On flatmap")
                    Observable.fromIterable(album.photos)
                }
                .filter { photo: Photo -> photo.url != null }
                .map { photo: Photo ->
                    // it's guaranteed to not have nulls
                    // from filter, but we need the compiler sugar coating
                    val url: String = photo.url ?: ""
                    url
                }
                .repeat()
                .toFlowable(BackpressureStrategy.MISSING)
                .toObservable()

        return photos

    }

    private fun fetchData(albumUrl: String): Single<Album> {

        androidLogger.i(TAG, "On fetch list %s", albumUrl)

        val albumObservable = retrofit
                .create(AmazingWallpapersService::class.java)
                .getAlbum(albumUrl)

        return albumObservable
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
    }

}