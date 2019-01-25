package com.sumian.sd.account.achievement

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.base.BasePresenterActivity
import com.sumian.common.helper.ToastHelper
import com.sumian.common.image.loadImage
import com.sumian.common.utils.ViewToImageFileListener
import com.sumian.sd.R
import com.sumian.sd.account.achievement.bean.Achievement
import com.sumian.sd.account.achievement.bean.LastAchievementData
import com.sumian.sd.account.achievement.bean.Record
import com.sumian.sd.account.achievement.bean.ShareAchievement
import com.sumian.sd.account.achievement.contract.MyAchievementShareContract
import com.sumian.sd.account.achievement.presenter.MyAchievementSharePresenter
import com.umeng.socialize.UMShareListener
import com.umeng.socialize.bean.SHARE_MEDIA
import kotlinx.android.synthetic.main.activity_main_my_achievement_share.*
import java.io.File


/**
 * Created by jzz
 *
 * on 2019/1/22
 *
 * desc: 我的勋章成就分享
 */
class MyAchievementShareActivity : BasePresenterActivity<MyAchievementShareContract.Presenter>(), UMShareListener, ViewToImageFileListener {

    private var shareAchievement: ShareAchievement? = null

    companion object {
        private const val TAG = "MyAchievementShareActivity"

        private const val EXTRAS_SHARE_ACHIEVEMENT = "com.sumian.sdd.extras.share.achievement"

        @JvmStatic
        fun show(shareAchievement: ShareAchievement) {
            ActivityUtils.getTopActivity()?.let {
                it.startActivity(Intent(it, MyAchievementShareActivity::class.java).apply {
                    putExtra(EXTRAS_SHARE_ACHIEVEMENT, shareAchievement)
                })
            }
        }

        @JvmStatic
        fun showFromLastAchievement(lastAchievementData: LastAchievementData) {
            val shareAchievement = ShareAchievement(achievement = Achievement
            (
                    achievementCategoryId = lastAchievementData.achievement.achievementCategoryId,
                    context = lastAchievementData.achievement.context,
                    createdAt = (System.currentTimeMillis() / 1000L).toInt(),
                    gainMedalPicture = lastAchievementData.achievement.gainMedalPicture,
                    id = lastAchievementData.achievement.id,
                    notGainMedalPicture = lastAchievementData.achievement.notGainMedalPicture,
                    record = Record(
                            lastAchievementData.achievement.achievement_category.id,
                            lastAchievementData.rewardedAt,
                            lastAchievementData.popAt
                    ),
                    sentence = lastAchievementData.achievement.sentence,
                    title = lastAchievementData.achievement.title,
                    type = lastAchievementData.achievement.type,
                    updatedAt = (System.currentTimeMillis() / 1000L).toInt()
            ),

                    createdAt = (System.currentTimeMillis() / 1000L).toInt(),
                    name = lastAchievementData.achievement.achievement_category.name,
                    type = lastAchievementData.achievement.achievement_category.type,
                    updatedAt = (System.currentTimeMillis() / 1000L).toInt(),
                    avatar = lastAchievementData.meta.avatar,
                    qrCode = lastAchievementData.meta.qrCode,
                    id = lastAchievementData.id)

            ActivityUtils.getTopActivity()?.let {
                it.startActivity(Intent(it, MyAchievementShareActivity::class.java).apply {
                    putExtra(EXTRAS_SHARE_ACHIEVEMENT, shareAchievement)
                })
            }
        }
    }

    override fun initBundle(bundle: Bundle) {
        super.initBundle(bundle)
        this.shareAchievement = bundle.getParcelable(EXTRAS_SHARE_ACHIEVEMENT)!!
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main_my_achievement_share
    }

    override fun initWidgetBefore() {
        super.initWidgetBefore()
        this.mPresenter = MyAchievementSharePresenter.create()
    }

    @SuppressLint("SetTextI18n")
    override fun initWidget() {
        super.initWidget()
        shadow.setOnClickListener { finish() }
        iv_close.setOnClickListener {
            finish()
        }
        iv_share_wx.setOnClickListener {
            mPresenter?.share(this, SHARE_MEDIA.WEIXIN, achievement_share_view, this)
        }
        iv_share_wx_circle.setOnClickListener {
            mPresenter?.share(this, SHARE_MEDIA.WEIXIN_CIRCLE, achievement_share_view, this)
        }
        iv_save_bitmap.setOnClickListener {
            mPresenter?.saveShareView(achievement_share_view, this)
        }
        shareAchievement?.let {
            val achievement = it.achievement
            iv_medal_icon.loadImage(achievement.gainMedalPicture, R.drawable.ic_popups_days, R.drawable.ic_popups_days)
            tv_medal_title.text = achievement.title
            tv_get_date.text = achievement.record?.formatDate()
            tv_medal_content_title.text = achievement.sentence
            web_view.post {
                web_view.sWebView.loadDataWithBaseURL(null, formatHtml(achievement.context), "text/html", "utf-8", null)
            }
            achievement_share_view.bindAchievement(it)
        }
    }

    @SuppressLint("LongLogTag")
    override fun onComplete(file: File) {
        showToast("勋章保存成功啦,请在相册中查看")
    }

    override fun onError(t: Throwable) {
        t.printStackTrace()
    }

    override fun onStart(shareMedia: SHARE_MEDIA?) {
        //showToast("正在分享中")
    }

    override fun onCancel(shareMedia: SHARE_MEDIA?) {
        showToast("分享已取消")
    }

    override fun onResult(shareMedia: SHARE_MEDIA?) {
        showToast("分享成功")
    }

    override fun onError(shareMedia: SHARE_MEDIA?, throwable: Throwable?) {
        showToast("分享失败")
    }

    fun Activity.showToast(text: String) {
        ToastHelper.show(this, text, Gravity.CENTER)
    }

}