package com.sumian.sd.homepage

import android.arch.lifecycle.Observer
import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.SPUtils
import com.sumian.common.image.ImageLoader
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sd.R
import com.sumian.sd.account.bean.Token
import com.sumian.sd.app.AppManager
import com.sumian.sd.base.SdBaseFragment
import com.sumian.sd.event.CBTIProgressChangeEvent
import com.sumian.sd.event.CBTIServiceBoughtEvent
import com.sumian.sd.event.EventBusUtil
import com.sumian.sd.event.SleepRecordFilledEvent
import com.sumian.sd.h5.H5Uri
import com.sumian.sd.h5.SimpleWebActivity
import com.sumian.sd.homepage.bean.GetCbtiChaptersResponse
import com.sumian.sd.homepage.bean.SleepPrescription
import com.sumian.sd.homepage.bean.SleepPrescriptionStatus
import com.sumian.sd.main.OnEnterListener
import com.sumian.sd.network.callback.BaseSdResponseCallback
import com.sumian.sd.scale.ScaleListActivity
import com.sumian.sd.service.cbti.activity.CBTIIntroductionWebActivity
import kotlinx.android.synthetic.main.fragment_homepage.*
import org.greenrobot.eventbus.Subscribe

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/7/10 15:22
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class HomepageFragment : SdBaseFragment<HomepageContract.Presenter>(), HomepageContract.View, OnEnterListener {
    override fun getLayoutId(): Int {
        return R.layout.fragment_homepage
    }

    companion object {
        const val SP_KEY_UPDATE_SLEEP_PRESCRIPTION_TIME = "update_sleep_prescription_time"
    }

    override fun initWidget(root: View) {
        super.initWidget(root)
        initUserInfo()
        cbti_progress_view.setOnEnterLearnBtnClickListener(View.OnClickListener { launchCbtiActivity() })
        tv_relaxation.setOnClickListener { ActivityUtils.startActivity(RelaxationActivity::class.java) }
        tv_sleep_health.setOnClickListener { SimpleWebActivity.launch(activity, H5Uri.CBTI_SLEEP_HEALTH) }
        tv_scale.setOnClickListener { ScaleListActivity.launch(context, ScaleListActivity.TYPE_ALL) }
        sleep_prescription_view.setOnClickListener { SleepPrescriptionSettingActivity.launch() }
        iv_avatar.setOnClickListener { onAvatarClick() }
    }

    private fun launchCbtiActivity() {
        ActivityUtils.startActivity(CBTIIntroductionWebActivity::class.java)
    }

    private fun initUserInfo() {
        AppManager.getAccountViewModel().liveDataToken.observe(this, object : Observer<Token> {
            override fun onChanged(t: Token?) {
                val userProfile = t?.user ?: return
                tv_name.text = userProfile.nameOrNickname
                val defaultAvatar = R.mipmap.ic_info_avatar_patient
                ImageLoader.loadImage(userProfile.avatar, iv_avatar, defaultAvatar)
            }
        })
    }

    override fun onStart() {
        super.onStart()
        refreshData()
    }

    private fun refreshData() {
        queryCbti()
        querySleepRecord()
        queryDailyReport()
        querySleepPrescription()
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
                sleep_prescription_view.setHasNewPrescription(response.meta.update)
                sleep_prescription_view.setPrescriptionData(response.meta.prescription.data)
            }
        })
    }

    private fun querySleepRecord() {
        sleep_data_pager_view.querySleepRecord()
    }

    private fun queryCbti() {
        val call = AppManager.getSdHttpService().getCbtiChapters(null)
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<GetCbtiChaptersResponse>() {
            override fun onFailure(errorResponse: ErrorResponse) {

            }

            override fun onSuccess(response: GetCbtiChaptersResponse?) {
                val isLock = response?.meta?.isLock != false
                if (!isLock) {
                    cbti_progress_view.setData(response)
                } else {
                    cbti_progress_view.setData(null)
                }
            }
        })
    }

    private fun queryDailyReport() {
        sleep_data_pager_view.queryDailyReport()
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

    @Subscribe(sticky = true)
    fun onSleepRecordFilledEvent(event: SleepRecordFilledEvent) {
        EventBusUtil.removeStickyEvent(event)
        querySleepRecord()
        querySleepPrescription()
    }

    private fun onAvatarClick() {
    }

    override fun onEnter(data: String?) {
        initData()
    }
}