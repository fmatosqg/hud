package com.fmatos.samples.hud.service

import android.util.Log
import com.fmatos.samples.hud.service.model.amazingwallpapers.Album
import com.fmatos.samples.hud.service.model.amazingwallpapers.AmazingWallpapersService
import com.fmatos.samples.hud.service.model.amazingwallpapers.Photo
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.Timed
import io.reactivex.subjects.BehaviorSubject
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject


/**
 * Created by fmatos on 25/06/2017.
 * Builds observable that emits urls to wallpapers
 */
class WallpaperService {

    private var TAG: String = WallpaperService::class.java.simpleName

    private val INTERVAL_1_SECOND_MS: Long = 1000

    @Inject
    constructor()

    /**
     * Returns an infinite stream of photo urls
     */
    fun getObservable(): Observable<String> {

        return updateListObservable()
    }

    private fun updateListObservable(): Observable<String> {

        var clock1Min = Observable.interval(0, 60 * INTERVAL_1_SECOND_MS, TimeUnit.MILLISECONDS)
                .timeInterval()

//        var zipper: BiFunction<in Timed<Long>, in String, out String>
        var zipper = BiFunction { time: Timed<Long>, url: String -> url }

        val urls = Observable
                .zip(clock1Min, buildListObservable(), zipper)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())

        return urls

    }

    private fun buildListObservable(): Observable<String> {

        val photos = fetchData()
                .observeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.newThread())
                .toObservable()
                .flatMap { album: Album -> BehaviorSubject.fromIterable(album.photos) }
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

    private fun fetchData(): Single<Album> {

        Log.i(TAG,"On fetch list")


        val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://amazingdomain.net/")
                .build()

        val ALBUM_INSTAGRAM_SCOTT_KELBY = "http://instatom.freelancis.net/scottkelby"
        val ALBUM_INSTAGRAM_INSTAGOOD = "http://instatom.freelancis.net/instagood"

        val amazingWallpaperService = retrofit.create(AmazingWallpapersService::class.java)
        val scottKelbyAlbum = amazingWallpaperService.getAlbum(ALBUM_INSTAGRAM_INSTAGOOD)

        return scottKelbyAlbum
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
    }

}