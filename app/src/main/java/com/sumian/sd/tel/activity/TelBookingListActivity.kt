package com.sumian.sd.tel.activity

import android.content.Intent
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.base.BaseBackPresenterActivity
import com.sumian.common.mvp.IPresenter
import com.sumian.sd.R
import com.sumian.sd.tel.bean.TelBooking
import com.sumian.sd.tel.fragment.TelBookingListFragment
import kotlinx.android.synthetic.main.activity_main_tel_booking.*

/**
 *
 *Created by sm
 * on 2018/6/4 18:28
 * desc:咨询详情,包含了提问或者回复的记录列表,在线报告列表
 **/
class TelBookingListActivity : BaseBackPresenterActivity<IPresenter>() {

    companion object {

        @JvmStatic
        fun show() {
            ActivityUtils.getTopActivity()?.let {
                it.startActivity(Intent(it, TelBookingListActivity::class.java))
            }
        }

    }

    override fun getChildContentId(): Int {
        return R.layout.activity_main_tel_booking
    }

    override fun initWidget() {
        super.initWidget()
        mTitleBar.setTitle("电话预约")
        view_pager.adapter = object : FragmentPagerAdapter(supportFragmentManager) {

            override fun getItem(position: Int): Fragment {
                return when (position) {
                    0 -> TelBookingListFragment.newInstance(TelBooking.UN_FINISHED_TYPE)
                    1 -> TelBookingListFragment.newInstance(TelBooking.IS_FINISHED_TYPE)
                    else -> throw IllegalStateException("invalid position")
                }
            }

            override fun getCount(): Int {
                return 2
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return when (position) {
                    0 -> getString(R.string.un_finished)
                    1 -> getString(R.string.is_finished)
                    else -> getString(R.string.un_finished)
                }
            }
        }

        tab_layout.setupWithViewPager(view_pager, true)
    }

}