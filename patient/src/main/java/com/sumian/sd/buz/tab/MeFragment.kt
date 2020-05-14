package com.sumian.sd.buz.tab

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import cn.leancloud.chatkit.LCIMManager
import com.blankj.utilcode.util.ActivityUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.qmuiteam.qmui.util.QMUISpanHelper
import com.sumian.common.base.BaseViewModelFragment
import com.sumian.common.buz.kefu.KefuManager
import com.sumian.common.h5.WebViewManger
import com.sumian.common.image.loadImage
import com.sumian.common.statistic.StatUtil
import com.sumian.device.callback.DeviceStatusListener
import com.sumian.device.manager.DeviceManager
import com.sumian.device.test.ui.DeviceTestActivity
import com.sumian.sd.BuildConfig
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.account.achievement.MyAchievementActivity
import com.sumian.sd.buz.account.achievement.bean.AchievementRecord
import com.sumian.sd.buz.account.achievement.contract.GetAchievementListContract
import com.sumian.sd.buz.account.achievement.presenter.GetAchievementListPresenter
import com.sumian.sd.buz.account.bean.UserInfo
import com.sumian.sd.buz.account.userProfile.UserInfoActivity
import com.sumian.sd.buz.advisory.activity.AdvisoryListActivity
import com.sumian.sd.buz.cbti.activity.CBTIIntroduction2WebActivity
import com.sumian.sd.buz.coupon.activity.CouponCenterActivity
import com.sumian.sd.buz.device.devicemanage.DeviceManageActivity
import com.sumian.sd.buz.diaryevaluation.DiaryEvaluationListActivity
import com.sumian.sd.buz.notification.NotificationListActivity
import com.sumian.sd.buz.notification.NotificationViewModel
import com.sumian.sd.buz.onlinereport.OnlineReportListActivity
import com.sumian.sd.buz.scale.ScaleListActivity
import com.sumian.sd.buz.setting.SettingActivity
import com.sumian.sd.buz.stat.StatConstants
import com.sumian.sd.buz.tel.activity.TelBookingListActivity
import com.sumian.sd.buz.version.VersionManager
import com.sumian.sd.common.h5.SleepFileWebActivity
import com.sumian.sd.common.log.LogManager
import com.sumian.sd.main.H5Router
import com.sumian.sd.main.MainActivity
import com.sumian.sd.main.OnEnterListener
import com.sumian.sd.widget.tips.PatientRecordTips
import com.sumian.sd.widget.tips.PatientServiceTips
import com.sumian.sd.wxapi.MiniProgramHelper
import kotlinx.android.synthetic.main.fragment_tab_me.*

/**
 * Created by jzz
 * on 2018/1/16.
 * desc:
 */

class MeFragment : BaseViewModelFragment<GetAchievementListPresenter>(), View.OnClickListener, PatientServiceTips.OnServiceTipsCallback, PatientRecordTips.OnRecordTipsCallback, OnEnterListener, GetAchievementListContract.View {

    private var mPreRequestTimeMills = 0L

    private val mNotificationViewModel by lazy {
        ViewModelProviders.of(activity!!)
                .get(NotificationViewModel::class.java)
    }

