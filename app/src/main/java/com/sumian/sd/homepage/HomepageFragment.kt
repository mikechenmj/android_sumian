package com.sumian.sd.homepage

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.view.View
import androidx.lifecycle.Observer
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.sumian.common.image.ImageLoader
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.utils.JsonUtil
import com.sumian.common.utils.SumianExecutor
import com.sumian.sd.R
import com.sumian.sd.account.achievement.MyAchievementShareActivity
import com.sumian.sd.account.achievement.bean.LastAchievementData
import com.sumian.sd.account.achievement.contract.LastAchievementContract
import com.sumian.sd.account.achievement.presenter.LastAchievementPresenter
import com.sumian.sd.account.bean.Token
import com.sumian.sd.account.userProfile.UserInfoActivity
import com.sumian.sd.anxiousandfaith.AnxiousAndFaithActivity
import com.sumian.sd.app.AppManager
import com.sumian.sd.base.SdBaseFragment
import com.sumian.sd.device.DeviceManager
import com.sumian.sd.device.bean.BlueDevice
import com.sumian.sd.device.scan.ScanDeviceActivity
import com.sumian.sd.device.widget.DeviceCardView
import com.sumian.sd.event.CBTIProgressChangeEvent
import com.sumian.sd.event.CBTIServiceBoughtEvent
import com.sumian.sd.event.EventBusUtil
import com.sumian.sd.h5.H5Uri
import com.sumian.sd.h5.SimpleWebActivity
import com.sumian.sd.homepage.banner.BannerContract
import com.sumian.sd.homepage.banner.BannerPresenter
import com.sumian.sd.homepage.bean.GetCbtiChaptersResponse
import com.sumian.sd.homepage.bean.SentencePoolText
import com.sumian.sd.homepage.bean.SleepPrescriptionStatus
import com.sumian.sd.main.MainActivity
import com.sumian.sd.main.OnEnterListener
import com.sumian.sd.network.callback.BaseSdResponseCallback
import com.sumian.sd.relaxation.RelaxationListActivity
import com.sumian.sd.scale.ScaleListActivity
import com.sumian.sd.service.cbti.activity.CBTIIntroductionActivity
import com.sumian.sd.service.cbti.activity.CbtiFinalReportDialogActivity
import com.sumian.sd.sleepguide.SleepGuideActivity
import com.sumian.sd.widget.banner.Banner
import com.sumian.sd.widget.banner.BannerViewPager
import kotlinx.android.synthetic.main.fragment_homepage.*
import kotlinx.android.synthetic.main.layout_homepage_fragment_grid_items.*
import org.greenrobot.eventbus.Subscribe

