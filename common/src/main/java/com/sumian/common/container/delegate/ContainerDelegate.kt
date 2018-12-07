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
 * desc: container delegate impl
 *
 */
class ContainerDelegate : IContainerDelegate {

    companion object {
        @JvmStatic
        fun install(): ContainerDelegate {
            return ContainerDelegate()
        }
    }

    private lateinit var weakContext: WeakReference<Context>

    private val mLoadingDialog: LoadingDialog by lazy {
        LoadingDialog(weakContext.get()!!)
    }

    private val mCalls by lazy {
        hashSetOf<Call<*>>()
    }

    override fun setup(context: Context) {
        weakContext = WeakReference(context)
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

    override fun addCall(call: Call<*>) {
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