package com.sumian.common.base

import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.sumian.common.R
import com.sumian.common.widget.TitleBar

/**
 * Created by sm
 *
 *
 * on 2018/8/1
 *
 *
 * desc:
 */
abstract class BaseBackActivity : BaseActivity() {

    private val mParent: LinearLayout  by lazy {
        findViewById<LinearLayout>(R.id.lay_child_content_container)
    }

    protected val mTitleBar: TitleBar by lazy {
        findViewById<TitleBar>(R.id.title_bar)
    }

    @LayoutRes
    override fun getLayoutId(): Int {
        return R.layout.activity_main_back_container
    }

    @LayoutRes
    protected abstract fun getChildContentId(): Int

    override fun initWidget() {
        mTitleBar.setOnBackClickListener { onBackPressed() }
        val childContent = LayoutInflater.from(this).inflate(getChildContentId(), mParent, false)
        this.mParent.addView(childContent)
    }


    override fun initData() {

    }

    override fun setTitle(titleId: Int) {
        mTitleBar.setTitle(resources.getString(titleId))
    }
}
