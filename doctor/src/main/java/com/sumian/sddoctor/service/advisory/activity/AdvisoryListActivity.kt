package com.sumian.sddoctor.service.advisory.activity

import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.sddoctor.R
import com.sumian.sddoctor.base.SddBaseActivity
import com.sumian.sddoctor.constants.StatConstants
import com.sumian.sddoctor.service.advisory.bean.Advisory
import com.sumian.sddoctor.service.advisory.fragment.AdvisoryListFragment
import kotlinx.android.synthetic.main.activity_main_advisory.*

@Suppress("DEPRECATION")
/**
 * Created by sm
 * on 2018/6/4 14:20
 * desc: 用户图文咨询列表
 */
class AdvisoryListActivity : SddBaseActivity() {

    companion object {

        @JvmStatic
        fun show() {
            val topActivity = ActivityUtils.getTopActivity()
            topActivity?.let {
                it.startActivity(Intent(it, AdvisoryListActivity::class.java))
            }
        }

    }

    override fun showBackNav(): Boolean {
        return true
    }

    override fun getPageName(): String {
        return StatConstants.page_service_rtf_list
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main_advisory
    }

    override fun initWidget() {
        super.initWidget()
        setTitle(R.string.doc_advisory)
        view_pager.offscreenPageLimit = 6
        view_pager?.adapter = object : FragmentPagerAdapter(supportFragmentManager) {

            override fun getItem(position: Int): Fragment {
                return when (position) {
                    0 -> AdvisoryListFragment.newInstance(Advisory.ALL_TYPE)
                    1 -> AdvisoryListFragment.newInstance(Advisory.REPLYING_TYPE)
                    2 -> AdvisoryListFragment.newInstance(Advisory.REPLIED_TYPE)
                    3 -> AdvisoryListFragment.newInstance(Advisory.FINISHED_TYPE)
                    4 -> AdvisoryListFragment.newInstance(Advisory.CLOSED_TYPE)
                    5 -> AdvisoryListFragment.newInstance(Advisory.CANCEL_TYPE)
                    else -> throw IllegalArgumentException("invalid position")
                }
            }

            override fun getCount(): Int {
                return 6
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return when (position) {
                    0 -> "全部"
                    1 -> "待回复"
                    2 -> "已回复"
                    3 -> "已完成"
                    4 -> "已关闭"
                    5 -> "已取消"
                    else -> throw IllegalArgumentException("invalid position")
                }
            }
        }

        tab_layout?.setupWithViewPager(view_pager, true)
    }
}
