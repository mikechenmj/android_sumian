package com.sumian.sd.scale

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.base.BaseBackActivity
import com.sumian.sd.R
import kotlinx.android.synthetic.main.activity_main_advisory.*

class ScaleListActivity : BaseBackActivity() {

    override fun getChildContentId(): Int {
        return R.layout.activity_scale_list
    }

    companion object {
        fun launch() {
            ActivityUtils.startActivity(ScaleListActivity::class.java)
        }
    }

    override fun initWidget() {
        super.initWidget()
        setTitle(R.string.scale_evaluation)
        tab_layout?.setupWithViewPager(view_pager, true)
        view_pager?.adapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return when (position) {
                    0 -> NotFilledScaleListFragment()
                    else -> FilledScaleListFragment()
                }
            }

            override fun getCount(): Int {
                return 2
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return when (position) {
                    0 -> getString(R.string.not_evaluated)
                    else -> getString(R.string.evaluated)
                }
            }
        }
    }
}
