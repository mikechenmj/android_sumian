package com.sumian.sd.homepage

import android.arch.lifecycle.Observer
import android.text.format.DateUtils
import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.image.ImageLoader
import com.sumian.sd.R
import com.sumian.sd.account.bean.Token
import com.sumian.sd.app.AppManager
import com.sumian.sd.base.SdBaseFragment
import com.sumian.sd.event.*
import com.sumian.sd.h5.H5Uri
import com.sumian.sd.h5.SimpleWebActivity
import com.sumian.sd.homepage.bean.GetCbtiChaptersResponse
import com.sumian.sd.homepage.bean.SleepPrescription
import com.sumian.sd.homepage.bean.SleepPrescriptionWrapper
import com.sumian.sd.homepage.bean.UpdateSleepPrescriptionWhenFatiguedData
import com.sumian.sd.main.OnEnterListener
import com.sumian.sd.network.callback.BaseResponseCallback
import com.sumian.sd.scale.ScaleListActivity
import com.sumian.sd.service.cbti.activity.CBTIIntroductionWebActivity
import com.sumian.sd.widget.dialog.SumianAlertDialog
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
    private var mSleepPrescriptionWrapper: SleepPrescriptionWrapper? = null

    override fun getLayoutId(): Int {
        return R.layout.fragment_homepage
    }

    companion object {
        const val REQUEST_CODE_FILL_SLEEP_RECORD = 1
        const val SP_KEY_UPDATE_SLEEP_PRESCRIPTION_TIME = "update_sleep_prescription_time"
        const val REFRESH_DATA_INTERVAL = DateUtils.MINUTE_IN_MILLIS * 1
    }

    override fun initWidget(root: View) {
        super.initWidget(root)
        initUserInfo()
        cbti_progress_view.setOnEnterLearnBtnClickListener(View.OnClickListener { launchCbtiActivity() })
        dsiv_cbti.setOnClickListener { launchCbtiActivity() }
        dsiv_relaxation.setOnClickListener { ActivityUtils.startActivity(RelaxationActivity::class.java) }
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

    fun refreshData() {
        queryCbti()
        querySleepRecord()
        queryDailyReport()
        querySleepPrescription()
    }

    private fun querySleepPrescription() {
        SPUtils.getInstance().put(SP_KEY_UPDATE_SLEEP_PRESCRIPTION_TIME, System.currentTimeMillis())
        val call = AppManager.getHttpService().getSleepPrescriptions()
        addCall(call)
        call.enqueue(object : BaseResponseCallback<SleepPrescriptionWrapper>() {
            override fun onSuccess(response: SleepPrescriptionWrapper?) {
                updateSleepPrescription(response)
            }

            override fun onFailure(code: Int, message: String) {

            }
        })

    }

    private fun updateSleepPrescription(response: SleepPrescriptionWrapper?) {
        sleep_prescription_view.setPrescriptionData(response)
        mSleepPrescriptionWrapper = response
    }

    private fun querySleepRecord() {
        sleep_data_pager_view.querySleepRecord()
    }

    private fun queryCbti() {
        val call = AppManager.getHttpService().getCbtiChapters(null)
        addCall(call)
        call.enqueue(object : BaseResponseCallback<GetCbtiChaptersResponse>() {
            override fun onSuccess(response: GetCbtiChaptersResponse?) {
                val isLock = response?.meta?.isLock != false
                dsiv_cbti.visibility = if (isLock) View.VISIBLE else View.GONE
                cbti_progress_view.visibility = if (!isLock) View.VISIBLE else View.GONE
                if (!isLock) {
                    cbti_progress_view.initData(response)
                }
            }

            override fun onFailure(code: Int, message: String) {

            }

        })
    }

    fun queryDailyReport() {
        sleep_data_pager_view.queryDailyReport()
    }

    override fun openEventBus(): Boolean {
        return true
    }

    @Subscribe(sticky = true)
    fun onCBTIBoughtEvent(event: CBTIServiceBoughtEvent) {
        EventBusUtil.removeStickyEvent(event)
        dsiv_cbti.visibility = View.GONE
        cbti_progress_view.visibility = View.VISIBLE
        queryCbti()
    }

    @Subscribe(sticky = true)
    fun onCBTIProgressChangeEvent(event: CBTIProgressChangeEvent) {
        EventBusUtil.removeStickyEvent(event)
        queryCbti()
    }

    @Subscribe(sticky = true)
    fun onSleepSubscriptionUpdatedEvent(event: SleepPrescriptionUpdatedEvent) {
        EventBusUtil.removeStickyEvent(event)
        updateSleepPrescription(event.sleepPrescriptionWrapper)
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