package com.sumian.devicedemo.base

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import org.greenrobot.eventbus.EventBus
import retrofit2.Call

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/5/20 14:25
 * desc   :
 * version: 1.0
 */
abstract class BaseFragment : Fragment() {
    lateinit var mRootView: View

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        mRootView = inflater.inflate(this.getLayoutId(), container, false)
        return mRootView
    }

    abstract fun getLayoutId(): Int


    private var mCalls = HashSet<Call<*>>()

    protected val mActivity: AppCompatActivity  by lazy {
        activity as AppCompatActivity
    }

    private val mLoadingDialog: ProgressDialog by lazy {
        ProgressDialog(activity!!)
    }
//
//    override fun onAttach(context: Context?) {
//        super.onAttach(context)
//        arguments?.let {
//            initBundle(it)
//        }
//    }

//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        onInitWidgetBefore()
//        return inflater.inflate(getLayoutId(), container, false)
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initWidget()
        initData()
    }

    override fun onStart() {
        super.onStart()
        if (openEventBus()) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onStop() {
        if (openEventBus()) {
            EventBus.getDefault().unregister(this)
        }
        super.onStop()
    }

    open fun openEventBus(): Boolean {
        return false
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

    protected open fun onInitWidgetBefore() {}

    protected open fun initWidget() {

    }

    protected fun <T : View> findView(viewId: Int): T {
        return view?.findViewById(viewId)!!
    }

    protected open fun initData() {

    }

    fun showLoading() {
        if (!mLoadingDialog.isShowing) {
            mLoadingDialog.show()
        }
    }

    fun dismissLoading() {
        if (mLoadingDialog.isShowing) {
            mLoadingDialog.cancel()
        }
    }

    fun addCall(call: Call<*>) {
        mCalls.add(call)
    }

}