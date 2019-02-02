package com.sumian.sd.buz.tab

import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.image.loadImage
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.base.SdBaseFragment
import com.sumian.sd.base.SdBasePresenter
import com.sumian.sd.buz.account.achievement.MyAchievementActivity
import com.sumian.sd.buz.account.bean.Token
import com.sumian.sd.buz.account.bean.UserInfo
import com.sumian.sd.buz.account.userProfile.UserInfoActivity
import com.sumian.sd.buz.advisory.activity.AdvisoryListActivity
import com.sumian.sd.buz.coupon.activity.CouponCenterActivity
import com.sumian.sd.buz.device.DeviceManageActivity
import com.sumian.sd.buz.device.DeviceManager
import com.sumian.sd.buz.diaryevaluation.DiaryEvaluationListActivity
import com.sumian.sd.buz.kefu.KefuManager
import com.sumian.sd.buz.notification.NotificationListActivity
import com.sumian.sd.buz.notification.NotificationViewModel
import com.sumian.sd.buz.onlinereport.OnlineReportListActivity
import com.sumian.sd.buz.scale.ScaleListActivity
import com.sumian.sd.buz.setting.SettingActivity
import com.sumian.sd.buz.tel.activity.TelBookingListActivity
import com.sumian.sd.buz.upgrade.model.VersionModel
import com.sumian.sd.common.h5.SleepFileWebActivity
import com.sumian.sd.common.log.LogManager
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

class MeFragment : SdBaseFragment<SdBasePresenter<*>>(), View.OnClickListener,
        PatientServiceTips.OnServiceTipsCallback, PatientRecordTips.OnRecordTipsCallback, OnEnterListener, VersionModel.ShowDotCallback {

    override fun getLayoutId(): Int {
        return R.layout.fragment_tab_me
    }

    override fun initWidget(root: View) {
        super.initWidget(root)
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
        dv_device_market.setOnClickListener { MiniProgramHelper.launchYouZanOrWeb(activity!!) }
    }

    override fun initData() {
        super.initData()
        val userProfile = AppManager.getAccountViewModel().token.user
        updateUserProfile(userProfile)
        AppManager.getAccountViewModel().liveDataToken.observe(this, Observer<Token> { token ->
            run {
                token?.let {
                    updateUserProfile(it.user)
                }
            }
        })
        ViewModelProviders.of(activity!!)
                .get(NotificationViewModel::class.java)
                .unreadCount
                .observe(this, Observer<Int> { unreadCount -> iv_notification.isActivated = unreadCount != null && unreadCount > 0 })

        DeviceManager.getMonitorLiveData().observe(this, Observer { blueDevice ->
            var monitorSn: String? = blueDevice?.sn
            if (TextUtils.isEmpty(monitorSn)) {
                monitorSn = getString(R.string.add_new_device)
            }
            dv_device_manage.setContent(monitorSn!!)
        })
        DeviceManager.mMonitorNeedUpdateLiveData.observe(this, Observer { dv_setting.showRedDot(DeviceManager.hasFirmwareNeedUpdate()) })
        DeviceManager.mSleeperNeedUpdateLiveData.observe(this, Observer { dv_setting.showRedDot(DeviceManager.hasFirmwareNeedUpdate()) })
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_modify, R.id.iv_avatar, R.id.tv_nickname -> ActivityUtils.startActivity(UserInfoActivity::class.java)
            R.id.dv_setting -> ActivityUtils.startActivity(SettingActivity::class.java)
            R.id.iv_notification -> NotificationListActivity.launch(activity)
            R.id.siv_customer_service -> {
//                UIProvider.getInstance().clearCacheMsg()
                KefuManager.launchKefuActivity()
            }
            R.id.dv_my_metal -> {
                MyAchievementActivity.show()
            }
            R.id.dv_device_manage -> {
                LogManager.appendUserOperationLog("点击【设备管理】")
                ActivityUtils.startActivity(DeviceManageActivity::class.java)
            }
            R.id.dv_coupon_center -> CouponCenterActivity.show()
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
        ScaleListActivity.launch(1)
    }

    override fun showOnlineReport() {
        OnlineReportListActivity.launchForShowAll(this)
    }

    override fun onEnter(data: String?) {
    }

    override fun showDot(isShowAppDot: Boolean, isShowMonitorDot: Boolean, isShowSleepyDot: Boolean) {

    }
}
