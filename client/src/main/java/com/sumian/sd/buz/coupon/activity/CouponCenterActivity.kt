package com.sumian.sd.buz.coupon.activity

import android.content.Intent
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.base.BaseViewModelActivity
import com.sumian.common.mvp.IPresenter
import com.sumian.sd.R
import com.sumian.sd.buz.coupon.fragment.CouponActionFragment
import com.sumian.sd.buz.coupon.fragment.CouponListFragment
import kotlinx.android.synthetic.main.activity_main_coupon_center.*
import com.sumian.common.base.BaseActivity

/**
 *
 *Created by sm
 * on 2018/6/4 18:28
 * desc:兑换中心,包含了兑换码兑换,兑换记录等
 **/
class CouponCenterActivity : BaseActivity(), androidx.viewpager.widget.ViewPager.OnPageChangeListener {

    private var onKeyBoardCallback: OnKeyBoardCallback? = null

    companion object {
        @JvmStatic
        fun show() {
            ActivityUtils.getTopActivity()?.let {
                it.startActivity(Intent(it, CouponCenterActivity::class.java))
            }
        }
    }

    override fun showBackNav(): Boolean {
        return true
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main_coupon_center
    }

    override fun initWidget() {
        super.initWidget()
        mTitleBar.openTopPadding(true)
        mTitleBar.setTitle(R.string.coupon_center)
        view_pager.adapter = object : androidx.fragment.app.FragmentPagerAdapter(supportFragmentManager) {

            override fun getItem(position: Int): androidx.fragment.app.Fragment {
                return when (position) {
                    0 -> CouponActionFragment.newInstance()
                    1 -> CouponListFragment.newInstance()
                    else -> throw IllegalStateException("invalid position")
                }
            }

            override fun getCount(): Int {
                return 2
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return when (position) {
                    0 -> getString(R.string.coupon_code)
                    1 -> getString(R.string.coupon_history)
                    else -> throw IllegalStateException("invalid position")
                }
            }
        }

        tab_layout.setupWithViewPager(view_pager, true)
        view_pager.addOnPageChangeListener(this)
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        if (position == 0) {
            onKeyBoardCallback?.showKeyBoard()
        } else {
            onKeyBoardCallback?.closeKeyBoard()
        }
    }

    fun setOnKeyBoardCallback(onKeyBoardCallback: OnKeyBoardCallback) {
        this.onKeyBoardCallback = onKeyBoardCallback
    }

    fun switchTab(position: Int) {
        view_pager?.setCurrentItem(position, true)
    }


    interface OnKeyBoardCallback {

        fun closeKeyBoard()

        fun showKeyBoard()

    }
}