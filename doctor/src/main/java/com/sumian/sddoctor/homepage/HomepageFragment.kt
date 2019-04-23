package com.sumian.sddoctor.homepage

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import cn.leancloud.chatkit.LCIMManager
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.sumian.common.statistic.StatUtil
import com.sumian.common.utils.SettingsUtil
import com.sumian.sddoctor.BuildConfig
import com.sumian.sddoctor.R
import com.sumian.sddoctor.account.activity.UserInfoActivity
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.base.BaseFragment
import com.sumian.sddoctor.booking.BookingManagementActivity
import com.sumian.sddoctor.constants.H5Uri
import com.sumian.sddoctor.constants.SPKeys
import com.sumian.sddoctor.constants.StatConstants
import com.sumian.sddoctor.event.NotificationUnreadCountChangeEvent
import com.sumian.sddoctor.homepage.widget.DoctorServicesView
import com.sumian.sddoctor.login.login.bean.DoctorInfo
import com.sumian.sddoctor.notification.NotificationListActivity
import com.sumian.sddoctor.notification.NotificationViewModel
import com.sumian.sddoctor.service.advisory.activity.AdvisoryListActivity
import com.sumian.sddoctor.service.cbti.activity.CBTIProgressActivity
import com.sumian.sddoctor.service.cbti.presenter.CBTILauncherPresenter
import com.sumian.sddoctor.service.evaluation.activity.WeekEvaluationListActivity
import com.sumian.sddoctor.util.EventBusUtil
import com.sumian.sddoctor.util.ImageLoader
import com.sumian.sddoctor.util.NotificationUtil
import com.sumian.sddoctor.widget.SumianAlertDialog
import com.umeng.socialize.bean.SHARE_MEDIA
import kotlinx.android.synthetic.main.fragment_homepage.*
import kotlinx.android.synthetic.main.layout_homepage_my_qr.*
import org.greenrobot.eventbus.Subscribe
import java.util.*

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/18 16:12
 *     desc   :
 *     version: 1.0
 *
 *     updated  by uni7corn
 *     on 2018/08/27
 *     desc:重构全新的医生服务入口
 * </pre>
 */
class HomepageFragment : BaseFragment(), DoctorServicesView.OnDoctorServicesCallback {

    private val mNotificationViewModel by lazy { ViewModelProviders.of(Objects.requireNonNull(activity!!)).get(NotificationViewModel::class.java) }

    companion object {
        const val REQUEST_CODE_OPEN_NOTIFICATION = 1
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_homepage
    }

    override fun initWidget(root: View?) {
        super.initWidget(root)
        StatUtil.event(StatConstants.enter_home_page)
        iv_avatar.setOnClickListener {
            ActivityUtils.startActivity(UserInfoActivity::class.java)
        }
        showOpenNotificationDialogIfNeeded()
        iv_notification.setOnClickListener { ActivityUtils.startActivity(NotificationListActivity::class.java) }
        doctor_services_view.setOnDoctorServicesCallback(this)
        iv_share_doctor_qr.setOnClickListener { showShareBottomSheet() }
        refresh_layout.setOnRefreshListener {
            AppManager.updateDoctorInfo()
        }
        homepage_fragment_v_cbti.setOnClickListener { CBTILauncherPresenter.launcherCBTI() }
    }

    override fun initData() {
        super.initData()
        AppManager.getAccountViewModel()
                .getDoctorInfo()
                .observe(this, Observer<DoctorInfo> { updateDoctorInfo(it) })
        mNotificationViewModel
                .unreadCountLiveData
                .observe(this, Observer<Int> { unreadCount -> updateNotificationIconUI() })
        LCIMManager.getInstance().unreadCountLiveData.observe(this, Observer<Int> { it -> updateNotificationIconUI() })
        AppManager.getAccountViewModel().getDoctorInfo().observe(this, Observer<DoctorInfo> { t ->
            ImageLoader.load(activity!!, t?.qr_code_raw, iv_doctor_qr)
            LogUtils.d("qr_code_raw", t?.qr_code_raw)
        })
    }

    private fun updateNotificationIconUI() {
        val notificationCount = mNotificationViewModel.unreadCountLiveData.value
        val hasNotification = notificationCount != null && notificationCount > 0
        val hasIm = LCIMManager.getInstance().unreadMessageCount > 0
        iv_notification.isActivated = hasNotification || hasIm
    }

