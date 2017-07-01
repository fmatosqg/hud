package com.fmatos.samples.hud.service

import android.util.Log
import io.reactivex.Observable
import io.reactivex.rxkotlin.toObservable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.ReplaySubject

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

        return sampleList
    }
}