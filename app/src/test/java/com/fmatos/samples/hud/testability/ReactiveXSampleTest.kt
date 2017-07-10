package com.fmatos.samples.hud.testability

import com.fmatos.samples.hud.service.model.amazingwallpapers.Album
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.TestScheduler
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.util.concurrent.TimeUnit


/**
 * Created by fmatos on 10/07/2017.
 */

class ReactiveXSampleTest {

    @Mock lateinit var
            album: Album

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        Mockito.`when`(album.name).thenReturn("fakeAlbum")
    }

    @Test
    fun singleTest() {

        val observable: Single<Album> = Single.just(album)
        val testObserver = observable.test()

        testObserver.assertComplete()
        testObserver.assertValue(album)
        assertEquals("fakeAlbum", testObserver.values().first().name)

        Mockito.verify(album, Mockito.times(0)).photos

    }

    @Test
    fun repeatableStreamTest() {
        val observable: Flowable<Album> = Single.just(album).repeat(1000)

        val testObserver = observable.test()

        testObserver.assertComplete()
        testObserver.assertValueCount(1000)
        assertEquals("fakeAlbum", testObserver.values().first().name)

    }

    @Test
    fun periodicStreamTest() {
        val scheduler = TestScheduler()
        val tick = Observable.interval(1, TimeUnit.SECONDS, scheduler)

        val observer = tick.subscribeOn(scheduler).test()

        observer.assertValueCount(0)

        scheduler.advanceTimeBy(1, TimeUnit.SECONDS)
        observer.assertValueCount(1)

        scheduler.advanceTimeBy(10, TimeUnit.SECONDS)
        observer.assertValueCount(11)

    }
}