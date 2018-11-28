package com.sumian.sd.service.advisory.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.sd.R
import com.sumian.sd.base.SdBaseActivity
import com.sumian.sd.service.advisory.bean.Advisory
import com.sumian.sd.service.advisory.fragment.AdvisoryListFragment
import com.sumian.sd.service.advisory.presenter.AdvisoryListPresenter
import com.sumian.sd.widget.TitleBar
import kotlinx.android.synthetic.main.activity_main_advisory.*

/**
 * Created by sm
 * on 2018/6/4 14:20
 * desc: 用户图文咨询列表
 */
class AdvisoryListActivity : SdBaseActivity<AdvisoryListPresenter>(), TitleBar.OnBackClickListener {
    private var mType = Advisory.UNFINISHED_TYPE

    companion object {
        private const val KEY_TYPE = "type"
        @JvmStatic
        fun getLaunchIntent(type: Int): Intent {
            val intent = Intent(ActivityUtils.getTopActivity(), AdvisoryListActivity::class.java)
            intent.putExtra(KEY_TYPE, type)
            return intent
        }

        @JvmStatic
        fun show() {
            ActivityUtils.getTopActivity()?.let {
                it.startActivity(Intent(it, AdvisoryListActivity::class.java))
            }
        }
    }

    override fun initBundle(bundle: Bundle?): Boolean {
        mType = bundle?.getInt(KEY_TYPE) ?: Advisory.UNFINISHED_TYPE
        return super.initBundle(bundle)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main_advisory
    }

    override fun initWidget(root: View?) {
        super.initWidget(root)

        title_bar?.setOnBackClickListener(this)
        view_pager?.adapter = object : androidx.fragment.app.FragmentPagerAdapter(supportFragmentManager) {

            override fun getItem(position: Int): androidx.fragment.app.Fragment {
                return when (position) {
                    0 -> {
                        AdvisoryListFragment.newInstance(Advisory.UNFINISHED_TYPE)!!
                    }
                    1 -> AdvisoryListFragment.newInstance(Advisory.FINISHED_TYPE)!!
                    else -> AdvisoryListFragment.newInstance(Advisory.UNFINISHED_TYPE)!!
                }
            }

            override fun getCount(): Int {
                return 2
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return when (position) {
                    0 -> getString(R.string.un_finished)
                    1 -> getString(R.string.finished)
                    else -> getString(R.string.un_finished)
                }
            }
        }

        tab_layout?.setupWithViewPager(view_pager, true)
        view_pager.currentItem = if (mType == Advisory.UNFINISHED_TYPE) 0 else 1
    }

    override fun onBack(v: View?) {
        finish()
    }
}
