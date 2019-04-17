package com.sumian.sddoctor.login.login

import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sddoctor.R
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.base.SddBaseActivity
import com.sumian.sddoctor.booking.bean.Booking
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback
import kotlinx.android.synthetic.main.activity_choose_identity.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/4/15 15:55
 * desc   :
 * version: 1.0
 */
class ChooseIdentityActivity : SddBaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_choose_identity
    }

    override fun initWidget() {
        super.initWidget()
        setTitle(R.string.choose_identity)
        tv_doctor.setOnClickListener { changeIdentity(1) }
        tv_counselor.setOnClickListener { changeIdentity(2) }
    }

    override fun showBackNav(): Boolean {
        return true
    }

    /**
     * type: 1 doctor, 2 counselor
     */
    private fun changeIdentity(type: Int) {
        tv_doctor.isSelected = type == 1
        tv_counselor.isSelected = type == 2
        tv_doctor.setTextColor(resources.getColor(if (tv_doctor.isSelected) R.color.t4_color else R.color.t2_color))
        tv_counselor.setTextColor(resources.getColor(if (tv_counselor.isSelected) R.color.t4_color else R.color.t2_color))

        val call = AppManager.getHttpService().getBookingByDate(1)
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<List<Booking>>() {
            override fun onSuccess(response: List<Booking>?) {
                ActivityUtils.startActivity(SetInviteCodeActivity::class.java)
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
            }
        })
    }
}