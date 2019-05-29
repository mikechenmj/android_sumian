package com.sumian.sd.buz.account.achievement

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.media.MediaScannerConnection
import android.os.Bundle
import android.view.Gravity
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.base.BaseViewModelActivity
import com.sumian.common.helper.ToastHelper
import com.sumian.common.image.loadImage
import com.sumian.common.statistic.StatUtil
import com.sumian.common.utils.ViewToImageFileListener
import com.sumian.sd.R
import com.sumian.sd.buz.account.achievement.bean.Achievement
import com.sumian.sd.buz.account.achievement.bean.LastAchievementData
import com.sumian.sd.buz.account.achievement.bean.Record
import com.sumian.sd.buz.account.achievement.bean.ShareAchievement
import com.sumian.sd.buz.account.achievement.presenter.MyAchievementSharePresenter
import com.sumian.sd.buz.stat.StatConstants
import com.umeng.socialize.UMShareListener
import com.umeng.socialize.bean.SHARE_MEDIA
import kotlinx.android.synthetic.main.activity_main_my_achievement_share.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.File


/**
 * Created by jzz
 *
 * on 2019/1/22
 *
 * desc: 我的勋章成就分享
 */
class MyAchievementShareActivity : BaseViewModelActivity<MyAchievementSharePresenter>(), UMShareListener, ViewToImageFileListener, EasyPermissions.PermissionCallbacks {

    private var shareAchievement: ShareAchievement? = null

    companion object {
        private const val WRITE_PERMISSION = 0x01

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
        this.mViewModel = MyAchievementSharePresenter.create()
    }

    override fun portrait(): Boolean {
        return false
    }

    override fun getPageName(): String {
        return StatConstants.page_medal_detail
    }
    @SuppressLint("SetTextI18n")
    override fun initWidget() {
        super.initWidget()
        shadow.setOnClickListener { finish() }
        iv_close.setOnClickListener {
            postEvent(null, "click_medal_share_cancel")
            finish()
        }
        iv_share_wx.setOnClickListener {
            mViewModel?.share(this, SHARE_MEDIA.WEIXIN, achievement_share_view, this)
            postEvent(SHARE_MEDIA.WEIXIN)
        }
        iv_share_wx_circle.setOnClickListener {
            mViewModel?.share(this, SHARE_MEDIA.WEIXIN_CIRCLE, achievement_share_view, this)
            postEvent(SHARE_MEDIA.WEIXIN)
        }
        iv_save_bitmap.setOnClickListener {
            requestPermission()
        }
        shareAchievement?.let {
            val achievement = it.achievement
            iv_medal_icon.loadImage(achievement.gainMedalPicture, R.drawable.ic_popups_days, R.drawable.ic_popups_days)
            tv_medal_title.text = achievement.title
            tv_get_date.text = achievement.record?.formatDate()
            tv_medal_content_title.text = achievement.sentence
            web_view.loadDataWithBaseURL(null, formatHtml(achievement.context), "text/html", "utf-8", null)
            achievement_share_view.bindAchievement(it)
        }
    }

    @SuppressLint("LongLogTag")
    override fun onComplete(file: File) {
        showToast(getString(R.string.save_images_success))
        MediaScannerConnection.scanFile(this, arrayOf(file.absolutePath), null, null)
    }

    override fun onError(t: Throwable) {
        t.printStackTrace()
    }

    override fun onStart(shareMedia: SHARE_MEDIA?) {
        //showToast("正在分享中")
    }

    override fun onCancel(shareMedia: SHARE_MEDIA?) {
        showToast(getString(R.string.cancel_share))
    }

    override fun onResult(shareMedia: SHARE_MEDIA?) {
        showToast(getString(R.string.share_success))
    }

    override fun onError(shareMedia: SHARE_MEDIA?, throwable: Throwable?) {
        showToast(getString(R.string.share_failed))
    }

    fun Activity.showToast(text: String) {
        ToastHelper.show(this, text, Gravity.CENTER)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        showToast(getString(R.string.save_permission_tips))
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
    }

    @AfterPermissionGranted(WRITE_PERMISSION)
    private fun requestPermission() {
        val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (EasyPermissions.hasPermissions(this, *permissions)) {
            mViewModel?.saveShareView(achievement_share_view, this)
            postEvent(SHARE_MEDIA.MORE)
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.request_save_permissions_tips), WRITE_PERMISSION, *permissions)
        }
    }

    private fun postEvent(shareMedia: SHARE_MEDIA?, eventName: String = "click_medal_share") {
        val map = mutableMapOf<String, String>()
        if (shareMedia != null) {
            map["channel"] = shareDesc(shareMedia)
        }
        map["typeId"] = shareAchievement?.type.toString()
        StatUtil.event(eventName, map)
    }

    private fun shareDesc(shareMedia: SHARE_MEDIA): String {
        return when (shareMedia) {
            SHARE_MEDIA.WEIXIN -> "微信会话"
            SHARE_MEDIA.WEIXIN_CIRCLE -> "微信朋友圈"
            else -> "本地相册"
        }
    }
}