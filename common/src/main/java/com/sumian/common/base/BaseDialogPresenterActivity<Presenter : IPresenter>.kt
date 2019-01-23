package com.sumian.common.base

import android.os.Bundle
import android.view.ViewGroup
import com.sumian.common.R
import com.sumian.common.mvp.IPresenter

/**
 * Created by sm
 *
 * on 2018/8/1
 *
 * desc:
 *
 */
open class BaseDialogPresenterActivity<Presenter : IPresenter> : BasePresenterActivity<Presenter>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }
    override fun getLayoutId(): Int {
        return R.layout.activity_dialog
    }

    override fun portrait(): Boolean {
        return false
    }

}