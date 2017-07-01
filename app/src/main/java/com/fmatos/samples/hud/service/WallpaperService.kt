package com.fmatos.samples.hud.service

import android.util.Log
import com.fmatos.samples.hud.service.model.amazingwallpapers.Album
import com.fmatos.samples.hud.service.model.amazingwallpapers.AmazingWallpapersService
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


/**
 * Created by fmatos on 25/06/2017.
 * Builds observable that emits urls to wallpapers
 */
class WallpaperService {

    private var TAG: String = WallpaperService::class.java.simpleName

    fun buildObservable(): Observable<String> {

        return buildListObservable()
                .repeatUntil({false})

    }

    private fun buildListObservable(): Subject<String> {

        val observableAlbum = fetchData()

        val subject: Subject<String> = BehaviorSubject.create()


        observableAlbum
                .observeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.newThread())
                .subscribeBy(
                        onComplete = {
                            Log.i(TAG, "On album complete")
                        },
                        onNext = {
                            album ->
                            Log.i(TAG, "On Album name is " + album.name)

                            album.photos
                                    .filter { photo -> photo.url != null }
                                    .map { photo ->
                                        val url: String = photo.url ?: ""
                                        subject.onNext(url)
                                    }

                        },
                        onError = {
                            Log.i(TAG, "On error " + it)
                        }
                )


        return subject
    }

    private fun fetchData(): Observable<Album> {

        val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://amazingdomain.net/")
                .build()

        val ALBUM_INSTAGRAM_SCOTT_KELBY = "http://instatom.freelancis.net/scottkelby"

        val amazingWallpaperService = retrofit.create(AmazingWallpapersService::class.java)
        val scottKelbyAlbum = amazingWallpaperService.getAlbum(ALBUM_INSTAGRAM_SCOTT_KELBY)

        return scottKelbyAlbum.subscribeOn(Schedulers.newThread())
    }

}