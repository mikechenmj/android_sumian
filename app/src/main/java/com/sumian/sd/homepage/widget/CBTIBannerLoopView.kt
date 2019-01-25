package com.sumian.sd.homepage.widget

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.view.View
import androidx.cardview.widget.CardView
import com.sumian.common.widget.voice.IVisible
import com.sumian.sd.R
import kotlinx.android.synthetic.main.lay_cbti_banner_loop_view.view.*

/**
 * Created by jzz
 *
 * on 2019/1/22
 *
 * desc:CBTI Banner 轮播 view
 */
class CBTIBannerLoopView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr), IVisible {

    companion object {
        private const val SCHEDULER_DELAY = 3 * 1000L

        private const val WHAT_SCHEDULER = 0x02
    }

    @Volatile
    private var currentIndex = 1
    private var icons = listOf("1", "2", "3", "4")

    private val loopHandler by lazy {
        object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                when (msg.what) {
                    WHAT_SCHEDULER -> {
                        updateView()
                        scheduler()
                    }
                }
            }
        }
    }

    init {
        radius = context.resources.getDimension(R.dimen.space_6)
        View.inflate(context, R.layout.lay_cbti_banner_loop_view, this)
        minimumHeight = resources.getDimensionPixelOffset(R.dimen.space_176)
    }

    override fun show() {
        visibility = View.VISIBLE
        prepareLoop(currentIndex)
    }

    override fun hide() {
        visibility = View.GONE
        pauseLoop()
    }

    private fun updateView() {
//        iv_cbti_loop.loadImage(icons[currentIndex])
        cbti_indicator_view.scrollIndicatorFromIndex(currentIndex)
        currentIndex = if (currentIndex == icons.size) {
            0
        } else {
            currentIndex + 1
        }
    }

    fun addLoopUrls(icons: List<String>) {
        this.icons = icons
        currentIndex = 0
        updateView()
        scheduler()
    }

    fun pauseLoop() {
        clearMsg()
    }

    private fun prepareLoop(index: Int = 0) {
        this.currentIndex = index
        scheduler()
    }

    private fun scheduler() {
        clearMsg()
        loopHandler.sendEmptyMessageDelayed(WHAT_SCHEDULER, SCHEDULER_DELAY)
    }

    private fun clearMsg() {
        loopHandler.removeCallbacksAndMessages(null)
    }

}