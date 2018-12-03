package com.sumian.common.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.sumian.common.dialog.LoadingDialog
import com.sumian.common.mvp.BaseShowLoadingView
import retrofit2.Call

/**
 * Created by sm
 *
 *
 * on 2018/8/1
 *
 *
 * desc:
 */
abstract class BaseFragment : Fragment(), BaseShowLoadingView {

    private var mCalls = HashSet<Call<*>>()

    protected val mActivity: AppCompatActivity  by lazy {
        activity as AppCompatActivity
    }

    private val mLoadingDialog: LoadingDialog by lazy {
        LoadingDialog(activity!!)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        arguments?.let {
            initBundle(it)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        onInitWidgetBefore()
        return inflater.inflate(getLayoutId(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initWidget()
        initData()
    }

    override fun onDestroyView() {
        onRelease()
        super.onDestroyView()
    }

    protected open fun onRelease() {
        for (call in mCalls) {
            call.cancel()
        }
    }

    protected open fun initBundle(bundle: Bundle) {

    }

    @LayoutRes
    abstract fun getLayoutId(): Int

    protected open fun onInitWidgetBefore() {}

    protected open fun initWidget() {

    }

    protected fun <T : View> findView(viewId: Int): T {
        return view?.findViewById(viewId)!!
    }

    protected open fun initData() {

    }

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

    fun addCall(call: Call<*>) {
        mCalls.add(call)
    }
}
