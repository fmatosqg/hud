package com.fmatos.samples.hud

import com.fmatos.samples.hud.service.model.amazingwallpapers.Album
import com.fmatos.samples.hud.service.model.amazingwallpapers.AmazingWallpapersService
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.hamcrest.Matchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito

/**
 * Created by fmatos on 15/09/2017.
 */
@RunWith(JUnit4::class)
class NetworkTest {

    private lateinit var amazingService: AmazingWallpapersService
    private lateinit var mockedAlbum: Album

    @Before
    fun setup() {

        amazingService = buildMockAmazingService()
    }


    private fun buildMockAmazingService(): AmazingWallpapersService {

        val service = Mockito.mock(AmazingWallpapersService::class.java)
        mockedAlbum = Mockito.mock(Album::class.java)
        val observableSuccess = Single.just(mockedAlbum)

        Mockito.`when`(service.getAlbum(Mockito.anyString())).thenReturn(observableSuccess)

        return service
    }

    @Test
    fun testTest() {
        assertEquals(mockedAlbum,mockedAlbum)
    }

    @Test
    fun successResponseTest() {

        val testObservable = amazingService
                .getAlbum("whatever")
                .observeOn(Schedulers.computation())
                .test()

        testObservable.assertValue({ album -> album.equals(mockedAlbum) })
        testObservable.assertComplete()

    }
}