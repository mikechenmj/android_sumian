package com.sumian.sleepdoctor.cbti.widget.video

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

/**
 * Created by dq
 *
 * on 2018/7/17
 *
 * desc:  video  视频状态view  loading/play finished/...
 */
class VideoStatusView : FrameLayout {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(context)
    }

    private fun initView(context: Context) {

    }
}