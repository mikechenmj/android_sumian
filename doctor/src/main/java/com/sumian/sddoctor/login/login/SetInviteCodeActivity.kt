package com.sumian.sddoctor.login.login

import android.view.View
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.statistic.StatUtil
import com.sumian.sddoctor.R
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.base.SddBaseActivity
import com.sumian.sddoctor.constants.StatConstants
import com.sumian.sddoctor.login.login.bean.DoctorInfo
import com.sumian.sddoctor.main.MainActivity
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback
import kotlinx.android.synthetic.main.activity_set_invite_code.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/12/24 10:23
 * desc   :
 * version: 1.0
 */
class SetInviteCodeActivity : SddBaseActivity() {
    override fun showBackNav(): Boolean {
        return true
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_set_invite_code
    }

    override fun initWidget() {
        super.initWidget()
        StatUtil.event(StatConstants.enter_fill_invite_code_page)
        mTitleBar.mIvBack.visibility = View.GONE
        setTitle(R.string.invite_code)
        mTitleBar.setMenuText(getString(R.string.skip))
        mTitleBar.setOnMenuClickListener {
            StatUtil.event(StatConstants.click_fill_invite_code_page_skip_btn)
            launchMain() }
        bt_complete.setOnClickListener { postInviteCode(et_invite_code.text.toString()) }
    }

    private fun launchMain() {
        ActivityUtils.finishAllActivities()
        ActivityUtils.startActivity(MainActivity::class.java)
    }

    private fun postInviteCode(inviteCode: String) {
        if (inviteCode.isEmpty()) {
            ToastUtils.showShort("输入不能为空")
            return
        }
        StatUtil.event(StatConstants.click_fill_invite_code_page_submit_btn)
        val call = AppManager.getHttpService().updateDoctorInfo(mapOf("retailer_invitation_code" to inviteCode))
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<DoctorInfo>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
            }

            override fun onSuccess(response: DoctorInfo?) {
                AppManager.getAccountViewModel().updateDoctorInfo(response)
                launchMain()
            }
        })
    }
}