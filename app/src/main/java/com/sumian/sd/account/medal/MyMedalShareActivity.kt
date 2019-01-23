package com.sumian.sd.account.medal

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.view.Gravity
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.base.BasePresenterActivity
import com.sumian.common.helper.ToastHelper
import com.sumian.common.image.loadImage
import com.sumian.sd.R
import com.sumian.sd.account.medal.bean.AchievementX
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

    private lateinit var achievementX: AchievementX
    private lateinit var qrCodeUrl: String
    private lateinit var avatar: String

    companion object {
        private const val EXTRAS_ACHIEVEMENT_X = "com.sumian.sdd.extras.achievementX"
        private const val EXTRAS_QR_CODE_URL = "com.sumian.sdd.extras.qr_code_url"
        private const val EXTRAS_AVATAR = "com.sumian.sdd.extras.avatar"


        fun show(achievementX: AchievementX, qrCodeUrl: String, avatar: String) {
            ActivityUtils.getTopActivity()?.let {
                it.startActivity(Intent(it, MyMedalShareActivity::class.java).apply {
                    putExtra(EXTRAS_ACHIEVEMENT_X, achievementX)
                    putExtra(EXTRAS_QR_CODE_URL, qrCodeUrl)
                    putExtra(EXTRAS_AVATAR, avatar)
                })
            }
        }
    }

    override fun initBundle(bundle: Bundle) {
        super.initBundle(bundle)
        this.achievementX = bundle.getParcelable(EXTRAS_ACHIEVEMENT_X)!!
        this.qrCodeUrl = bundle.getString(EXTRAS_QR_CODE_URL, "")
        this.avatar = bundle.getString(EXTRAS_AVATAR, "")

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
        container.setOnClickListener {
            finish()
        }
        iv_close.setOnClickListener {
            finish()
        }
        iv_share_wx.setOnClickListener {

        }
        iv_share_wx_circle.setOnClickListener {

        }
    }

    @SuppressLint("SetTextI18n")
    override fun initData() {
        super.initData()
        tv_medal_title.text = achievementX.title
        iv_medal_icon.loadImage(achievementX.gainMedalPicture, R.drawable.ic_popups_days, R.drawable.ic_popups_days)
        tv_medal_content_title.text = "“${achievementX.sentence}”"
        tv_get_date.text = achievementX.record?.formatDate()

        Looper.myQueue().addIdleHandler {
            web_view.sWebView.loadDataWithBaseURL(null, achievementX.context, "text/html", "utf-8", null)
            return@addIdleHandler false
        }
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