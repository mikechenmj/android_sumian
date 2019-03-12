package com.sumian.sddoctor.buz.patientdoctorim

import android.content.Intent
import android.os.Bundle
import cn.leancloud.chatkit.LCChatKit
import com.avos.avoscloud.im.v2.AVIMClient
import com.avos.avoscloud.im.v2.AVIMException
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.sddoctor.R
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.base.SddBaseActivity

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/3/12 17:59
 * desc   :
 * version: 1.0
 */
class ConversationListActivity : SddBaseActivity() {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val doctor = AppManager.getAccountViewModel().getDoctorInfo().value!!
        val clientId = doctor.im_id
        LCChatKit.getInstance().open(clientId, object : AVIMClientCallback() {
            override fun done(p0: AVIMClient?, p1: AVIMException?) {
            }
        })
    }

    companion object {
        fun launch() {
            ActivityUtils.startActivity(Intent(ActivityUtils.getTopActivity(), ConversationListActivity::class.java))
        }
    }
}