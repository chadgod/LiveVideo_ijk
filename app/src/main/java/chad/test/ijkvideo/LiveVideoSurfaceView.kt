package chad.test.ijkvideo

import android.util.Log
import android.view.Gravity
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.FrameLayout
import android.graphics.PixelFormat

import java.io.IOException

import tv.danmaku.ijk.media.player.IMediaPlayer
import tv.danmaku.ijk.media.player.IjkMediaPlayer

class LiveVideoSurfaceView internal constructor(act: MainActivity, layout: FrameLayout) {
    companion object {
        private val TAG = LiveVideoSurfaceView::class.java.simpleName
    }
    private var mMediaPlayer: IjkMediaPlayer? = null
    private var mActivity: MainActivity? = null
    private var mVideoSource = ""
    private var isPlaying = false
    private var mTexture: SurfaceView? = null
    private val mCallback = object : SurfaceHolder.Callback {
        override fun surfaceCreated(holder: SurfaceHolder) {
            Log.d(TAG, "surfaceCreated")
        }
        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            Log.d(TAG, "surfaceChanged")
            if (isPlaying) rePlay()
        }
        override fun surfaceDestroyed(holder: SurfaceHolder) {
            Log.d(TAG, "surfaceDestroyed")
            release(false)
        }
    }
    private val preparedListener = IMediaPlayer.OnPreparedListener{
        Log.d(TAG, "Start")
        mMediaPlayer!!.start()
        isPlaying = true
    }
    private val errorListener = IMediaPlayer.OnErrorListener {
        _,error,_ ->
        Log.d(TAG, "onError error:$error")
        if (error == -10000) {
            //connect fail
        }
        false
    }

    init {
        mActivity = act
        initTexture()
        layout.addView(mTexture)
    }

    private fun initTexture() {
        mTexture = SurfaceView(mActivity!!.getContext())
        mTexture!!.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT,
            Gravity.CENTER
        )
        mTexture!!.holder.addCallback(mCallback)
    }

    private fun initPlayer() {
        Log.d(TAG, "initPlayer")
        createPlayer()
        try {
            mMediaPlayer!!.dataSource = mVideoSource
        } catch (e: IOException) {
            e.printStackTrace()
        }
        mMediaPlayer!!.setDisplay(mTexture!!.holder)
        mMediaPlayer!!.prepareAsync()
    }

    private fun createPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer!!.stop()
            mMediaPlayer!!.setDisplay(null)
            mMediaPlayer!!.release()
        }
        mMediaPlayer = IjkMediaPlayer()
        mMediaPlayer!!.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "an", 1)
        mMediaPlayer!!.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max_cached_duration", 0)
        mMediaPlayer!!.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "infbuf", 1)
        mMediaPlayer!!.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzeduration", 1)
        mMediaPlayer!!.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "flush_packets", 1)
        mMediaPlayer!!.setOnPreparedListener(preparedListener)
        mMediaPlayer!!.setOnErrorListener(errorListener)
    }

    fun play(source: String) {
        if (mTexture != null) {
            mTexture!!.holder.setFormat(PixelFormat.OPAQUE)
        }
        mVideoSource = source
        initPlayer()
    }

    private fun rePlay() {
        if (mVideoSource != "") {
            play(mVideoSource)
        }
    }

    fun pause() {
        Log.d(TAG, "Pause")
        if (mMediaPlayer != null) {
            mMediaPlayer!!.pause()
        }
    }

    fun stop() {
        Log.d(TAG, "Stop")
        if (mMediaPlayer != null) {
            mMediaPlayer!!.stop()
        }
    }

    fun release(clearScreen: Boolean) {
        Log.d(TAG, "Release clearScreen:$clearScreen")
        if (mTexture != null && clearScreen) {
            mTexture!!.holder.setFormat(PixelFormat.TRANSLUCENT)
            isPlaying = false
        }
        if (mMediaPlayer != null) {
            mMediaPlayer!!.stop()
            mMediaPlayer!!.release()
            mMediaPlayer = null
        }
    }

}
