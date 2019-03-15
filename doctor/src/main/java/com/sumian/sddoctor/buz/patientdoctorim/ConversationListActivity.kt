package com.sumian.sddoctor.buz.patientdoctorim

import android.content.Intent
import cn.leancloud.chatkit.activity.LCIMConversationListFragmentV2
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.sddoctor.R
import com.sumian.sddoctor.base.SddBaseActivity

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/3/12 17:59
 * desc   :
 * version: 1.0
 */
class ConversationListActivity : SddBaseActivity(), LCIMConversationListFragmentV2.Host {
    override fun getLayoutId(): Int {
        return R.layout.activity_conversation_list
    }

    override fun showBackNav(): Boolean {
        return true
    }

    override fun initWidget() {
        super.initWidget()
        setTitle(R.string.patient_message)
    }

    companion object {
        fun launch() {
            ActivityUtils.startActivity(Intent(ActivityUtils.getTopActivity(), ConversationListActivity::class.java))
        }
    }

    override fun isDoctor(): Boolean {
        return true
    }
}