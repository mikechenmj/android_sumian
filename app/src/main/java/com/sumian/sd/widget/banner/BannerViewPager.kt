package com.sumian.sd.widget.banner

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.sumian.common.widget.voice.IVisible
import com.sumian.sd.R

/**
 * Created by jzz
 *
 * on 2019/1/23
 *
 * desc:
 */
class BannerViewPager : ViewPager, View.OnClickListener, IVisible {

    companion object {
        private const val TAG = "BannerViewPager"
        private const val SCHEDULER_DELAY = 3 * 1000L
        private const val WHAT_SCHEDULER = 0x02
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        addOnPageChangeListener(object : SimpleOnPageChangeListener() {
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                Log.e(TAG, "onPageScrollStateChanged------->state=$state")
                if (state != ViewPager.SCROLL_STATE_IDLE) {
                    pauseLoop()
                } else {
                    scheduler()
                }
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Log.e(TAG, "onPageSelected------->position=$position")
                prepareLoop(position)
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                Log.e(TAG, "onPageScrolled----->position=$position   positionOffset=$positionOffset  positionOffsetPixels=$positionOffsetPixels")
            }
        })
        pageMargin = resources.getDimensionPixelOffset(R.dimen.space_4)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        when (ev.actionMasked) {
            MotionEvent.ACTION_MOVE -> {
                val moveX = ev.x
                val moveY = ev.y
                Log.d(TAG, "moveX=$moveX  moveY=$moveY")
            }
        }

        return super.onTouchEvent(ev)
    }

    @Volatile
    private var currentIndex = 1

    private val loopHandler by lazy {
        return@lazy object : Handler(Looper.getMainLooper()) {
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

    private var onBannerClickListener: OnBannerClickListener? = null

    fun setBannerClickListener(onBannerClickListener: OnBannerClickListener) {
        this.onBannerClickListener = onBannerClickListener
    }

    fun bindBannerList(bannerUrlList: List<String>) {
        adapter = BannerAdapter(context = context, bannerUrlList = bannerUrlList).setOnClickListener(this)
        currentIndex = 0
        setCurrentItem(currentIndex, false)
        updateView()
        scheduler()
    }

    override fun onClick(v: View) {
        val position = v.getTag(R.drawable.ic_cbti_img_banner2) as Int
        onBannerClickListener?.onClick(v, position = position)
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
        currentIndex = if (currentIndex == adapter!!.count) {
            0
        } else {
            currentIndex + 1
        }
        setCurrentItem(currentIndex, false)
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


    interface OnBannerClickListener {
        fun onClick(banner: View, position: Int)
    }
}