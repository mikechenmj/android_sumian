package com.sumian.sddoctor.notification

import android.content.Context
import android.content.Intent
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.utils.TimeUtilV2
import com.sumian.sddoctor.R
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.base.SddBaseActivity
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback
import com.sumian.sddoctor.notification.bean.SystemNotificationData
import kotlinx.android.synthetic.main.activity_system_notification_detail.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/12/11 11:05
 * desc   :
 * version: 1.0
 */
class SystemNotificationDetailActivity : SddBaseActivity() {

    companion object {
        private const val KEY_ID = "com.sumian.sddoctor.notification.SystemNotificationDetailActivity.id"

        fun getIntent(context: Context, id: Int): Intent {
            val intent = Intent(context, SystemNotificationDetailActivity::class.java)
            intent.putExtra(KEY_ID, id)
            return intent
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_system_notification_detail
    }

    override fun showBackNav(): Boolean {
        return true
    }

    override fun initWidget() {
        super.initWidget()
        setTitle(R.string.system_notification)
    }

    override fun initData() {
        super.initData()
        val call = AppManager.getHttpService().getSystemNotificationDetail(intent.getIntExtra(KEY_ID, 0))
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<SystemNotificationData>() {
            override fun onSuccess(response: SystemNotificationData?) {
                updateUI(response ?: return)
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
            }
        })
    }

    private fun updateUI(data: SystemNotificationData) {
        tv_notification_title.text = data.content
        tv_content.text = data.contentDetail
        tv_time.text = TimeUtilV2.formatYYYYMMDDHHMM(data.noticeAt)
    }
}