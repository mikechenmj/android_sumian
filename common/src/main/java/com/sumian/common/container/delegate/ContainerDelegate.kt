package com.sumian.common.container.delegate

import android.content.Context
import com.sumian.common.dialog.LoadingDialog
import retrofit2.Call
import java.lang.ref.WeakReference

/**
 * Created by sm
 *
 * on 2018/12/5
 *
 * desc:
 *
 */
class ContainerDelegate : IContainerDelegate {

    companion object {

        @JvmStatic
        fun install(): ContainerDelegate {
            return ContainerDelegate()
        }
    }

    private lateinit var weakActivity: WeakReference<Context>

    private val mLoadingDialog: LoadingDialog by lazy {
        LoadingDialog(weakActivity.get()!!)
    }

    private val mCalls by lazy {
        hashSetOf<Call<*>>()
    }

    override fun setup(context: Context) {
        weakActivity = WeakReference(context)
    }

    override fun onShowLoading() {
        if (!mLoadingDialog.isShowing) {
            mLoadingDialog.show()
        }
    }

    override fun onDismissLoading() {
        if (mLoadingDialog.isShowing) {
            mLoadingDialog.cancel()
        }
    }

    override fun addCallAction(call: Call<*>) {
        mCalls.add(call)
    }

    override fun onCancel() {
        for (call in mCalls) {
            //if (call.isExecuted && !call.isCanceled) {
            call.cancel()
            //}
        }
        mCalls.clear()
    }

    override fun onRelease() {
        onCancel()
    }

}