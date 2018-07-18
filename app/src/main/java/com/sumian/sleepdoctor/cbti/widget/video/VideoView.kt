package com.sumian.sleepdoctor.cbti.widget.video

import android.content.Context
import android.graphics.SurfaceTexture
import android.util.AttributeSet
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.widget.FrameLayout
import com.sumian.sleepdoctor.R
import kotlinx.android.synthetic.main.lay_video_view.view.*

/**
 * Created by dq
 *
 * on 2018/7/17
 *
 * desc: 自定义 cbti video view
 */
class VideoView : FrameLayout, VideoBannerView.OnBannerCallback, VideoController.OnControllerCallback, TextureView.SurfaceTextureListener {

    private val mediaPlayer: MediaPlayer by lazy {
        MediaPlayer.create(context)
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(context)
    }


    private fun initView(context: Context) {
        View.inflate(context, R.layout.lay_video_view, this)
        video_banner.setOnBannerCallback(this)
        video_controller.setOnControllCallback(this)
        texture_view.surfaceTextureListener = this
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
        // SurfaceTexture缓冲大小变化

    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
        // SurfaceTexture通过updateImage更新
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
        // SurfaceTexture即将被销毁
        return false
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
        // SurfaceTexture准备就绪,打开播放器
        openMediaPlayer(surface)
    }

    private fun openMediaPlayer(surface: SurfaceTexture?) {
            mediaPlayer.setSurface(Surface(surface))
    }


    override fun showContent() {

    }

    override fun play() {
    }

    override fun pause() {
    }

    override fun exitFullScreen() {

    }

    override fun showMenu() {
    }
}