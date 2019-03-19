package com.sumian.sd.buz.patientdoctorim

import android.os.Bundle
import cn.leancloud.chatkit.activity.LCIMConversationListFragmentV2
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.common.base.BaseViewModel
import com.sumian.common.base.BaseViewModelActivity
import com.sumian.common.statistic.StatUtil
import com.sumian.sd.R
import com.sumian.sd.buz.stat.StatConstants

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/3/7 10:38
 * desc   :
 * version: 1.0
 */
class ConversationListActivity : BaseViewModelActivity<BaseViewModel>(), LCIMConversationListFragmentV2.Host {
    override fun getLayoutId(): Int {
        return R.layout.activity_patient_doctor_im
    }

    override fun showBackNav(): Boolean {
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatUtil.event(StatConstants.enter_conversation_list_page)
    }

    override fun initWidget() {
        super.initWidget()
        setTitle(R.string.doctor_message)
    }

    companion object {
        fun launch() {
            ActivityUtils.startActivity(ActivityUtils.getTopActivity(), ConversationListActivity::class.java)
        }
    }

    override fun isDoctor(): Boolean {
        return false
    }
}