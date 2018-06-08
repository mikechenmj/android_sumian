package com.sumian.sleepdoctor.improve.advisory.activity

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.view.View
import com.sumian.sleepdoctor.R
import com.sumian.sleepdoctor.base.BaseActivity
import com.sumian.sleepdoctor.improve.advisory.bean.Advisory
import com.sumian.sleepdoctor.improve.advisory.fragment.AdvisoryListFragment
import com.sumian.sleepdoctor.improve.advisory.presenter.AdvisoryListPresenter
import com.sumian.sleepdoctor.widget.TitleBar
import kotlinx.android.synthetic.main.activity_main_advisory.*

/**
 * Created by sm
 * on 2018/6/4 14:20
 * desc: 用户图文咨询列表
 */
class AdvisoryListActivity : BaseActivity<AdvisoryListPresenter>(), TitleBar.OnBackListener {

    override fun getLayoutId(): Int {
        return R.layout.activity_main_advisory
    }

    override fun initWidget(root: View?) {
        super.initWidget(root)

        title_bar?.setOnBackListener(this)
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
