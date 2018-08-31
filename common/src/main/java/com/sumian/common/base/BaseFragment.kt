package com.sumian.common.base

import android.content.Context
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sumian.common.dialog.LoadingDialog
import com.sumian.common.mvp.BaseShowLoadingView

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

    override fun onDetach() {
        super.onDetach()
        onRelease()
    }

    protected open fun onRelease() {

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

}