    private fun updateDoctorInfo(doctorInfo: DoctorInfo?) {
        if (doctorInfo == null) {
            return
        }
        tv_name.text = doctorInfo.name
        tv_introduction.text = doctorInfo.getDescString(activity!!)
        tv_introduction.isVisible = doctorInfo.isAuthenticated()
        ImageLoader.load(context!!, doctorInfo.avatar, iv_avatar)
    }

    private fun showOpenNotificationDialogIfNeeded() {
        val previousShowTime = SPUtils.getInstance().getLong(SPKeys.SLEEP_RECORD_PREVIOUS_SHOW_NOTIFICATION_TIME, 0)
        val alreadyShowed = previousShowTime > 0
        if (NotificationUtil.areNotificationsEnabled(activity) || alreadyShowed) {
            return
        }
        SumianAlertDialog(activity)
                .setCloseIconVisible(true)
                .setTopIconResource(R.drawable.ic_notification_alert)
                .setTitle(R.string.open_notification)
                .setMessage(R.string.open_notification_and_receive_doctor_response)
                .setRightBtn(R.string.open_notification) { SettingsUtil.launchSettingActivityForResult(this, REQUEST_CODE_OPEN_NOTIFICATION) }
                .show()
        SPUtils.getInstance().put(SPKeys.SLEEP_RECORD_PREVIOUS_SHOW_NOTIFICATION_TIME, System.currentTimeMillis())
    }

    override fun openEventBus(): Boolean {
        return true
    }

    override fun onShowBookingService(v: View) {
        ActivityUtils.startActivity(BookingManagementActivity::class.java)
    }

    override fun onShowCBTIService(v: View) {
        CBTIProgressActivity.show()
    }

    override fun onShowAdvisoryService(v: View) {
        AdvisoryListActivity.show()
    }

    override fun onShowEvaluateService(v: View) {
        //ToastUtils.showShort(R.string.coming_soon)
        WeekEvaluationListActivity.show()
    }

    @Subscribe(sticky = true)
    fun onNotificationUnreadCountChange(notificationUnreadCountChangeEvent: NotificationUnreadCountChangeEvent) {
        mNotificationViewModel.updateUnreadCount()
        EventBusUtil.removeStickyEvent(notificationUnreadCountChangeEvent)
    }

    @SuppressLint("InflateParams")
    private fun showShareBottomSheet() {
        val available = isWeixinAvailable(activity!!)
        if (!available) {
            ToastUtils.showShort(getString(R.string.wechat_not_install))
            return
        }
        val bottomSheetDialog = BottomSheetDialog(activity!!)
        val contentView = LayoutInflater.from(activity!!).inflate(R.layout.layout_share_bottom_sheet, null, false)
        contentView.findViewById<View>(R.id.tv_wechat_friend).setOnClickListener {
            share(SHARE_MEDIA.WEIXIN)
            bottomSheetDialog.dismiss()
        }
        contentView.findViewById<View>(R.id.tv_wechat_circle).setOnClickListener {
            share(SHARE_MEDIA.WEIXIN_CIRCLE)
            bottomSheetDialog.dismiss()
        }
        contentView.findViewById<View>(R.id.tv_cancel).setOnClickListener { bottomSheetDialog.dismiss() }
        bottomSheetDialog.setContentView(contentView)
        bottomSheetDialog.show()
    }

    private fun share(shareMedia: SHARE_MEDIA) {
        val url = (BuildConfig.BASE_H5_URL.replace("sdd", "sd") + H5Uri.DOCTOR_SHARE).replace("{id}", AppManager.getAccountViewModel().getDoctorInfo().value?.id.toString())
        val title = getString(R.string.share_doctor_qr_title, AppManager.getAccountViewModel().getDoctorInfo().value?.name)
        val description = getString(R.string.share_doctor_qr_desc)
        AppManager.getOpenEngine().shareUrl(activity!!, url, title, description, AppManager.getAccountViewModel().getDoctorInfo().value?.avatar, shareMedia)
    }

    private fun isWeixinAvailable(context: Context): Boolean {
        val packageManager = context.packageManager
        val packageInfoList = packageManager.getInstalledPackages(0)
        packageInfoList.forEach {
            if (it.packageName.equals("com.tencent.mm")) {
                return true
            }
        }
        return false
    }
}