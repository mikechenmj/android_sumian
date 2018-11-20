package com.sumian.common.base

import android.content.Intent
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.sumian.common.R
import com.sumian.common.dialog.LoadingDialog
import com.sumian.common.mvp.BaseShowLoadingView
import com.sumian.common.widget.TitleBar
import retrofit2.Call

/**
 * Created by sm
 *
 * on 2018/8/1
 *
 * desc:
 *
 */
abstract class BaseActivity : AppCompatActivity(), BaseShowLoadingView {

    protected val mBackRootView: LinearLayout  by lazy {
        val layout = findViewById<LinearLayout>(R.id.lay_child_content_container)
                ?: throw RuntimeException("please return true in showBackNav()")
        layout
    }

    protected val mTitleBar: TitleBar by lazy {
        val titleBar = findViewById<TitleBar>(R.id.title_bar)
                ?: throw RuntimeException("please return true in showBackNav()")
        titleBar
    }

    private val mLoadingDialog: LoadingDialog by lazy {
        LoadingDialog(this)
    }

    private val mCalls = HashSet<Call<*>>()
    private val mActivityDelegate = BaseActivityManager.createActivityDelegate(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkBundle(intent)
        setContentView(if (showBackNav()) R.layout.activity_main_back_container else getLayoutId())
        initWidgetBefore()
        initWidget()
        initData()
        mActivityDelegate.onCreate(savedInstanceState)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        checkBundle(intent)
        mActivityDelegate.onNewIntent(intent)
    }

    override fun onStart() {
        super.onStart()
        mActivityDelegate.onStart()
    }

    override fun onResume() {
        super.onResume()
        mActivityDelegate.onResume()
    }

    override fun onPause() {
        super.onPause()
        mActivityDelegate.onPause()
    }

    override fun onStop() {
        super.onStop()
        mActivityDelegate.onStop()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mActivityDelegate.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        for (call in mCalls) {
            call.cancel()
        }
        onRelease()
        super.onDestroy()
        mActivityDelegate.onDestroy()
    }

    protected open fun initBundle(bundle: Bundle) {}

    @LayoutRes
    protected abstract fun getLayoutId(): Int

    protected open fun initData() {}

    protected open fun initWidget() {
        if (showBackNav()) {
            mTitleBar.setOnBackClickListener { onBackPressed() }
            val childContent = LayoutInflater.from(this).inflate(getLayoutId(), mBackRootView, false)
            mBackRootView.addView(childContent)
        }
    }

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

    fun addCall(call: Call<*>) {
        mCalls.add(call)
    }

    open fun showBackNav(): Boolean {
        return false
    }

    override fun setTitle(titleId: Int) {
        setTitle(resources.getString(titleId))
    }

    fun setTitle(title: String) {
        mTitleBar.setTitle(title)
    }
}