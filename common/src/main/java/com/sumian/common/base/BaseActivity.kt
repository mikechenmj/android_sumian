package com.sumian.common.base

import android.content.Intent
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import com.sumian.common.dialog.LoadingDialog
import com.sumian.common.mvp.BaseShowLoadingView

/**
 * Created by sm
 *
 * on 2018/8/1
 *
 * desc:
 *
 */
abstract class BaseActivity : AppCompatActivity(), BaseShowLoadingView {

    private val mLoadingDialog: LoadingDialog by lazy {
        LoadingDialog(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkBundle(intent)
        setContentView(getLayoutId())
        initWidgetBefore()
        initWidget()
        initData()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        checkBundle(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        onRelease()
    }

    protected open fun initBundle(bundle: Bundle) {}

    @LayoutRes
    protected abstract fun getLayoutId(): Int

    protected open fun initData() {}

    protected open fun initWidget() {}

    protected open fun onRelease() {}

    /**
     * this method can init presenter/...
     */
    protected open fun initWidgetBefore() {}

    override fun showLoading() {
        if (!mLoadingDialog.isShowing) {
            mLoadingDialog.show()
        }
    }

    override fun dismissLoading() {
        if (mLoadingDialog.isShowing) {
            mLoadingDialog.cancel()
        }
    }

    private fun checkBundle(intent: Intent?) {
        intent?.extras?.let {
            initBundle(it)
        }
    }
}