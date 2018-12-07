package com.sumian.common.container.base

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.os.MessageQueue
import androidx.appcompat.app.AppCompatActivity
import com.sumian.common.container.IContainer
import com.sumian.common.container.delegate.ContainerDelegate
import com.sumian.common.container.delegate.IContainerDelegate
import com.sumian.common.container.mvp.BaseShowLoadingView
import com.sumian.common.container.mvp.IPresenter

/**
 * Created by sm
 *
 * on 2018/12/6
 *
 * desc:  不可导航的 activity container
 *
 */
abstract class BaseActivity<Presenter : IPresenter> : AppCompatActivity(), BaseShowLoadingView, IContainer {

    companion object {
        private val TAG = BaseActivity::class.java.simpleName
    }

    protected var mPresenter: Presenter? = null

    private val containerDelegate: IContainerDelegate by lazy {
        return@lazy ContainerDelegate.install()
    }

    private val mPrepareInitDataTask by lazy {
        MessageQueue.IdleHandler {
            initData()
            return@IdleHandler false
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        this.intent = intent
        checkBundle(this.intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        containerDelegate.setup(this@BaseActivity)
        checkBundle(intent)
        setContentView(getLayoutId())
        initWidgetBefore()
        initWidget()
        prepareDataTask()
    }

    override fun onStop() {
        onCancel()
        super.onStop()
    }

    override fun onDestroy() {
        onRelease()
        super.onDestroy()
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

    private fun checkBundle(intent: Intent?) {
        intent?.extras?.let {
            initBundle(it)
        }
    }

    private fun prepareTask(task: MessageQueue.IdleHandler) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mainLooper.queue.addIdleHandler(task)
        } else {
            Looper.myQueue().addIdleHandler(task)
        }
    }

    private fun prepareTask(block: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mainLooper.queue.addIdleHandler {
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