package com.sumian.sddoctor.base

import android.app.Activity
import android.app.Application
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sumian.sddoctor.util.EventBusUtil
import com.sumian.sddoctor.widget.LoadingDialog
import retrofit2.Call
import java.util.*

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/13 17:58
 *     desc   :
 *     version: 1.0
 * </pre>
 *
 * @see SddBaseActivity
 */
@Deprecated("see #SddBaseActivity")
@Suppress("MemberVisibilityCanBePrivate")
abstract class BaseActivity : AppCompatActivity(), BaseView {
    protected var mCalls: MutableSet<Call<*>> = HashSet()

    private val mLoadingDialog: LoadingDialog by lazy {
        LoadingDialog(this)
    }

    protected val mActivity: Activity by lazy { this }

    companion object {

        @JvmStatic
        fun show(context: Context, clx: Class<out BaseActivity>) {
            show(context, clx, null)
        }

        @JvmStatic
        fun show(context: Context, clx: Class<out BaseActivity>, extras: Bundle?) {
            val intent = Intent(context, clx)
            if (extras != null) {
                intent.putExtras(extras)
            }
            show(context, intent)
        }

        @JvmStatic
        fun show(context: Context, intent: Intent) {
            if (context is Application || context is Service) {
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            context.startActivity(intent)
        }

        @JvmStatic
        fun showClearTop(context: Context, clx: Class<out BaseActivity>) {
            showClearTop(context, clx, null)
        }

        @JvmStatic
        fun showClearTop(context: Context, clx: Class<out BaseActivity>, bundle: Bundle?) {
            val intent = Intent(context, clx)
            bundle?.let {
                intent.putExtras(bundle)
            }
            showClearTop(context, intent)
        }

        @JvmStatic
        fun showClearTop(context: Context, intent: Intent) {
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(getContentId())
        initBundle(intent.extras)
        initPresenter()
        initWidget()
        initData()
        mActivityDelegate.onCreate(savedInstanceState)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initBundle(intent?.extras)
    }

    protected abstract fun getContentId(): Int


    protected open fun initBundle(bundle: Bundle?) {

    }

    protected open fun initPresenter() {

    }

    protected open fun initWidget() {

    }

    protected open fun initData() {

    }

    protected open fun openEventBus(): Boolean {
        return false
    }

    override fun onStart() {
        super.onStart()
        if (openEventBus()) EventBusUtil.register(this)
        mActivityDelegate.onStart()
    }

    override fun onStop() {
        super.onStop()
        if (openEventBus()) EventBusUtil.unregister(this)
        mActivityDelegate.onStop()
    }

    override fun showLoading() {
        super.showLoading()
        if (!mLoadingDialog.isShowing) {
            mLoadingDialog.show()
        }
    }

    override fun dismissLoading() {
        super.dismissLoading()
        if (mLoadingDialog.isShowing) {
            mLoadingDialog.dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        for (call in mCalls) {
            call.cancel()
        }
        mActivityDelegate.onDestroy()
    }

    protected fun addCall(call: Call<*>) {
        mCalls.add(call)
    }

    private val mActivityDelegate = BaseActivityDelegate(this)


    override fun onResume() {
        super.onResume()
        mActivityDelegate.onResume()
    }

    override fun onPause() {
        super.onPause()
        mActivityDelegate.onPause()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mActivityDelegate.onActivityResult(requestCode, resultCode, data)
    }
}