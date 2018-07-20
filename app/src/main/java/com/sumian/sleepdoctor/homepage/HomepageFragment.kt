package com.sumian.sleepdoctor.homepage

import android.arch.lifecycle.Observer
import android.text.format.DateUtils
import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.utils.ImageLoader
import com.sumian.sleepdoctor.R
import com.sumian.sleepdoctor.account.bean.Token
import com.sumian.sleepdoctor.app.AppManager
import com.sumian.sleepdoctor.base.BaseFragment
import com.sumian.sleepdoctor.cbti.activity.CBTIIntroductionWebActivity
import com.sumian.sleepdoctor.event.*
import com.sumian.sleepdoctor.homepage.bean.GetCbtiChaptersResponse
import com.sumian.sleepdoctor.homepage.bean.SleepPrescription
import com.sumian.sleepdoctor.homepage.bean.SleepPrescriptionWrapper
import com.sumian.sleepdoctor.network.callback.BaseResponseCallback
import com.sumian.sleepdoctor.network.response.ErrorResponse
import com.sumian.sleepdoctor.record.FillSleepRecordActivity
import com.sumian.sleepdoctor.record.SleepRecordActivity
import com.sumian.sleepdoctor.record.bean.SleepRecord
import com.sumian.sleepdoctor.scale.ScaleListActivity
import com.sumian.sleepdoctor.widget.dialog.SumianAlertDialog
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
class HomepageFragment : BaseFragment<HomepageContract.Presenter>(), HomepageContract.View {
    private var mSleepPrescriptionWrapper: SleepPrescriptionWrapper? = null
    private var mHttpService = AppManager.getHttpService()!!

    override fun getLayoutId(): Int {
        return R.layout.fragment_homepage
    }

    companion object {
        const val REQUEST_CODE_FILL_SLEEP_RECORD = 1
        const val SP_KEY_UPDATE_SLEEP_PRESCRIPTION_TIME = "update_sleep_prescription_time"
    }

    override fun initWidget(root: View) {
        super.initWidget(root)
        initUserInfo()
        sleep_record_view.setOnClickRightArrowListener { SleepRecordActivity.launch(activity!!) }
        sleep_record_view.setOnClickFillSleepRecordBtnListener { FillSleepRecordActivity.launchForResult(this, System.currentTimeMillis(), REQUEST_CODE_FILL_SLEEP_RECORD) }
        cbti_progress_view.setOnEnterLearnBtnClickListener(View.OnClickListener { launchCbtiActivity() })
        doctor_service_item_view_cbti.setOnClickListener { launchCbtiActivity() }
        tv_sleep_health.setOnClickListener { ToastUtils.showShort(R.string.not_open_yet_please_wait) }
        tv_scale.setOnClickListener { ScaleListActivity.launch(context, ScaleListActivity.TYPE_ALL) }
        sleep_prescription_view.setOnClickListener { onSleepPrescriptionClick() }
        iv_avatar.setOnClickListener { onAvatarClick() }
    }

    private fun launchCbtiActivity() {
        ActivityUtils.startActivity(CBTIIntroductionWebActivity::class.java)
    }

    private fun initUserInfo() {
        AppManager.getAccountViewModel().liveDataToken.observe(this, object : Observer<Token> {
            override fun onChanged(t: Token?) {
                val userProfile = t?.user ?: return
                tv_name.text = userProfile.nickname
                val defaultAvatar = R.mipmap.ic_info_avatar_patient
                ImageLoader.loadImage(activity, iv_avatar, userProfile.avatar, defaultAvatar)
            }
        })
    }

    override fun initData() {
        super.initData()
        queryCbti()
        querySleepRecord()
        querySleepPrescription()
    }

    override fun onStart() {
        super.onStart()
        updateSleepPrescriptionIfNeed()
    }

    private fun querySleepPrescription() {
        val call = AppManager.getHttpService().getSleepPrescriptions()
        addCall(call)
        call.enqueue(object : BaseResponseCallback<SleepPrescriptionWrapper>() {
            override fun onSuccess(response: SleepPrescriptionWrapper?) {
                updateSleepPrescription(response)
                if (response != null) {
                    showSleepPrescriptionDialogIfNeed(response)
                }
            }

            override fun onFailure(errorResponse: ErrorResponse) {

            }
        })

    }

