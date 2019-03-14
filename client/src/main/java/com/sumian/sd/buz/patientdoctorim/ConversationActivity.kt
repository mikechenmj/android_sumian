package com.sumian.sd.buz.patientdoctorim

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.util.Log
import cn.leancloud.chatkit.LCIMManager
import cn.leancloud.chatkit.activity.LCIMConversationFragment
import cn.leancloud.chatkit.cache.LCIMConversationItemCache
import cn.leancloud.chatkit.utils.LCIMConstants
import cn.leancloud.chatkit.utils.LCIMConversationUtils
import cn.leancloud.chatkit.utils.LCIMLogUtils
import com.avos.avoscloud.AVCallback
import com.avos.avoscloud.AVException
import com.avos.avoscloud.im.v2.AVIMConversation
import com.avos.avoscloud.im.v2.AVIMException
import com.avos.avoscloud.im.v2.AVIMTemporaryConversation
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.base.BaseActivity
import com.sumian.sd.R
import java.util.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/3/14 15:20
 * desc   :
 * version: 1.0
 */
class ConversationActivity : BaseActivity() {

    private val mFragment: LCIMConversationFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.conversation_fragment) as LCIMConversationFragment
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_conversaion
    }

    override fun showBackNav(): Boolean {
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initByIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initByIntent(intent ?: return)
    }


    private fun initByIntent(intent: Intent) {
        val extras = intent.extras
        if (null != extras) {
            if (extras.containsKey(LCIMConstants.PEER_ID)) {
                getConversation(extras.getString(LCIMConstants.PEER_ID))
            } else if (extras.containsKey(LCIMConstants.CONVERSATION_ID)) {
                val conversationId = extras.getString(LCIMConstants.CONVERSATION_ID)
                updateConversation(LCIMManager.getInstance().client!!.getConversation(conversationId))
            } else {
                ToastUtils.showShort("memberId or conversationId is needed")
                finish()
            }
        }
    }

    private fun getConversation(memberId: String?) {
        LCIMManager.getInstance().client!!.createConversation(
                Arrays.asList<String>(memberId), "", null, false, true, object : AVIMConversationCreatedCallback() {
            override fun done(avimConversation: AVIMConversation, e: AVIMException?) {
                if (null != e) {
                    ToastUtils.showShort(e.message)
                } else {
                    updateConversation(avimConversation)
                }
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
                        setTitle(s)
                    }
                }
            })
        }
    }

    companion object {
        fun launch(conversationId: String) {
            try {
                val activity = ActivityUtils.getTopActivity()
                val intent = Intent()
                intent.setPackage(activity.getPackageName())
                intent.action = LCIMConstants.CONVERSATION_ITEM_CLICK_ACTION
                intent.addCategory(Intent.CATEGORY_DEFAULT)
                intent.putExtra(LCIMConstants.CONVERSATION_ID, conversationId)
                activity.startActivity(intent)
            } catch (exception: ActivityNotFoundException) {
                Log.i(LCIMConstants.LCIM_LOG_TAG, exception.toString())
            }

        }
    }
}