package com.sumian.common.container

import android.os.Bundle
import androidx.annotation.LayoutRes

/**
 * Created by sm
 *
 * on 2018/12/5
 *
 * desc:
 *
 */
interface IContainer {
    /**
     * init bundle
     * @param bundle Bundle
     */
    fun initBundle(bundle: Bundle)

    /**
     * set layout sources
     * @return Int
     */
    @LayoutRes
    fun getLayoutId(): Int

    /**
     * this method can init presenter/...   e.g.  do transform action
     */
    fun initWidgetBefore()

    /**
     * init widget
     */
    fun initWidget()

    /**
     * init data (e.g. can load data)
     */
    fun initData()

    /**
     * cancel the task
     */
    fun onCancel()

    /**
     * release the task/resource
     */
    fun onRelease()

}