package com.sumian.sd.tab

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.FragmentActivity
import android.text.TextUtils
import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.hyphenate.helpdesk.easeui.UIProvider
import com.sumian.common.image.ImageLoader
import com.sumian.hw.leancloud.HwLeanCloudHelper
import com.sumian.hw.log.LogManager
import com.sumian.hw.upgrade.model.VersionModel
import com.sumian.sd.R
import com.sumian.sd.account.bean.Token
import com.sumian.sd.account.bean.UserInfo
import com.sumian.sd.account.userProfile.SdUserProfileActivity
import com.sumian.sd.app.AppManager
import com.sumian.sd.base.SdBaseFragment
import com.sumian.sd.base.SdBasePresenter
import com.sumian.sd.device.DeviceManageActivity
import com.sumian.sd.device.DeviceManager
import com.sumian.sd.h5.SleepFileWebActivity
import com.sumian.sd.kefu.KefuManager
import com.sumian.sd.main.OnEnterListener
import com.sumian.sd.notification.NotificationListActivity
import com.sumian.sd.notification.NotificationViewModel
import com.sumian.sd.onlinereport.OnlineReportListActivity
import com.sumian.sd.scale.ScaleListActivity
import com.sumian.sd.service.advisory.activity.AdvisoryListActivity
import com.sumian.sd.service.coupon.activity.CouponCenterActivity
import com.sumian.sd.service.diary.DiaryEvaluationListActivity
import com.sumian.sd.service.tel.activity.TelBookingListActivity
import com.sumian.sd.setting.SettingActivity
import com.sumian.sd.widget.tips.PatientRecordTips
import com.sumian.sd.widget.tips.PatientServiceTips
import kotlinx.android.synthetic.main.fragment_tab_me.*
import java.util.*

/**
 * Created by jzz
 * on 2018/1/16.
 * desc:
 */

class MeFragment : SdBaseFragment<SdBasePresenter<*>>(), View.OnClickListener, PatientServiceTips.OnServiceTipsCallback, PatientRecordTips.OnRecordTipsCallback,
        HwLeanCloudHelper.OnShowMsgDotCallback, OnEnterListener, VersionModel.ShowDotCallback {

    //@BindView(R.id.siv_customer_service)
    //ImageView mSivKefu;

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
        dv_device_manage.setOnClickListener(this)
        dv_coupon_center.setOnClickListener(this)
        tips_service.setOnServiceTipsCallback(this)
        tips_record.setOnRecordTipsCallback(this)
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
        ViewModelProviders.of(Objects.requireNonNull<FragmentActivity>(activity))
                .get(NotificationViewModel::class.java)
                .unreadCount
                .observe(this, Observer<Int> { unreadCount -> iv_notification.isActivated = unreadCount != null && unreadCount > 0 })

        // HwLeanCloudHelper.addOnAdminMsgCallback(this);
        DeviceManager.getMonitorLiveData().observe(this, Observer { blueDevice ->
            var monitorSn: String? = blueDevice?.sn
            if (TextUtils.isEmpty(monitorSn)) {
                monitorSn = getString(R.string.add_new_device)
            }
            dv_device_manage.setContent(monitorSn!!)
        })

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_avatar, R.id.tv_nickname -> SdUserProfileActivity.show(context, SdUserProfileActivity::class.java)
            R.id.dv_setting -> SettingActivity.show(context, SettingActivity::class.java)
            R.id.iv_notification -> NotificationListActivity.launch(activity)
            R.id.siv_customer_service -> {
                UIProvider.getInstance().clearCacheMsg()
                KefuManager.launchKefuActivity()
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
        ImageLoader.loadImage(userProfile.avatar, iv_avatar, R.mipmap.ic_info_avatar_patient)
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

    override fun onShowMsgDotCallback(adminMsgLen: Int, doctorMsgLen: Int, customerMsgLen: Int) {
        // onHideMsgCallback(adminMsgLen, doctorMsgLen, customerMsgLen);
    }

    override fun onHideMsgCallback(adminMsgLen: Int, doctorMsgLen: Int, customerMsgLen: Int) {
        // runOnUiThread(() -> mSivKefu.setImageResource((customerMsgLen > 0) ? R.drawable.ic_info_customerservice_reply : R.drawable.ic_info_customerservice));
    }

    override fun onEnter(data: String?) {
        // if (mSivKefu != null) {
        //    runOnUiThread(() -> mSivKefu.setImageResource((HwLeanCloudHelper.isHaveCustomerMsg()) ? R.drawable.ic_info_customerservice_reply : R.drawable.ic_info_customerservice));
        // }
    }

    override fun showDot(isShowAppDot: Boolean, isShowMonitorDot: Boolean, isShowSleepyDot: Boolean) {

    }
}
