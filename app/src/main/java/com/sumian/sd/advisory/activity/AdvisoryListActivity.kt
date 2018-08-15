package com.sumian.sd.advisory.activity

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.view.View
import com.sumian.sd.R
import com.sumian.sd.advisory.bean.Advisory
import com.sumian.sd.advisory.fragment.AdvisoryListFragment
import com.sumian.sd.advisory.presenter.AdvisoryListPresenter
import com.sumian.sd.base.SdBaseActivity
import com.sumian.sd.widget.TitleBar
import kotlinx.android.synthetic.main.activity_main_advisory.*

/**
 * Created by sm
 * on 2018/6/4 14:20
 * desc: 用户图文咨询列表
 */
class AdvisoryListActivity : SdBaseActivity<AdvisoryListPresenter>(), TitleBar.OnBackClickListener {

    override fun getLayoutId(): Int {
        return R.layout.activity_main_advisory
    }

    override fun initWidget(root: View?) {
        super.initWidget(root)

        title_bar?.setOnBackClickListener(this)
        view_pager?.adapter = object : FragmentPagerAdapter(supportFragmentManager) {

            override fun getItem(position: Int): Fragment {
                return when (position) {
                    0 -> {
                        AdvisoryListFragment.newInstance(Advisory.UNUSED_TYPE)!!
                    }
                    1 -> AdvisoryListFragment.newInstance(Advisory.USED_TYPE)!!
                    else -> AdvisoryListFragment.newInstance(Advisory.UNUSED_TYPE)!!
                }
            }

            override fun getCount(): Int {
                return 2
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return when (position) {
                    0 -> getString(R.string.unused)
                    1 -> getString(R.string.used)
                    else -> getString(R.string.unused)
                }
            }
        }

        tab_layout?.setupWithViewPager(view_pager, true)
    }

    override fun onBack(v: View?) {
        finish()
    }
}
