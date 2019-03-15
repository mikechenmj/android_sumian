package com.sumian.sddoctor.buz.patientdoctorim

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
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
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.sumian.sddoctor.R
import com.sumian.sddoctor.base.SddBaseActivity
import com.sumian.sddoctor.widget.SumianAlertDialog
import kotlinx.android.synthetic.main.bottom_sheet_close_conversation.view.*
import java.util.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/3/8 10:58
 * desc   :
 * version: 1.0
 */
open class ConversationActivity : SddBaseActivity(), LCIMConversationFragment.Host {
    private val mFragment: LCIMConversationFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.conversation_fragment) as LCIMConversationFragment
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_conversation
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

    override fun initWidget() {
        super.initWidget()
        mTitleBar.showMoreIcon()
        mTitleBar.mIvMenu.setImageResource(R.drawable.ic_nav_icon_more)
        mTitleBar.mIvMenu.setOnClickListener { showCloseConversationBottomSheet() }
    }

    private fun showCloseConversationBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val content = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_close_conversation, null)
        bottomSheetDialog.setContentView(content)
        content.tv_close_conversation.setOnClickListener {
            showCloseConversationDialog()
            bottomSheetDialog.dismiss()
        }
        content.tv_cancel.setOnClickListener { bottomSheetDialog.dismiss() }
        bottomSheetDialog.show()
    }

    private fun showCloseConversationDialog() {
        SumianAlertDialog(this)
                .setTitle(R.string.clocse_conversation_dialog_title)
                .setMessage(R.string.clocse_conversation_dialog_message)
                .setLeftBtn(R.string.cancel, null)
                .whitenLeft()
                .setRightBtn(R.string.confirm, object : View.OnClickListener {
                    override fun onClick(v: View?) {
                        closeConversation()
                    }
                })
                .show()
    }

    private fun closeConversation() {
        mFragment.closeConversation(true, object : AVIMConversationCallback() {
            override fun done(e: AVIMException?) {
                if (e != null) {
                    ToastUtils.showShort(e.message)
                } else {
                }
            }
        })
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
        LCIMManager.getInstance().client.createConversation(
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
            LCIMManager.getInstance().removeUnreadConversation(conversation)
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

    override fun isDoctor(): Boolean {
        return true
    }
}