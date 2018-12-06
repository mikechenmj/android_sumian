package com.sumian.common.container.mvp

/**
 * Created by sm
 *
 * on 2018/12/5
 *
 * desc: 当操作需要添加屏蔽 e.y. loading 时 需要继承该 loading
 *
 */
interface BaseShowLoadingView : IView {

    fun onShowLoading()

    fun onDismissLoading()

}