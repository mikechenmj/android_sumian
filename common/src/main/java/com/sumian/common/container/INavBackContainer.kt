package com.sumian.common.container

import androidx.annotation.LayoutRes

/**
 * Created by sm
 *
 * on 2018/12/6
 *
 * desc:
 *
 */
interface INavBackContainer : IContainer {
    @LayoutRes
    fun getChildLayoutId(): Int

    fun getTitleBarTitle(): String
}