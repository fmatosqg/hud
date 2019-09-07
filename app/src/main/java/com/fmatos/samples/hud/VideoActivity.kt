package com.fmatos.samples.hud

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Surface
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.audio.AudioRendererEventListener
import com.google.android.exoplayer2.extractor.ts.DefaultTsPayloadReaderFactory.FLAG_ALLOW_NON_IDR_KEYFRAMES
import com.google.android.exoplayer2.source.*
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.hls.DefaultHlsDataSourceFactory
import com.google.android.exoplayer2.source.hls.DefaultHlsExtractorFactory
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.video.VideoRendererEventListener
import java.io.File
import java.io.IOException

class VideoActivity : AppCompatActivity() {

    companion object {
        fun startActivity(context: Context) {
            Intent(context, VideoActivity::class.java)
                    .also { context.startActivity(it) }
        }
    }

    private val TAG = "PlayerActivity"

    private var player: SimpleExoPlayer? = null
    private lateinit var playerView: PlayerView

    private var componentListener: ComponentListener? = null

    private var playbackPosition: Long = 0
    private var currentWindow: Int = 0
    private var playWhenReady = true

    private val timeoutMs = 25000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        componentListener = ComponentListener()
        playerView = findViewById(R.id.video_view)

    }

    public override fun onResume() {
        super.onResume()
        hideSystemUi()
        if (Util.SDK_INT <= 23 || player == null) {
            initializePlayer()
        }
    }

    public override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {
            releasePlayer()
        }
    }


    private lateinit var trackSelector: DefaultTrackSelector

    private val DISABLE_AUDIO: Boolean = true

    private fun initializePlayer() {
        if (player == null) {

            val rf = DefaultRenderersFactory(this)

            val renderersFactory: RenderersFactory = DefaultRenderersFactory(this)

            val renderers: Array<Renderer> =
                    renderersFactory.createRenderers(
                            Handler(),
                            object : VideoRendererEventListener {
                                override fun onVideoInputFormatChanged(format: Format?) {
                                    Log.i("TAGG", "New video format coming $format")
                                }
                            },
                            object : AudioRendererEventListener {
                                override fun onAudioDecoderInitialized(decoderName: String?, initializedTimestampMs: Long, initializationDurationMs: Long) {

                                    Log.i("TAGG", "New audio format initialized $decoderName")
                                }

                                override fun onAudioInputFormatChanged(format: Format?) {

                                    Log.i("TAGG", "New audio format coming $format")
                                }
                            },
                            { cues ->
                                val t = cues.foldRight("", { c, acc -> acc + c.text })
                                Log.i("TAGG", "New cues $t $cues")
                            },
                            { metadata ->
                                Log.i("TAGG", "Metadata $metadata")
                            },
                            null)

            val loadControl = DefaultLoadControl()

//
            trackSelector = DefaultTrackSelector()


//
//            player = ExoPlayerFactory.newSimpleInstance(this)
            player = ExoPlayerFactory.newSimpleInstance(this, rf, trackSelector, loadControl)
//            player = ExoPlayerFactory.newInstance(this, renderers, trackSelector, DefaultLoadControl())

            player?.addListener(componentListener)
            playerView.player = player
            player?.playWhenReady = playWhenReady
//            player?.seekTo(currentWindow, playbackPosition)
        }

        var url = "https://v.cdn.vine.co/r/videos/C40B136F021365174982178762752_53f4484ad8e.25.1.ADDA1E67-CF16-4C3B-901A-DE068DE26134.mp4"
//        url = "http://techslides.com/demos/sample-videos/small.mp4"
        url = "http://techslides.com/demos/sample-videos/small.webm"
//        url = "http://techslides.com/demos/sample-videos/small.3gp"
        url = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4" // good source
        url = "https://bitdash-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8" // https://bitmovin.com/mpeg-dash-hls-examples-sample-streams/
//        url = "https://wowzaprod100-i.akamaihd.net/hls/live/254872/226ef637/playlist.m3u8" // 404 error
//        url = "http://dl3.webmfiles.org/big-buck-bunny_trailer.webm"

//        url = getString(R.string.media_url_mp4)

//        url = Uri.fromFile(File("file")).toString()

        url = "http://amazingdomain.net/sea.mp4"

        val mediaSource = buildMediaSource1(Uri.parse(url))
        player?.repeatMode = Player.REPEAT_MODE_ONE



        player
//                ?.also { it.retry() }
                ?.also { it.prepare(mediaSource, true, true) }
                ?.also { it.addAnalyticsListener(MyAnalyticsListener()) }

        for (i: Int in 0..((player?.rendererCount ?: 1) - 1)) {

            when (player?.getRendererType(i)) {
                C.TRACK_TYPE_VIDEO -> Log.i(TAG, "Rendere video $i")
                C.TRACK_TYPE_AUDIO -> {
                    Log.i(TAG, "Rendere audio $i")
                    if (DISABLE_AUDIO) {
                        trackSelector.setParameters(
                                trackSelector.buildUponParameters()
                                        .setRendererDisabled(1, true)
                        )
                    }

                }
                C.TRACK_TYPE_METADATA -> Log.i(TAG, "Rendere metadata $i")
                else -> {
                    Log.i(TAG, "Renderer ${player?.getRendererType(i)} -- $i")
                }
            }

        }
    }

    inner class MyAnalyticsListener : AnalyticsListener {

        override fun onLoadError(eventTime: AnalyticsListener.EventTime?, loadEventInfo: MediaSourceEventListener.LoadEventInfo?, mediaLoadData: MediaSourceEventListener.MediaLoadData?, error: IOException?, wasCanceled: Boolean) {

            Log.d(TAG, "what load $error") // captures status code 404 errors
        }

        override fun onDroppedVideoFrames(eventTime: AnalyticsListener.EventTime?, droppedFrames: Int, elapsedMs: Long) {
            Log.d(TAG, "what dropped $droppedFrames frames")

        }

        override fun onLoadCompleted(eventTime: AnalyticsListener.EventTime?, loadEventInfo: MediaSourceEventListener.LoadEventInfo?, mediaLoadData: MediaSourceEventListener.MediaLoadData?) {

            Log.d(TAG, "what load completed")
        }

        override fun onPlayerError(eventTime: AnalyticsListener.EventTime?, error: ExoPlaybackException?) {

            Log.d(TAG, "what player $error")
        }

        override fun onAudioUnderrun(eventTime: AnalyticsListener.EventTime?, bufferSize: Int, bufferSizeMs: Long, elapsedSinceLastFeedMs: Long) {
            Log.d(TAG, "what audio underrun")

        }

        override fun onRenderedFirstFrame(eventTime: AnalyticsListener.EventTime?, surface: Surface?) {

            Log.d(TAG, "what first frame")
        }

        override fun onTracksChanged(eventTime: AnalyticsListener.EventTime?, trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {

            trackSelections
                    ?.all
                    ?.forEachIndexed { index, trackSelection ->


                        if (index == 1) {
                            trackSelection?.disable()
                        }

                        Log.d(TAG, "tracks 2 changed $index" + " --- " + trackSelection?.selectedFormat + " ---" + trackSelection?.selectionReason)

                    }
        }
    }


    private fun releasePlayer() {
        if (player != null) {
            playbackPosition = player!!.currentPosition
            currentWindow = player!!.currentWindowIndex
            playWhenReady = player!!.playWhenReady
//            player!!.removeListener(componentListener)
//            player!!.release()
            player = null
        }
    }

    private fun loopVideo() {
        if (player != null) {
            player!!.playWhenReady
        }
    }


    private fun buildMediaSource1(uri: Uri): MediaSource {

//        https@ //medium.com/fungjai/playing-video-by-exoplayer-b97903be0b33
        // these are reused for both media sources we create below
        val videoSource = ExtractorMediaSource.Factory(
                DefaultHttpDataSourceFactory("exoplayer-codelab")).createMediaSource(uri)
        val httpDataSource = DefaultHttpDataSourceFactory("exoplayer-codelab")

        val hlsDatasource = DefaultHlsDataSourceFactory(httpDataSource)
        val dash = DashMediaSource.Factory(httpDataSource)
                .createMediaSource(uri)

        val hlsExtractorFactory = DefaultHlsExtractorFactory(FLAG_ALLOW_NON_IDR_KEYFRAMES, true)
        val hls = HlsMediaSource.Factory(hlsDatasource)
                .setExtractorFactory(hlsExtractorFactory)
                .setAllowChunklessPreparation(true) // makes things load faster
                .setLoadErrorHandlingPolicy(DefaultLoadErrorHandlingPolicy())
                .createMediaSource(uri)
//        val v = HlsMediaSource

        return if (uri.toString().endsWith("mp4")) {
            videoSource
        } else if (uri.toString().endsWith("m3u8")) {
            ConcatenatingMediaSource(hls)
        } else {
            videoSource
        }

//        return hls
//        return dash
    }

    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
//        playerView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
//                or View.SYSTEM_UI_FLAG_FULLSCREEN
//                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    private inner class ComponentListener : Player.EventListener {

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            val stateString: String =
                    when (playbackState) {
                        Player.STATE_IDLE -> "ExoPlayer.STATE_IDLE      -"
                        Player.STATE_BUFFERING -> "ExoPlayer.STATE_BUFFERING -"
                        Player.STATE_READY -> {
                            if (BuildConfig.DEBUG) {
                                Toast.makeText(this@VideoActivity, "Video is ready", Toast.LENGTH_SHORT).show()
                            }
                            "ExoPlayer.STATE_READY     -"
                        }
                        Player.STATE_ENDED -> {

                            loopVideo()
                            "ExoPlayer.STATE_ENDED     -"
                        }
                        else -> "UNKNOWN_STATE             -"
                    }
            Log.d(TAG, "changed state to $stateString playWhenReady: $playWhenReady")
        }

        override fun onPlayerError(error: ExoPlaybackException?) {
            Log.d(TAG, "Error $error ") // error status code 301
        }

        override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {

            trackSelections
                    ?.all
                    ?.forEachIndexed { index, trackSelection ->
                        Log.d(TAG, "tracks changed $index" + " --- " + trackSelection?.selectedFormat)
                    }
        }

    }

}
