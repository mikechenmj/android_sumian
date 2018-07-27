package com.sumian.sleepdoctor.cbti.widget.video

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.sumian.sleepdoctor.R
import kotlinx.android.synthetic.main.lay_video_banner_view.view.*

/**
 * Created by dq
 *
 * on 2018/7/17
 *
 * desc:视频播放 banner view  用于返回全屏播放状态或者 展示 附加功能,e.g. 倍速播放/质量调节/等等
 */
class VideoBannerView : LinearLayout, View.OnClickListener {

    private var mOnBannerCallback: OnBannerCallback? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(context)
    }

    private fun initView(context: Context?) {
        View.inflate(context, R.layout.lay_video_banner_view, this)
        iv_back.setOnClickListener(this)
        iv_menu.setOnClickListener(this)
    }

    fun setOnBannerCallback(onBannerCallback: OnBannerCallback) {
        this.mOnBannerCallback = onBannerCallback
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_back -> {
                mOnBannerCallback?.exitFullScreen()
            }
            R.id.iv_menu -> {
                mOnBannerCallback?.showMenu()
            }
        }
    }


    interface OnBannerCallback {

        fun exitFullScreen()

        fun showMenu()
    }
}