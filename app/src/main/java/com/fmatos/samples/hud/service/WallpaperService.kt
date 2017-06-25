package com.fmatos.samples.hud.service

import io.reactivex.Observable
import io.reactivex.rxkotlin.toObservable

/**
 * Created by fmatos on 25/06/2017.
 * Builds observable that emits urls to wallpapers
 */
class WallpaperService {

//    http://amazingdomain.net/album/customrss?url=http://instatom.freelancis.net/scottkelby

    fun buildObservable(): Observable<String> {

        val sampleList = listOf<String>(
                "https://scontent-amt2-1.cdninstagram.com/t51.2885-15/e35/19428895_1442540705784136_5195084963979984896_n.jpg",
                "https://scontent-amt2-1.cdninstagram.com/t51.2885-15/e35/c180.0.720.720/19228605_1940749149543021_747073109751758848_n.jpg"
        )

        return sampleList.toObservable()
                .repeatUntil({false})

    }
}