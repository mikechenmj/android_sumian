package com.sumian.sleepdoctor.homepage

import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.utils.ImageLoader
import com.sumian.sleepdoctor.R
import com.sumian.sleepdoctor.R.id.*
import com.sumian.sleepdoctor.account.bean.UserProfile
import com.sumian.sleepdoctor.app.AppManager
import com.sumian.sleepdoctor.base.ActivityLauncher
import com.sumian.sleepdoctor.base.BaseFragment
import com.sumian.sleepdoctor.homepage.bean.GetCbtiChaptersResponse
import com.sumian.sleepdoctor.network.callback.BaseResponseCallback
import com.sumian.sleepdoctor.network.response.ErrorResponse
import com.sumian.sleepdoctor.record.FillSleepRecordActivity
import com.sumian.sleepdoctor.record.SleepRecordDetailActivity
import com.sumian.sleepdoctor.record.bean.SleepRecord
import com.sumian.sleepdoctor.scale.ScaleListActivity
import kotlinx.android.synthetic.main.fragment_homepage.*

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/7/10 15:22
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class HomepageFragment : BaseFragment<HomepageContract.Presenter>(), HomepageContract.View, ActivityLauncher {
    override fun getLayoutId(): Int {
        return R.layout.fragment_homepage
    }

    companion object {
        const val REQUEST_CODE_FILL_SLEEP_RECORD = 1
    }

    override fun initWidget(root: View) {
        super.initWidget(root)
        initUserInfo()
        sleep_record_view.setOnClickRightArrowListener { ActivityUtils.startActivity(SleepRecordDetailActivity::class.java) }
        sleep_record_view.setOnClickFillSleepRecordBtnListener { FillSleepRecordActivity.launchForResult(this, System.currentTimeMillis(), REQUEST_CODE_FILL_SLEEP_RECORD) }
        ll_sleep_prescription.setOnClickListener { onSleepPrescriptionClick() }
        cbti_progress_view.setOnEnterLearnBtnClickListener(View.OnClickListener { run { ToastUtils.showShort("todo") } })
        doctor_service_item_view_cbti.setOnClickListener { ToastUtils.showShort("todo") }
        tv_sleep_health.setOnClickListener { ToastUtils.showShort(R.string.not_open_yet_please_wait) }
        tv_scale.setOnClickListener { ScaleListActivity.launch(context, ScaleListActivity.TYPE_ALL) }
    }

    private fun initUserInfo() {
        val userProfile = AppManager.getAccountViewModel().userProfile
        tv_name.text = userProfile.nickname
//        val defaultAvatar = if (userProfile.isMale) R.drawable.login_icon_gender_man_selected else R.drawable.login_icon_gender_woman_selected
        val defaultAvatar = R.mipmap.ic_info_avatar_patient
        ImageLoader.loadImage(this, iv_avatar, userProfile.avatar, defaultAvatar)
    }

    override fun initData() {
        super.initData()
        queryCbti()
        querySleepRecord()
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
//        AppManager.getHttpService().getServiceDetailById(17)
//                .enqueue(object : BaseResponseCallback<DoctorService>() {
//                    override fun onSuccess(response: DoctorService?) {
//                        LogUtils.d(response)
//                    }
//
//                    override fun onFailure(errorResponse: ErrorResponse) {
//
//                    }
//
//                })
//        AppManager.getHttpService().getServiceDetailByType(3)
//                .enqueue(object : BaseResponseCallback<Any>() {
//                    override fun onSuccess(response: Any?) {
//                        LogUtils.d(response)
//                    }
//
//                    override fun onFailure(errorResponse: ErrorResponse) {
//
//                    }
//
//                })
    }
}