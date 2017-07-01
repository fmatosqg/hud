package com.fmatos.samples.hud.service

import android.util.Log
import com.fmatos.samples.hud.service.model.amazingwallpapers.AmazingWallpapersService
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.toObservable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.ReplaySubject
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


/**
 * Created by fmatos on 25/06/2017.
 * Builds observable that emits urls to wallpapers
 */
class WallpaperService {

    private var TAG: String = WallpaperService::class.java.simpleName

//    http://amazingdomain.net/album/customrss?url=http://instatom.freelancis.net/scottkelby


    private var subject: BehaviorSubject<String>? = null

    private var subject2: ReplaySubject<String> = ReplaySubject.create()

    val sampleList = listOf<String>(
            "https://scontent-amt2-1.cdninstagram.com/t51.2885-15/e35/19428895_1442540705784136_5195084963979984896_n.jpg",
            "https://scontent-amt2-1.cdninstagram.com/t51.2885-15/e35/c180.0.720.720/19228605_1940749149543021_747073109751758848_n.jpg",
            "https://scontent-amt2-1.cdninstagram.com/t51.2885-15/e35/c180.0.720.720/18513836_220288391802329_621043874340536320_n.jpg"

    )

    fun buildObservable(): Observable<String> {

        return buildList().toObservable()
                .repeatUntil({false})

    }

    private fun buildList(): List<String> {

        fetchData()

        return sampleList
    }

    private fun fetchData() {

        val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                .addConverterFactory(JacksonConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://amazingdomain.net/")
                .build()

        val ALBUM_INSTAGRAM_SCOTT_KELBY = "http://instatom.freelancis.net/scottkelby"

        val amazingWallpaperService = retrofit.create(AmazingWallpapersService::class.java)
        val scottKelbyAlbum = amazingWallpaperService.getAlbum(ALBUM_INSTAGRAM_SCOTT_KELBY)


        scottKelbyAlbum.subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribeBy(
                        onComplete = {
                            Log.i(TAG,"On album error")
                        },
                        onNext = {
                            album -> Log.i(TAG, "On Album name is " + album.name)
                        },
                        onError = {
                            Log.i(TAG,"On error " + it)
                        }
                )
    }
}