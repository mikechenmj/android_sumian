package com.sumian.sleepdoctor.improve.advisory.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import com.sumian.sleepdoctor.R
import com.sumian.sleepdoctor.base.BaseActivity
import com.sumian.sleepdoctor.improve.advisory.bean.Advisory
import com.sumian.sleepdoctor.improve.advisory.contract.AdvisoryContract
import com.sumian.sleepdoctor.improve.advisory.fragment.UnusedAdvisoryFragment
import com.sumian.sleepdoctor.improve.advisory.fragment.UsedAdvisoryFragment
import com.sumian.sleepdoctor.improve.advisory.presenter.AdvisoryPresenter
import com.sumian.sleepdoctor.widget.TitleBar
import kotlinx.android.synthetic.main.activity_main_advisory.*

/**
 * Created by sm
 * on 2018/6/4 14:20
 * desc: 用户咨询列表
 */
class AdvisoryListActivity : BaseActivity<AdvisoryPresenter>(), AdvisoryContract.View, TitleBar.OnBackListener {

    companion object {
        private const val ARGS_ADVISORY_ID: String = "com.sumian.app.extras.advisory.id"
    }

    private var mAdvisoryId: Int = 0

    override fun setPresenter(presenter: AdvisoryContract.Presenter?) {
        this.mPresenter = presenter as AdvisoryPresenter?
    }

    override fun initBundle(bundle: Bundle?): Boolean {
        this.mAdvisoryId.let {
            bundle?.getInt(ARGS_ADVISORY_ID, 0)
        }
        return super.initBundle(bundle)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main_advisory
    }

    override fun initPresenter() {
        super.initPresenter()
        AdvisoryPresenter.init(this)
    }

    override fun initWidget(root: View?) {
        super.initWidget(root)

        title_bar?.addOnBackListener(this)
        view_pager?.adapter = object : FragmentPagerAdapter(supportFragmentManager) {

            override fun getItem(position: Int): Fragment {
                return when (position) {
                    0 -> UnusedAdvisoryFragment()
                    1 -> UsedAdvisoryFragment()
                    else -> UnusedAdvisoryFragment()
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
        view_pager?.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
            }
        })

    }

    override fun initData() {
        super.initData()
        this.mPresenter.getAdvisories(Advisory.UNUSED_TYPE, mAdvisoryId)
    }

    override fun onBack(v: View?) {
        finish()
    }

    override fun onGetAdvisoriesSuccess(advisories: ArrayList<Advisory>) {
    }

    override fun onGetAdvisoriesFailed(error: String) {
    }

    override fun onGetNextAdvisoriesSuccess(advisories: ArrayList<Advisory>) {
    }

    override fun onGetAdvisoryDetailSuccess(advisory: Advisory) {
    }

    override fun onGetAdvisoryDetailFailed(error: String) {
    }
}
