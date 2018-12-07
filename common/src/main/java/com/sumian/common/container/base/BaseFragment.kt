package com.sumian.common.container.base

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.os.MessageQueue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sumian.common.container.IContainer
import com.sumian.common.container.delegate.ContainerDelegate
import com.sumian.common.container.delegate.IContainerDelegate
import com.sumian.common.container.mvp.BaseShowLoadingView
import com.sumian.common.container.mvp.IPresenter

/**
 * Created by sm
 *
 * on 2018/12/5
 *
 * desc: fragment container
 *
 */
abstract class BaseFragment<Presenter : IPresenter> : Fragment(), IContainer, BaseShowLoadingView {

    private var mIsActivityCreated = false
    private var mIsViewCreated = false
    private var mIsVisibleToUser = true
    private var mRootView: View? = null

    protected var mPresenter: Presenter? = null

    private val containerDelegate: IContainerDelegate by lazy {
        return@lazy ContainerDelegate.install()
    }

    private val mPrepareInitWidgetTask by lazy {
        MessageQueue.IdleHandler {
            initWidget()
            return@IdleHandler false
        }
    }

    private val mPrepareInitDataTask by lazy {
        MessageQueue.IdleHandler {
            initData()
            return@IdleHandler false
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        this.mIsVisibleToUser = isVisibleToUser
        if (mIsActivityCreated && isVisibleToUser) {//用户可见状态，执行任务
            prepareDataTask()
        } else {//用户不可见状态，取消任务
            if (mIsViewCreated) return
            onCancel()
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        this.mIsVisibleToUser = !hidden
        if (hidden) {//调用了 hide commit 为不可见状态，取消任务
            onCancel()
        } else {//调用了show commit  该fragment 为可见状态  执行任务
            if (mIsActivityCreated) {
                prepareDataTask()
            }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        containerDelegate.setup(context = context!!)
        arguments?.let {
            initBundle(it)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (mRootView == null) {
            mRootView = inflater.inflate(getLayoutId(), container, false)
        }
        return mRootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initWidgetBefore()
        //initWidget()
        prepareWidgetTask()
        mIsViewCreated = true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        this.mIsActivityCreated = true
        if (mIsViewCreated && mIsVisibleToUser) {
            //initData()
            prepareDataTask()
            mIsViewCreated = false
        }
    }

    override fun onStop() {
        onCancel()
        super.onStop()
    }

    override fun onDestroyView() {
        mIsActivityCreated = false
        mIsViewCreated = false
        mIsVisibleToUser = false
        onRelease()
        super.onDestroyView()
    }

    override fun initWidgetBefore() {
    }

    override fun initBundle(bundle: Bundle) {
    }

    override fun initWidget() {
    }

    override fun initData() {
    }

    override fun onRelease() {
        mPresenter?.onRelease()
        containerDelegate.onRelease()
    }

    override fun onCancel() {
        mPresenter?.onCancel()
        containerDelegate.onCancel()
    }

    override fun onShowLoading() {
        containerDelegate.onShowLoading()
    }

    override fun onDismissLoading() {
        containerDelegate.onDismissLoading()
    }

    private fun prepareDataTask() {
        prepareTask(mPrepareInitDataTask)
    }

    private fun prepareWidgetTask() {
        prepareTask(mPrepareInitWidgetTask)
    }

    private fun prepareTask(task: MessageQueue.IdleHandler) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context?.mainLooper?.queue?.addIdleHandler(task)
        } else {
            Looper.myQueue().addIdleHandler(task)
        }
    }

    private fun prepareTask(block: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context?.mainLooper?.queue?.addIdleHandler {
                block.invoke()
                false
            }
        } else {
            Looper.myQueue().addIdleHandler {
                block.invoke()
                false
            }
        }
    }
}