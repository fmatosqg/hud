package com.fmatos.samples.hud

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.SimpleExoPlayerView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util

class VideoActivity : AppCompatActivity() {

    companion object {
        fun startActivity(context: Context) {
            Intent(context, VideoActivity::class.java)
                    .also { context.startActivity(it) }
        }
    }

    // bandwidth meter to measure and estimate bandwidth
    private val BANDWIDTH_METER = DefaultBandwidthMeter()
    private val TAG = "PlayerActivity"

    private var player: SimpleExoPlayer? = null
    private var playerView: SimpleExoPlayerView? = null
    private var componentListener: ComponentListener? = null

    private var playbackPosition: Long = 0
    private var currentWindow: Int = 0
    private var playWhenReady = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        componentListener = ComponentListener()
        playerView = findViewById(R.id.video_view)
    }

    public override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            initializePlayer()
        }
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

    public override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            releasePlayer()
        }
    }

    private fun initializePlayer() {
        if (player == null) {
            // a factory to create an AdaptiveVideoTrackSelection
            val adaptiveTrackSelectionFactory = AdaptiveTrackSelection.Factory(BANDWIDTH_METER)
            // using a DefaultTrackSelector with an adaptive video selection factory
            player = ExoPlayerFactory.newSimpleInstance(DefaultRenderersFactory(this),
                    DefaultTrackSelector(adaptiveTrackSelectionFactory), DefaultLoadControl())
            player!!.addListener(componentListener)
            playerView!!.player = player
            player!!.playWhenReady = playWhenReady
            player!!.seekTo(currentWindow, playbackPosition)
        }

        val url = "https://v.cdn.vine.co/r/videos/C40B136F021365174982178762752_53f4484ad8e.25.1.ADDA1E67-CF16-4C3B-901A-DE068DE26134.mp4"
        val mediaSource = buildMediaSource1(Uri.parse(url))
        player!!.repeatMode = Player.REPEAT_MODE_ONE
        player!!.prepare(mediaSource, true, true)
    }


    private fun releasePlayer() {
        if (player != null) {
            playbackPosition = player!!.currentPosition
            currentWindow = player!!.currentWindowIndex
            playWhenReady = player!!.playWhenReady
            player!!.removeListener(componentListener)
            player!!.release()
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

        return ConcatenatingMediaSource(videoSource)
    }

    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        playerView!!.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    private inner class ComponentListener : Player.DefaultEventListener() {

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            val stateString: String
            when (playbackState) {
                Player.STATE_IDLE -> stateString = "ExoPlayer.STATE_IDLE      -"
                Player.STATE_BUFFERING -> stateString = "ExoPlayer.STATE_BUFFERING -"
                Player.STATE_READY -> stateString = "ExoPlayer.STATE_READY     -"
                Player.STATE_ENDED -> {
                    stateString = "ExoPlayer.STATE_ENDED     -"
                    loopVideo()
                }
                else -> stateString = "UNKNOWN_STATE             -"
            }
            Log.d(TAG, "changed state to $stateString playWhenReady: $playWhenReady")
        }
    }

}
