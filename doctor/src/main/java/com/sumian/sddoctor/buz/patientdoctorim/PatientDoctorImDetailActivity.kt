package com.sumian.sddoctor.buz.patientdoctorim

import android.content.Intent
import android.widget.Toast
import cn.leancloud.chatkit.LCChatKit
import cn.leancloud.chatkit.LCChatKitUser
import cn.leancloud.chatkit.LCChatProfileProvider
import cn.leancloud.chatkit.LCChatProfilesCallBack
import cn.leancloud.chatkit.activity.LCIMConversationFragment
import cn.leancloud.chatkit.cache.LCIMConversationItemCache
import cn.leancloud.chatkit.utils.LCIMConversationUtils
import cn.leancloud.chatkit.utils.LCIMLogUtils
import com.avos.avoscloud.AVCallback
import com.avos.avoscloud.AVException
import com.avos.avoscloud.im.v2.AVIMClient
import com.avos.avoscloud.im.v2.AVIMConversation
import com.avos.avoscloud.im.v2.AVIMException
import com.avos.avoscloud.im.v2.AVIMTemporaryConversation
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.sddoctor.BuildConfig
import com.sumian.sddoctor.R
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.base.SddBaseActivity

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/3/8 10:58
 * desc   :
 * version: 1.0
 */
open class PatientDoctorImDetailActivity : SddBaseActivity() {
    private val mFragment: LCIMConversationFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.conversation_fragment) as LCIMConversationFragment
    }
    override fun getLayoutId(): Int {
        return R.layout.im_activity_patient_doctor_im_detail
    }

    override fun showBackNav(): Boolean {
        return true
    }

    override fun initWidget() {
        super.initWidget()
        val doctor = AppManager.getAccountViewModel().getDoctorInfo().value!!
        val clientId = doctor.im_id
        LCChatKit.getInstance().init(this, BuildConfig.LEANCLOUD_APP_ID, BuildConfig.LEANCLOUD_APP_KEY)
        LCChatKit.getInstance().open(clientId, object : AVIMClientCallback() {
            override fun done(avimClient: AVIMClient, e: AVIMException?) {
                if (null == e) {
                    onConversationOpened()
                } else {
                    Toast.makeText(this@PatientDoctorImDetailActivity, e.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        })
        LCChatKit.getInstance().profileProvider = object : LCChatProfileProvider {
            override fun fetchProfiles(userIdList: MutableList<String>?, profilesCallBack: LCChatProfilesCallBack?) {
                val userList = ArrayList<LCChatKitUser>()
                userList.add(LCChatKitUser(doctor.im_id, doctor.name, doctor.avatar))
                profilesCallBack?.done(userList, null)
            }

            override fun getAllUsers(): MutableList<LCChatKitUser> {
                return ArrayList<LCChatKitUser>()
            }
        }

    }

    private fun onConversationOpened() {
        LCChatKit.getInstance().client.createConversation(listOf("jack", "lucy"), "Titanic", null, false, true, object : AVIMConversationCreatedCallback() {
            override fun done(conversation: AVIMConversation?, p1: AVIMException?) {
                updateConversation(LCChatKit.getInstance().client!!.getConversation(conversation?.conversationId))
            }
        })
    }

    private fun updateConversation(conversation: AVIMConversation?) {
        if (null != conversation) {
            if (conversation is AVIMTemporaryConversation) {
                println("Conversation expired flag: " + conversation.isExpired)
            }
            mFragment.setConversation(conversation)
            LCIMConversationItemCache.getInstance().insertConversation(conversation.conversationId)
            LCIMConversationUtils.getConversationName(conversation, object : AVCallback<String>() {
                override fun internalDone0(s: String, e: AVException?) {
                    if (null != e) {
                        LCIMLogUtils.logException(e)
                    } else {
                    }
                }
            })
        }
    }

    companion object {
        fun launch() {
            ActivityUtils.startActivity(Intent(ActivityUtils.getTopActivity(), PatientDoctorImDetailActivity::class.java))
        }
    }
}