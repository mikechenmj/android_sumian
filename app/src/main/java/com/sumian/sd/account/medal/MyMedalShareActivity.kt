package com.sumian.sd.account.medal

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.base.BasePresenterActivity
import com.sumian.common.helper.ToastHelper
import com.sumian.sd.R
import com.sumian.sd.account.medal.bean.MyMedalShare
import com.sumian.sd.account.medal.contract.MyMedalShareContract
import com.sumian.sd.account.medal.presenter.MyMedalSharePresenter
import kotlinx.android.synthetic.main.activity_main_my_medal_share.*

/**
 * Created by jzz
 *
 * on 2019/1/22
 *
 * desc: 我的勋章成就分享
 */
class MyMedalShareActivity : BasePresenterActivity<MyMedalShareContract.Presenter>(), MyMedalShareContract.View {

    private var id = 0

    companion object {
        private const val EXTRAS_ID = "com.sumian.sdd.extras.id"

        fun show(id: Int) {
            ActivityUtils.getTopActivity()?.let {
                it.startActivity(Intent(it, MyMedalShareActivity::class.java).apply {
                    putExtra(EXTRAS_ID, id)
                })
            }
        }
    }

    override fun initBundle(bundle: Bundle) {
        super.initBundle(bundle)
        this.id = bundle.getInt(EXTRAS_ID, 0)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main_my_medal_share
    }

    override fun initWidgetBefore() {
        super.initWidgetBefore()
        this.mPresenter = MyMedalSharePresenter.create(this)
    }

    override fun initWidget() {
        super.initWidget()
        iv_share_wx.setOnClickListener {

        }
        iv_share_wx_circle.setOnClickListener {

        }
    }

    override fun initData() {
        super.initData()
        mPresenter?.getMyMedal(id)
    }

    override fun onGetMyMedalSuccess(medalShare: MyMedalShare) {

    }

    override fun onGetMyMedalFailed(error: String) {
        ToastHelper.show(this, error, Gravity.CENTER)
    }

    override fun onShareSuccess(shareUrlPath: String) {

    }

    override fun onShareFailed(error: String) {
        onGetMyMedalFailed(error)
    }

}