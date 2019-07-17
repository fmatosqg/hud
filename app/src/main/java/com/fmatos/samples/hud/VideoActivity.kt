package com.fmatos.samples.hud

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioRendererEventListener
import com.google.android.exoplayer2.extractor.ts.DefaultTsPayloadReaderFactory.FLAG_ALLOW_NON_IDR_KEYFRAMES
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.hls.DefaultHlsDataSourceFactory
import com.google.android.exoplayer2.source.hls.DefaultHlsExtractorFactory
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.video.VideoRendererEventListener

class VideoActivity : AppCompatActivity() {

    companion object {
        fun startActivity(context: Context) {
            Intent(context, VideoActivity::class.java)
                    .also { context.startActivity(it) }
        }
    }

    private val TAG = "PlayerActivity"

    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView
    //    private var playerView: SimpleExoPlayerView? = null
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


            // changes stream quality according to available bandwidth
//            val trackSel = AdaptiveTrackSelection.Factory().createTrackSelections(null, DefaultBandwidthMeter())
//
            val loadControl = DefaultLoadControl()
            player = ExoPlayerFactory.newInstance(
                    this,
                    renderers,
                    DefaultTrackSelector())

            val trackSelector = DefaultTrackSelector()
//
//            player = ExoPlayerFactory.newSimpleInstance(this)
//            player = ExoPlayerFactory.newSimpleInstance(this, rf, trackSelector, loadControl)
            player = ExoPlayerFactory.newInstance(this, renderers, trackSelector, DefaultLoadControl())

            player?.addListener(componentListener)
            playerView.player = player
            player?.playWhenReady = playWhenReady
            player?.seekTo(currentWindow, playbackPosition)
        }

        var url = "https://v.cdn.vine.co/r/videos/C40B136F021365174982178762752_53f4484ad8e.25.1.ADDA1E67-CF16-4C3B-901A-DE068DE26134.mp4"
//        url = "http://techslides.com/demos/sample-videos/small.mp4"
//        url = "http://techslides.com/demos/sample-videos/small.webm"
//        url = "http://techslides.com/demos/sample-videos/small.3gp"
        url = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"
//        url = "https://wowzaprod100-i.akamaihd.net/hls/live/254872/226ef637/playlist.m3u8"
//        url = "http://dl3.webmfiles.org/big-buck-bunny_trailer.webm"

//        url = getString(R.string.media_url_mp4)

        val mediaSource = buildMediaSource1(Uri.parse(url))
        player?.repeatMode = Player.REPEAT_MODE_ONE
        player?.prepare(mediaSource, true, true)

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

        if (uri.toString().endsWith("mp4")) {
            return videoSource
        } else {
            return ConcatenatingMediaSource(hls)
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

    private inner class ComponentListener : Player.DefaultEventListener() {

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
    }

}