@Suppress("unused")
/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/7/10 15:22
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class HomepageFragment : SdBaseFragment<HomepageContract.Presenter>(), HomepageContract.View, OnEnterListener, LastAchievementContract.View, BannerContract.View {

    override fun getLayoutId(): Int {
        return R.layout.fragment_homepage
    }

    companion object {
        const val SP_KEY_UPDATE_SLEEP_PRESCRIPTION_TIME = "update_sleep_prescription_time"
        const val REQUEST_CODE_SCAN_DEVICE = 1
        const val REQUEST_CODE_ENABLE_BLUETOOTH = 2
        const val SP_KEY_SHOW_SLEEP_GUIDE_DIALOG_APP_VERSION = "SP_KEY_SHOW_SLEEP_GUIDE_DIALOG_APP_VERSION"
    }

    private var isLock: Boolean = false

    var show = true
    var flag = 1
    override fun initWidget(root: View) {
        super.initWidget(root)
        initUserInfo()
        cbti_progress_view.setOnEnterLearnBtnClickListener(View.OnClickListener { CBTIIntroductionActivity.show() })
        tv_relaxation.setOnClickListener { ActivityUtils.startActivity(RelaxationListActivity::class.java) }
        tv_sleep_health.setOnClickListener { SimpleWebActivity.launch(activity!!, H5Uri.CBTI_SLEEP_HEALTH) }
        tv_scale.setOnClickListener { ScaleListActivity.launch() }
        sleep_prescription_view.setOnClickListener { SleepPrescriptionActivity.launch() }
        iv_avatar.setOnClickListener { onAvatarClick() }
        device_card_view.registerLifecycleOwner(this)
        DeviceManager.tryToConnectCacheMonitor()
        device_card_view.mHost = object : DeviceCardView.Host {
            override fun scanForDevice() {
                ScanDeviceActivity.startForResult(this@HomepageFragment, REQUEST_CODE_SCAN_DEVICE)
            }

            override fun enableBluetooth() {
                startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_CODE_ENABLE_BLUETOOTH)
            }
        }
        tv_anxious_and_faith.setOnClickListener { ActivityUtils.startActivity(AnxiousAndFaithActivity::class.java) }
        home_page_sleep_guide_enter_btn.setOnClickListener { SleepGuideActivity.start() }
    }

    private fun showSleepGuideDialogIfNeed() {
        val sp = SPUtils.getInstance(javaClass.simpleName)
        val showVersionCode = sp.getInt(SP_KEY_SHOW_SLEEP_GUIDE_DIALOG_APP_VERSION)
        val appVersionCode = AppUtils.getAppVersionCode()
        if (showVersionCode == appVersionCode) {
            return
        }
        if ((activity as MainActivity?)?.mCurrentPosition != MainActivity.TAB_0) return
        val arr = intArrayOf(0, 0)
        home_page_sleep_guide_enter_btn?.getLocationInWindow(arr) ?: return
        sp.put(SP_KEY_SHOW_SLEEP_GUIDE_DIALOG_APP_VERSION, appVersionCode)
        SleepGuideDialogActivity.start(arr[1])
    }

    private fun initUserInfo() {
        AppManager.getAccountViewModel().liveDataToken.observe(this, Observer<Token> { t ->
            val userProfile = t?.user ?: return@Observer
            tv_name.text = userProfile.nameOrNickname
            val defaultAvatar = R.mipmap.ic_info_avatar_patient
            ImageLoader.loadImage(userProfile.avatar, iv_avatar, defaultAvatar)
        })
    }

    override fun onStart() {
        super.onStart()
        refreshData()
    }

    override fun onResume() {
        super.onResume()
        device_card_view.onResume()
        //LastAchievementPresenter.init(this).getLastAchievement()
    }

    override fun onPause() {
        super.onPause()
        device_card_view.onPause()
        cbti_banner_view_pager.pauseLoop()
    }

    override fun onGetAchievementListForTypeSuccess(lastAchievementData: LastAchievementData) {
        MyAchievementShareActivity.showFromLastAchievement(lastAchievementData)
    }

    override fun onGetBannerListSuccess(banners: List<Banner>) {
        if (banners.isNullOrEmpty()) {
            iv_default_banner.visibility = View.VISIBLE
            cbti_banner_view_pager.hide()
        } else {
            cbti_banner_view_pager.bindBannerList(banners)
            cbti_banner_view_pager.setBannerClickListener(object : BannerViewPager.OnBannerClickListener {
                override fun onClick(banner: View, position: Int) {
                    CBTIIntroductionActivity.show()
                }
            })
            cbti_banner_view_pager.show()
            iv_default_banner.visibility = View.GONE
        }
    }

    override fun onGetBannerListFailed(error: String) {
    }

    private fun refreshData() {
        queryCbti()
        querySleepPrescription()
        querySentencePool()
    }

    private fun querySentencePool() {
        val call = AppManager.getSdHttpService().getSentencePool()
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<SentencePoolText>() {
            override fun onSuccess(response: SentencePoolText?) {
                tv_home_random_text.text = response?.homeSentence
                        ?: getString(R.string.homepage_sleep_slogan)
            }

            override fun onFailure(errorResponse: ErrorResponse) {
            }

        })
    }

    private fun querySleepPrescription() {
        SPUtils.getInstance().put(SP_KEY_UPDATE_SLEEP_PRESCRIPTION_TIME, System.currentTimeMillis())
        val call2 = AppManager.getSdHttpService().getSleepPrescriptionStatus()
        addCall(call2)
        call2.enqueue(object : BaseSdResponseCallback<SleepPrescriptionStatus?>() {
            override fun onFailure(errorResponse: ErrorResponse) {

            }

            override fun onSuccess(response: SleepPrescriptionStatus?) {
                if (response == null) {
                    return
                }
                sleep_prescription_view.setPrescriptionData(response)
            }
        })
    }

    private fun queryCbti() {
        CbtiFinalReportDialogActivity.showFinalReportDialogIfNeed()
        val call = AppManager.getSdHttpService().getCbtiChapters(null)
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<GetCbtiChaptersResponse>() {
            override fun onFailure(errorResponse: ErrorResponse) {
            }

            override fun onSuccess(response: GetCbtiChaptersResponse?) {
                isLock = response?.meta?.isLock != false
                cbti_progress_view.setData(response)
                if (isLock) {
                    iv_default_banner.setOnClickListener {
                        CBTIIntroductionActivity.show()
                    }
                    cbti_progress_view.visibility = View.GONE
                    cbti_banner_view_pager.visibility = View.INVISIBLE
                    BannerPresenter.init(this@HomepageFragment).getBannerList()
                } else {
                    iv_default_banner.visibility = View.GONE
                    cbti_progress_view.visibility = View.VISIBLE
                    cbti_banner_view_pager.hide()
                }
                querySleepGuide()
            }
        })
    }

    private fun querySleepGuide() {
        val call = AppManager.getSdHttpService().getSleepGuide()
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<Any?>() {
            override fun onSuccess(response: Any?) {
                LogUtils.d("getSleepGuide", response)
                home_page_sleep_guide_enter_btn.isActivated = response != null
                if (response == null) {
                    showSleepGuideDialogIfNeed()
                }
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                LogUtils.d("getSleepGuide", errorResponse)
            }
        })
    }

    override fun openEventBus(): Boolean {
        return true
    }

    @Subscribe(sticky = true)
    fun onCBTIBoughtEvent(event: CBTIServiceBoughtEvent) {
        EventBusUtil.removeStickyEvent(event)
        queryCbti()
    }

    @Subscribe(sticky = true)
    fun onCBTIProgressChangeEvent(event: CBTIProgressChangeEvent) {
        EventBusUtil.removeStickyEvent(event)
        queryCbti()
    }

    private fun onAvatarClick() {
        UserInfoActivity.show(context, UserInfoActivity::class.java)
    }

    override fun onEnter(data: String?) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_SCAN_DEVICE) {
            if (resultCode == Activity.RESULT_OK) {
                val deviceJson = data?.getStringExtra(ScanDeviceActivity.DATA)
                val blueDevice = JsonUtil.fromJson(deviceJson, BlueDevice::class.java) ?: return
                DeviceManager.scanAndConnect(blueDevice)
            }
        }
    }
}