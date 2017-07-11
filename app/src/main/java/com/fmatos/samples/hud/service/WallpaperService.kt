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

    private val TAG: String = WallpaperService::class.java.simpleName

    private val RETRY_INTERVAL_SECONDS: Long = 60
    private val PHOTO_PRINT_TIME_SECONDS: Long = 60
    private val DEBOUNCE_SECONDS: Long = 5

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
     * Returns an infinite stream of photo urls, one every PHOTO_PRINT_TIME_SECONDS seconds
     */
    fun getObservable(): Observable<String> {

        return updateListObservable()
    }

    private var printCount: Long = 0
    private var itemsCount: Long = 0
    private val bufferSize = 5


    /**
     * provides coroutine to watch available amount of items inside the stream
     * and trigger server fetch when count is too low
     */
    private fun updateListObservable(): Observable<String> {

        var clockEmmitImageUrl = Observable.interval(0, PHOTO_PRINT_TIME_SECONDS,
                TimeUnit.SECONDS)
                .timeInterval()


//        var zipper: BiFunction<in Timed<Long>, in String, out String>
        var zipper = BiFunction { time: Timed<Long>, url: String -> url }


        val fastUrls = buildListObservable(ALBUM_INSTAGRAM_INSTAGOOD)
                .repeatWhen { t: Observable<Any> ->
                    androidLogger.i(TAG, "On stream is running empty")
                    throttle.debounce(DEBOUNCE_SECONDS, TimeUnit.SECONDS)
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
                    if (itemsCount < (printCount + bufferSize)) {
                        androidLogger.i(TAG, "On refill stream from server")
                        throttle.onNext(printCount)
                    }
                    it
                }
                .debounce(DEBOUNCE_SECONDS, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())

        return timedUrls

    }

    private fun buildListObservable(albumUrl: String): Observable<String> {

        val urls = fetchData(albumUrl)
                .toObservable()
                .flatMap { album: Album ->
                    androidLogger.i(TAG, "On fetched album: %s (%s)", album.niceName, album.photos.size.toString())
                    if (!isAlbumInvalid(album)) {
                        throw RuntimeException("On album is invalid (RuntimeException)")
                    }
                    Observable.fromIterable(album.photos)
                }
                .retryWhen { errs ->
                    errs.flatMap { err ->
                        androidLogger.i(TAG, "On retry flatmap in %s seconds %s"
                                , RETRY_INTERVAL_SECONDS.toString(), err.localizedMessage)
                        Observable.timer(RETRY_INTERVAL_SECONDS, TimeUnit.SECONDS)
                    }
                }
                .filter { photo: Photo -> photo.url != null }
                .map { photo: Photo -> photo.url ?: "" }


        return urls
    }

    /**
     * Returns a single that brings an album and its photo urls from the server
     */
    private fun fetchData(albumUrl: String): Single<Album> {

        androidLogger.i(TAG, "On fetch list %s", albumUrl)

        val albumObservable = retrofit
                .create(AmazingWallpapersService::class.java)
                .getAlbum(albumUrl)

        return albumObservable
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
    }


    private fun isAlbumInvalid(album: Album): Boolean {
        return album.photos.size > 0
    }


}