package com.fmatos.samples.hud.service

import com.fmatos.samples.hud.service.model.amazingwallpapers.Album
import com.fmatos.samples.hud.service.model.amazingwallpapers.AmazingWallpapersService
import com.fmatos.samples.hud.service.model.amazingwallpapers.Photo
import com.fmatos.samples.hud.utils.AndroidLogger
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.Timed
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Inject


/**
 * Created by fmatos on 25/06/2017.
 * Builds observable that emits urls to wallpapers
 */
class WallpaperService {

    private var TAG: String = WallpaperService::class.java.simpleName

    private val ALBUM_INSTAGRAM_SCOTT_KELBY = "http://instatom.freelancis.net/scottkelby"
    private val ALBUM_INSTAGRAM_INSTAGOOD = "http://instatom.freelancis.net/instagood"
    private val ALBUM_500PX_FRESH = "https://500px.com/fresh.rss?period=today"
    //    private val ALBUM_SPACEX_TWITTER = "https://www.flickr.com/photos/spacex/"
    private val ALBUM_SPACEX_FLICKR = "https://www.flickr.com/services/feeds/photos_public.gne?id=130608600@N05"

    private val androidLogger: AndroidLogger
    private val retrofit: Retrofit

    private val throttle: Subject<Long> = BehaviorSubject.create()

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

    private var printCount: Long = 0
    private var itemsCount: Long = 0
    private val bufferSize = 5


    private fun updateListObservable(): Observable<String> {

        var clockEmmitImageUrl = Observable.interval(0, 60, TimeUnit.SECONDS)
                .timeInterval()


//        var zipper: BiFunction<in Timed<Long>, in String, out String>
        var zipper = BiFunction { time: Timed<Long>, url: String -> url }


        val fastUrls = buildListObservable(ALBUM_INSTAGRAM_INSTAGOOD)
                .repeatWhen { t: Observable<Any> ->
                    androidLogger.i(TAG, "On repeat when")
                    throttle
                }
                .map {
                    it ->
                    itemsCount++
                    it
                }

        val timedUrls = Observable
                .zip(clockEmmitImageUrl, fastUrls, zipper)
                .map { it ->
                    printCount++
                    androidLogger.i(TAG, "On prints %s > %s ", itemsCount.toString(), printCount.toString())
                    if (itemsCount == (printCount + bufferSize)) {
                        throttle.onNext(printCount)
                    }
                    it
                }
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())

        return timedUrls

    }

    private fun buildListObservable(albumUrl: String): Observable<String> {

        val urls = fetchData(albumUrl)
                .toObservable()
                .observeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.newThread())
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



        return urls
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