    private fun updateSleepPrescription(response: SleepPrescriptionWrapper?) {
        SPUtils.getInstance().getLong(SP_KEY_UPDATE_SLEEP_PRESCRIPTION_TIME, System.currentTimeMillis())
        sleep_prescription_view.setPrescriptionData(response)
        mSleepPrescriptionWrapper = response
    }

    /**
     * 当距离上次请求超过3小时，再次回到该页面时，刷新SleepPrescription数据
     */
    private fun updateSleepPrescriptionIfNeed() {
        val lastUpdateTime = SPUtils.getInstance().getLong(SP_KEY_UPDATE_SLEEP_PRESCRIPTION_TIME, 0)
        if (System.currentTimeMillis() - lastUpdateTime > DateUtils.MINUTE_IN_MILLIS * 5) {
            querySleepPrescription()
        }
    }

    private fun showSleepPrescriptionDialogIfNeed(response: SleepPrescriptionWrapper) {
//        response.showEnquireDialog = true //test code
        if (response.showUpdateDialog) {
            showSleepUpdatedDialog()
        } else if (response.showEnquireDialog) {
            SumianAlertDialog(activity)
                    .setTitle(R.string.last_week_tired_enquire)
                    .setMessage(R.string.last_week_tired_enquire)
                    .setLeftBtn(R.string.no, null)
                    .whitenLeft()
                    .setRightBtn(R.string.yes) {
                        updateSleepPrescriptionWhenTired(response.sleepPrescription!!)
                    }
                    .show()
        }
    }

    private fun showSleepUpdatedDialog() {
        SumianAlertDialog(activity)
                .setMessage(R.string.update_sleep_prescription_hint)
                .setLeftBtn(R.string.i_got_it, null)
                .show()
    }

    private fun updateSleepPrescriptionWhenTired(sleepPrescription: SleepPrescription) {
        val call = mHttpService.updateSleepPrescriptionsWhenFatigue(sleepPrescription)
        addCall(call)
        call.enqueue(object : BaseResponseCallback<SleepPrescriptionWrapper>() {
            override fun onSuccess(response: SleepPrescriptionWrapper?) {
                sleep_prescription_view.setPrescriptionData(response)
                showSleepUpdatedDialog()
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
            }
        })
    }

    private fun querySleepRecord() {
        val call = AppManager.getHttpService().getSleepDiaryDetail((System.currentTimeMillis() / 1000L).toInt())
        addCall(call)
        call
                .enqueue(object : BaseResponseCallback<SleepRecord>() {
                    override fun onSuccess(response: SleepRecord?) {
                        sleep_record_view.setSleepRecord(response)
                    }

                    override fun onFailure(errorResponse: ErrorResponse) {
                        sleep_record_view.setSleepRecord(null)
                    }

                    override fun onFinish() {
                        super.onFinish()
                    }
                })
    }

    private fun queryCbti() {
        val call = AppManager.getHttpService().getCbtiChapters(null)
        addCall(call)
        call.enqueue(object : BaseResponseCallback<GetCbtiChaptersResponse>() {
            override fun onSuccess(response: GetCbtiChaptersResponse?) {
                val isLock = response?.meta?.isLock != false
                doctor_service_item_view_cbti.visibility = if (isLock) View.VISIBLE else View.GONE
                cbti_progress_view.visibility = if (!isLock) View.VISIBLE else View.GONE
                if (!isLock) {
                    cbti_progress_view.initData(response)
                }
            }

            override fun onFailure(errorResponse: ErrorResponse) {

            }

        })
    }

    private fun onSleepPrescriptionClick() {
        if (mSleepPrescriptionWrapper == null || mSleepPrescriptionWrapper?.isServiceStopped!!) {
            showStopPrescriptionDialog()
        } else {
            SleepPrescriptionSettingActivity.launch(mSleepPrescriptionWrapper!!)
        }
    }

    private fun showStopPrescriptionDialog() {
        SumianAlertDialog(activity)
                .setTitle(R.string.sleep_data_no_enough)
                .setMessage(R.string.sleep_data_no_enough_hint)
                .setLeftBtn(R.string.confirm, null)
                .show()
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
//        SumianImageTextDialog(activity!!)
//                .setType(SumianImageTextDialog.TYPE_TEXT)
//                .setText("dialog")
//                .show()
//        ToastUtils.showShort("toast")
    }
}