    private var mPause = false
    private val mDeviceStatusListener = object : DeviceStatusListener {
        override fun onStatusChange(type: String) {
            if (type == DeviceManager.EVENT_RECEIVE_MONITOR_SN) {
                activity?.runOnUiThread {
                    if (!mPause) {
                        dv_device_manage.setContent(DeviceManager.getDevice()?.monitorSn
                                ?: getString(R.string.add_new_device))
                    }
                }

            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_tab_me
    }

    override fun onInitWidgetBefore() {
        super.onInitWidgetBefore()
        mViewModel = GetAchievementListPresenter(this)
    }

    override fun initWidget() {
        super.initWidget()
        iv_avatar.setOnClickListener(this)
        tv_nickname.setOnClickListener(this)
        dv_setting.setOnClickListener(this)
        iv_notification.setOnClickListener(this)
        siv_customer_service.setOnClickListener(this)
        dv_my_metal.setOnClickListener(this)
        dv_device_manage.setOnClickListener(this)
        dv_coupon_center.setOnClickListener(this)
        tips_service.setOnServiceTipsCallback(this)
        tips_record.setOnRecordTipsCallback(this)
        dv_exchange_center.setOnClickListener(this)
        dv_device_market.setOnClickListener { MiniProgramHelper.launchYouZanOrWeb(activity!!) }
        dv_test.isVisible = BuildConfig.DEBUG
        dv_test.setOnClickListener { ActivityUtils.startActivity(DeviceTestActivity::class.java) }
    }


    override fun initData() {
        super.initData()
        val userProfile = AppManager.getAccountViewModel().userInfo!!
        updateUserProfile(userProfile)
        AppManager.getAccountViewModel().getUserInfoLiveData().observe(this, Observer<UserInfo> { userInfo ->
            run {
                userInfo?.let {
                    updateUserProfile(it)
                }
            }
        })
        mNotificationViewModel
                .unreadCount
                .observe(this, Observer<Int> { updateNotificationIcon() })
        LCIMManager.getInstance().unreadCountLiveData.observe(this, Observer<Int> { updateNotificationIcon() })

        mViewModel?.getAchievementList()
        VersionManager.queryAppVersion()
        VersionManager.mAppUpgradeMode.observe(this, Observer {
            dv_setting.showRedDot(it == VersionManager.UPGRADE_MODE_FORCE)
        })
    }

    override fun onResume() {
        super.onResume()
        mPause = false
        val device = DeviceManager.getDevice()
        var monitorSn: String? = device?.monitorSn
        if (TextUtils.isEmpty(monitorSn)) {
            monitorSn = getString(R.string.add_new_device)
        }
        dv_device_manage.setContent(monitorSn!!)
        DeviceManager.registerDeviceStatusListener(mDeviceStatusListener)
//        LCIMManager.getInstance().updateUnreadConversation()
    }

    override fun onPause() {
        super.onPause()
        mPause = true
        DeviceManager.unregisterDeviceStatusListener(mDeviceStatusListener)
    }

    override fun showLoading() {
        //super.showLoading()
    }

    override fun dismissLoading() {
        //super.dismissLoading()
    }

    private fun updateNotificationIcon() {
        val notificationCount = mNotificationViewModel.unreadCount.value
        val hasNotification = notificationCount != null && notificationCount > 0
        val hasIm = LCIMManager.getInstance().unreadMessageCount > 0
        iv_notification.isActivated = hasNotification || hasIm
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_modify, R.id.iv_avatar, R.id.tv_nickname -> ActivityUtils.startActivity(UserInfoActivity::class.java)
            R.id.dv_setting -> ActivityUtils.startActivity(SettingActivity::class.java)
            R.id.iv_notification -> NotificationListActivity.launch(activity!!)
            R.id.siv_customer_service -> {
//                UIProvider.getInstance().clearCacheMsg()
                KefuManager.launchKefuActivity(activity!!)
            }
            R.id.dv_my_metal -> {
                StatUtil.event(StatConstants.click_me_page_my_medal_item)
                MyAchievementActivity.show()
            }
            R.id.dv_device_manage -> {

                LogManager.appendUserOperationLog("点击【设备管理】")
                ActivityUtils.startActivity(DeviceManageActivity::class.java)
            }
            R.id.dv_coupon_center -> CouponCenterActivity.show()
            R.id.dv_exchange_center -> {
                if (activity is H5Router) {
                    val act = activity as H5Router
                    val url = WebViewManger.getInstance().getBaseUrl() + "redemption-code-center"
                    val bundle = Bundle().apply {
                        putInt(MainActivity.KEY_TAB_INDEX, 0)
                        putString(MainActivity.KEY_H5_URL, url)
                    }
                    act.goto(bundle)
                }
            }
            else -> {
            }
        }
    }

    private fun updateUserProfile(userProfile: UserInfo) {
        iv_avatar.loadImage(userProfile.avatar, R.mipmap.ic_info_avatar_patient, R.mipmap.ic_info_avatar_patient)
        val nickname = userProfile.nickname
        tv_nickname.text = if (TextUtils.isEmpty(nickname)) userProfile.mobile else nickname
    }

    override fun showGraphicService() {
        AdvisoryListActivity.show()
    }

    override fun showTelService() {
        TelBookingListActivity.show()
    }

    override fun onDiaryEvaluationClick() {
        ActivityUtils.startActivity(DiaryEvaluationListActivity::class.java)
    }

    override fun showSleepRecord() {
        SleepFileWebActivity.show(context!!)
    }

    override fun showEvaluation() {
        StatUtil.event(StatConstants.click_me_page_my_scale_icon)
        ScaleListActivity.launch(1)
    }

    override fun showOnlineReport() {
        OnlineReportListActivity.launchForShowAll(this)
    }

    override fun onEnter(data: String?) {
        if (isCanRequest()) {
            mViewModel?.getAchievementList()
        }
    }

    override fun onGetAchievementListSuccess(achievementRecordList: List<AchievementRecord>) {
        invalidImageSpanAndTiming(achievementRecordList)
    }

    override fun onGetAchievementListFailed(error: String) {
    }

    /**
     * 超过5分钟再去请求刷新勋章列表
     * @return Boolean
     */
    private fun isCanRequest(): Boolean {
        return isVisible && (mPreRequestTimeMills == 0L || System.currentTimeMillis() - mPreRequestTimeMills >= 5 * 60 * 60 * 1000L)
    }

    private fun invalidImageSpanAndTiming(achievementRecordList: List<AchievementRecord>) {
        mPreRequestTimeMills = System.currentTimeMillis()
        var imageSpan: CharSequence = ""
        val tmpAchievementRecordList: List<AchievementRecord>? = if (achievementRecordList.size > 3) {
            achievementRecordList.subList(0, 3)
        } else {
            achievementRecordList
        }
        tmpAchievementRecordList?.forEach { achievementRecord ->
            val options = RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC)
            Glide.with(this).asDrawable().load(achievementRecord.achievement.gainMedalPicture).apply(options).listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    return true
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    //  bannerView.setImageBitmap(resource)
                    val text = QMUISpanHelper.generateSideIconText(false, resources.getDimensionPixelOffset(R.dimen.space_5), " ", resource)
                    imageSpan = TextUtils.concat(imageSpan, text)
                    dv_my_metal?.setContent(imageSpan)
                    return true
                }

            }).preload(resources.getDimensionPixelOffset(R.dimen.space_34), resources.getDimensionPixelOffset(R.dimen.space_34))
        }
    }
}
