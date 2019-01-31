package com.sumian.sd.buz.tel.activity

import android.content.Intent
import android.os.Bundle
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.base.BasePresenterActivity
import com.sumian.common.mvp.IPresenter
import com.sumian.sd.R
import com.sumian.sd.buz.tel.bean.TelBooking
import com.sumian.sd.buz.tel.fragment.TelBookingListFragment
import kotlinx.android.synthetic.main.activity_main_tel_booking.*

/**
 *
 *Created by sm
 * on 2018/6/4 18:28
 * desc:咨询详情,包含了提问或者回复的记录列表,在线报告列表
 **/
class TelBookingListActivity : BasePresenterActivity<IPresenter>() {
    private var mType = TelBooking.UN_FINISHED_TYPE

    companion object {
        private const val KEY_TYPE = "type"

        @JvmStatic
        fun show() {
            ActivityUtils.getTopActivity()?.let {
                it.startActivity(Intent(it, TelBookingListActivity::class.java))
            }
        }

        fun getLaunchIntent(type: Int): Intent {
            val intent = Intent(ActivityUtils.getTopActivity(), TelBookingListActivity::class.java)
            intent.putExtra(KEY_TYPE, type)
            return intent
        }

    }

    override fun showBackNav(): Boolean {
        return true
    }

    override fun initBundle(bundle: Bundle) {
        super.initBundle(bundle)
        mType = bundle.getInt(KEY_TYPE, TelBooking.UN_FINISHED_TYPE)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main_tel_booking
    }

    override fun initWidget() {
        super.initWidget()
        mTitleBar.setTitle("电话预约")
        view_pager.adapter = object : androidx.fragment.app.FragmentPagerAdapter(supportFragmentManager) {

            override fun getItem(position: Int): androidx.fragment.app.Fragment {
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
        view_pager.currentItem = if (mType == TelBooking.UN_FINISHED_TYPE) 0 else 1
    }

}