package com.sumian.sd.examine.main.fragment

import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialog
import com.sumian.common.base.BaseFragment
import com.sumian.sd.R
import com.sumian.sd.examine.main.consultant.ExamineConsultantMsgActivity
import com.sumian.sd.examine.widget.ContactDialog
import com.sumian.sd.main.OnEnterListener
import kotlinx.android.synthetic.main.examine_consultant_fragment.*

class ExamineConsultantFragment : BaseFragment(), OnEnterListener {

    override fun getLayoutId(): Int {
        return R.layout.examine_consultant_fragment
    }

    override fun onEnter(data: String?) {
    }

    override fun initWidget() {
        super.initWidget()
        lay_customer_call_service.setOnClickListener {
            ContactDialog().show(fragmentManager!!, "ContactDialog")
        }
        lay_doctor_service.setOnClickListener { ExamineConsultantMsgActivity.show("速眠医生", "您好，我是您的专属睡眠医生，有什么睡眠问题都可以留言咨询我。") }
        lay_online_service.setOnClickListener { ExamineConsultantMsgActivity.show("线上客服", "您好，我是您的售后客服-小眠。对于设备您有什么疑问，都可以咨询我。") }
    }
}