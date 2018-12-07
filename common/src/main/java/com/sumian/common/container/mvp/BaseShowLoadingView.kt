package com.sumian.common.container.mvp

/**
 * Created by sm
 *
 * on 2018/12/5
 *
 * desc: 当task需要添加loading e.y. loading 时 需要继承该 loadingView
 *
 */
interface BaseShowLoadingView : IView {
    fun onShowLoading()
    fun onDismissLoading()
}