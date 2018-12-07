package com.sumian.common.container.base

import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.sumian.common.R
import com.sumian.common.container.INavBackContainer
import com.sumian.common.container.mvp.BaseShowLoadingView
import com.sumian.common.container.mvp.IPresenter
import com.sumian.common.widget.TitleBar
import kotlinx.android.synthetic.main.common_activity_base_nav_back_container.*

/**
 * Created by sm
 *
 * on 2018/12/6
 *
 * desc:可导航的 activity container
 *
 */
abstract class BaseNavBackActivity<Presenter : IPresenter> : BaseActivity<Presenter>(), BaseShowLoadingView, INavBackContainer,
        TitleBar.OnBackClickListener {

    override fun getLayoutId(): Int {
        return R.layout.common_activity_base_nav_back_container
    }

    override fun initWidget() {
        super.initWidget()
        lay_nav_back_container.addView(inflateChildView(getChildLayoutId(), lay_nav_back_container))
        title_bar.setTitle(getTitleBarTitle())
        title_bar.setOnBackClickListener(this)
    }

    override fun onBack(v: View?) {
        onBackPressed()
    }

    private fun inflateChildView(@LayoutRes layoutId: Int, parent: ViewGroup?): ViewGroup =
            layoutInflater.inflate(layoutId, parent, false) as ViewGroup

}