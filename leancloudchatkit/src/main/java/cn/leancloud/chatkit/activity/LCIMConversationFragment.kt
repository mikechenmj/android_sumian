package cn.leancloud.chatkit.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import cn.leancloud.chatkit.LCIMManager
import cn.leancloud.chatkit.R
import cn.leancloud.chatkit.adapter.LCIMChatAdapter
import cn.leancloud.chatkit.event.*
import cn.leancloud.chatkit.utils.*
import com.avos.avoscloud.AVException
import com.avos.avoscloud.im.v2.AVIMConversation
import com.avos.avoscloud.im.v2.AVIMException
import com.avos.avoscloud.im.v2.AVIMMessage
import com.avos.avoscloud.im.v2.AVIMMessageOption
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback
import com.avos.avoscloud.im.v2.callback.AVIMMessageRecalledCallback
import com.avos.avoscloud.im.v2.callback.AVIMMessageUpdatedCallback
import com.avos.avoscloud.im.v2.callback.AVIMMessagesQueryCallback
import com.avos.avoscloud.im.v2.messages.AVIMAudioMessage
import com.avos.avoscloud.im.v2.messages.AVIMImageMessage
import com.avos.avoscloud.im.v2.messages.AVIMRecalledMessage
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage
import kotlinx.android.synthetic.main.lcim_conversation_fragment.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.File
import java.io.IOException

/**
 * Created by wli on 15/8/27.
 * 将聊天相关的封装到此 Fragment 里边，只需要通过 setConversation 传入 Conversation 即可
 */
class LCIMConversationFragment : Fragment() {
    private var mConversation: AVIMConversation? = null
    private var itemAdapter: LCIMChatAdapter = LCIMChatAdapter()
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var localCameraPath: String
    private var mHost: Host? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is Host) {
            mHost = context
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.lcim_conversation_fragment, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fragment_chat_srl_pullrefresh.isEnabled = false
        layoutManager = LinearLayoutManager(activity)
        fragment_chat_rv_chat.layoutManager = layoutManager
        itemAdapter.resetRecycledViewPoolSize(fragment_chat_rv_chat)
        fragment_chat_rv_chat.adapter = itemAdapter
        EventBus.getDefault().register(this)
        fragment_chat_srl_pullrefresh.setOnRefreshListener {
            val message = itemAdapter.firstMessage
            if (null == message) {
                fragment_chat_srl_pullrefresh.isRefreshing = false
            } else {
                mConversation!!.queryMessages(message.messageId, message.timestamp, 20, object : AVIMMessagesQueryCallback() {
                    override fun done(list: List<AVIMMessage>?, e: AVIMException?) {
                        fragment_chat_srl_pullrefresh.isRefreshing = false
                        if (filterException(e)) {
                            if (null != list && list.size > 0) {
                                itemAdapter.addMessageList(list)
                                itemAdapter.setDeliveredAndReadMark(mConversation!!.lastDeliveredAt,
                                        mConversation!!.lastReadAt)
                                itemAdapter.notifyDataSetChanged()
                                layoutManager.scrollToPositionWithOffset(list.size - 1, 0)
                            }
                        }
                    }
                })
            }
        }
        fragment_chat_inputbar.showAudioBtn(mHost?.isDoctor() ?: false)
    }

    override fun onResume() {
        super.onResume()
        if (null != mConversation) {
            LCIMNotificationUtils.addTag(mConversation!!.conversationId)
        }
    }

    override fun onPause() {
        super.onPause()
        LCIMAudioHelper.getInstance().stopPlayer()
        if (null != mConversation) {
            LCIMNotificationUtils.removeTag(mConversation!!.conversationId)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        LCIMManager.getInstance().setCurrentOpenConversation(null)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.conv_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == R.id.menu_conv_setting) {
            val intent = Intent(activity, LCIMConversationDetailActivity::class.java)
            intent.putExtra(LCIMConstants.CONVERSATION_ID, mConversation!!.conversationId)
            activity!!.startActivity(intent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun setConversation(conversation: AVIMConversation) {
        LCIMManager.getInstance().setCurrentOpenConversation(conversation)
        mConversation = conversation
        fragment_chat_srl_pullrefresh.isEnabled = true
        fragment_chat_inputbar.tag = mConversation!!.conversationId
        fetchMessages()
        mConversation!!.read()
        LCIMNotificationUtils.addTag(conversation.conversationId)
        if (!conversation.isTransient) {
            if (conversation.members.size == 0) {
                conversation.fetchInfoInBackground(object : AVIMConversationCallback() {
                    override fun done(e: AVIMException?) {
                        if (null != e) {
                            LCIMLogUtils.logException(e)
                            Toast.makeText(context, "encounter network error, please try later.", Toast.LENGTH_SHORT)
                        }
                        itemAdapter.showUserName(conversation.members.size > 2)
                    }
                })
            } else {
                itemAdapter.showUserName(conversation.members.size > 2)
            }
        } else {
            itemAdapter.showUserName(true)
        }
        updateConversationCloseUI()
    }

    /**
     * 拉取消息，必须加入 conversation 后才能拉取消息
     */
    private fun fetchMessages() {
        mConversation!!.queryMessages(object : AVIMMessagesQueryCallback() {
            override fun done(messageList: List<AVIMMessage>, e: AVIMException?) {
                if (filterException(e)) {
                    itemAdapter.setMessageList(messageList)
                    fragment_chat_rv_chat.adapter = itemAdapter
                    itemAdapter.setDeliveredAndReadMark(mConversation!!.lastDeliveredAt,
                            mConversation!!.lastReadAt)
                    itemAdapter.notifyDataSetChanged()
                    scrollToBottom()
                    clearUnreadConut()
                }
            }
        })
    }

    /**
     * 输入事件处理，接收后构造成 AVIMTextMessage 然后发送
     * 因为不排除某些特殊情况会受到其他页面过来的无效消息，所以此处加了 tag 判断
     */
    @Subscribe
    fun onEvent(textEvent: LCIMInputBottomBarTextEvent?) {
        LCIMLogUtils.d(textEvent!!.toString())
        if (null != mConversation && null != textEvent) {
            if (!TextUtils.isEmpty(textEvent.sendContent) && mConversation!!.conversationId == textEvent.tag) {
                sendText(textEvent.sendContent)
            }
        }
    }

    /**
     * 处理推送过来的消息
     * 同理，避免无效消息，此处加了 conversation id 判断
     */
    @Subscribe
    fun onEvent(messageEvent: LCIMIMTypeMessageEvent?) {
        if (null != mConversation && null != messageEvent &&
                mConversation!!.conversationId == messageEvent.conversation.conversationId) {
            println("currentConv unreadCount=" + mConversation!!.unreadMessagesCount)
            if (mConversation!!.unreadMessagesCount > 0) {
                paddingNewMessage(mConversation)
            } else {
                itemAdapter.addMessage(messageEvent.message)
                itemAdapter.notifyDataSetChanged()
                scrollToBottom()
            }
//            itemAdapter.addMessage(messageEvent.message)
//            itemAdapter.notifyDataSetChanged()
//            scrollToBottom()
        }
    }

    /**
     * 重新发送已经发送失败的消息
     */
    @Subscribe
    fun onEvent(resendEvent: LCIMMessageResendEvent?) {
        if (null != mConversation && null != resendEvent &&
                null != resendEvent.message && mConversation!!.conversationId == resendEvent.message.conversationId) {
            if (AVIMMessage.AVIMMessageStatus.AVIMMessageStatusFailed == resendEvent.message.messageStatus && mConversation!!.conversationId == resendEvent.message.conversationId) {
                sendMessage(resendEvent.message, false)
            }
        }
    }

    /**
     * 处理输入栏发送过来的事件
     *
     * @param event
     */
    @Subscribe
    fun onEvent(event: LCIMInputBottomBarEvent?) {
        if (null != mConversation && null != event && mConversation!!.conversationId == event.tag) {
            when (event.eventAction) {
                LCIMInputBottomBarEvent.INPUTBOTTOMBAR_IMAGE_ACTION -> dispatchPickPictureIntent()
                LCIMInputBottomBarEvent.INPUTBOTTOMBAR_CAMERA_ACTION -> dispatchTakePictureIntent()
                else -> {
                }
            }
        }
    }

    /**
     * 处理录音事件
     *
     * @param recordEvent
     */
    @Subscribe
    fun onEvent(recordEvent: LCIMInputBottomBarRecordEvent?) {
        if (null != mConversation && null != recordEvent
                && !TextUtils.isEmpty(recordEvent.audioPath)
                && mConversation!!.conversationId == recordEvent.tag) {
            if (recordEvent.audioDuration > 0)
                sendAudio(recordEvent.audioPath)
        }
    }

    /**
     * 更新对方已读的位置事件
     *
     * @param readEvent
     */
    @Subscribe
    fun onEvent(readEvent: LCIMConversationReadStatusEvent?) {
        if (null != mConversation && null != readEvent &&
                mConversation!!.conversationId == readEvent.conversationId) {
            itemAdapter.setDeliveredAndReadMark(mConversation!!.lastDeliveredAt,
                    mConversation!!.lastReadAt)
            itemAdapter.notifyDataSetChanged()
        }
    }

    @Subscribe
    fun onEvent(event: LCIMMessageUpdateEvent?) {
        if (null != mConversation && null != event &&
                null != event.message && mConversation!!.conversationId == event.message.conversationId) {
            val builder = AlertDialog.Builder(activity!!)
            builder.setTitle("操作").setItems(arrayOf("撤回", "修改消息内容")) { dialog, which ->
                if (0 == which) {
                    recallMessage(event.message)
                } else if (1 == which) {
                    showUpdateMessageDialog(event.message)
                }
            }
            builder.create().show()
        }
    }

    @Subscribe
    fun onEvent(event: LCIMMessageUpdatedEvent?) {
        if (null != mConversation && null != event &&
                null != event.message && mConversation!!.conversationId == event.message.conversationId) {
            itemAdapter.updateMessage(event.message)
        }
    }

    @Subscribe
    fun onEvent(event: LCIMOfflineMessageCountChangeEvent?) {
        if (null == event || null == event.conversation || null == event.conversation) {
            return
        }
        if (mConversation!!.conversationId != event.conversation.conversationId) {
            return
        }
        if (event.conversation.unreadMessagesCount < 1) {
            return
        }
        paddingNewMessage(event.conversation)
    }

    @Subscribe
    fun onEvent(event: LCIMConversationInfoChangeEvent) {
        if (event.mConversation == null) {
            return
        }
        if (mConversation!!.conversationId != event.mConversation.conversationId) {
            return
        }
        updateConversationCloseUI()
    }

    private fun paddingNewMessage(currentConversation: AVIMConversation?) {
        if (null == currentConversation || currentConversation.unreadMessagesCount < 1) {
            return
        }
        val queryLimit = if (currentConversation.unreadMessagesCount > 100) 100 else currentConversation.unreadMessagesCount
        currentConversation.queryMessages(queryLimit, object : AVIMMessagesQueryCallback() {
            override fun done(list: List<AVIMMessage>, e: AVIMException?) {
                if (null != e) {
                    return
                }
                for (m in list) {
                    itemAdapter.addMessage(m)
                }
                itemAdapter.notifyDataSetChanged()
                clearUnreadConut()
            }
        })
    }

    private fun showUpdateMessageDialog(message: AVIMMessage) {
        val builder = AlertDialog.Builder(activity!!)
        val editText = EditText(activity)
        builder.setView(editText)
        builder.setTitle("修改消息内容")
        builder.setNegativeButton("取消") { dialog, which -> dialog.dismiss() }
        builder.setPositiveButton("提交") { dialog, which ->
            dialog.dismiss()
            val content = editText.text.toString()
            updateMessage(message, content)
        }
        builder.show()
    }

    private fun recallMessage(message: AVIMMessage) {
        mConversation!!.recallMessage(message, object : AVIMMessageRecalledCallback() {
            override fun done(recalledMessage: AVIMRecalledMessage, e: AVException?) {
                if (null == e) {
                    itemAdapter.updateMessage(recalledMessage)
                } else {
                    Toast.makeText(activity, "撤回失败", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun updateMessage(message: AVIMMessage, newContent: String) {
        val textMessage = AVIMTextMessage()
        textMessage.text = newContent
        mConversation!!.updateMessage(message, textMessage, object : AVIMMessageUpdatedCallback() {
            override fun done(message: AVIMMessage, e: AVException?) {
                if (null == e) {
                    itemAdapter.updateMessage(message)
                } else {
                    Toast.makeText(activity, "更新失败", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    /**
     * 发送 Intent 跳转到系统拍照页面
     */
    private fun dispatchTakePictureIntent() {

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            localCameraPath = LCIMPathUtils.getPicturePathByCurrentTime(context)
            val imageUri = Uri.fromFile(File(localCameraPath))
            takePictureIntent.putExtra("return-data", false)
            takePictureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageUri)
        } else {
            localCameraPath = Environment.getExternalStorageDirectory().toString() + "/images/" + System.currentTimeMillis() + ".jpg"
            val photoFile = File(localCameraPath)

            val photoURI = FileProvider.getUriForFile(this.context!!,
                    this.context!!.packageName + ".provider", photoFile)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                    photoURI)
        }
        if (takePictureIntent.resolveActivity(activity!!.packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    /**
     * 发送 Intent 跳转到系统图库页面
     */
    private fun dispatchPickPictureIntent() {
        val photoPickerIntent = Intent(Intent.ACTION_PICK, null)
        photoPickerIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        startActivityForResult(photoPickerIntent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        println("requestCode=$requestCode, resultCode=$resultCode")
        if (Activity.RESULT_OK == resultCode) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> sendImage(localCameraPath)
                REQUEST_IMAGE_PICK -> sendImage(getRealPathFromURI(activity, data!!.data!!))
                else -> {
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * 滚动 recyclerView 到底部
     */
    private fun scrollToBottom() {
        layoutManager.scrollToPositionWithOffset(itemAdapter.itemCount - 1, 0)
    }

    /**
     * 根据 Uri 获取文件所在的位置
     *
     * @param context
     * @param contentUri
     * @return
     */
    private fun getRealPathFromURI(context: Context?, contentUri: Uri): String? {
        if (contentUri.scheme == "file") {
            return contentUri.encodedPath
        } else {
            var cursor: Cursor? = null
            try {
                val proj = arrayOf(MediaStore.Images.Media.DATA)
                cursor = context!!.contentResolver.query(contentUri, proj, null, null, null)
                if (null != cursor) {
                    val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    cursor.moveToFirst()
                    return cursor.getString(column_index)
                } else {
                    return ""
                }
            } finally {
                cursor?.close()
            }
        }
    }

    /**
     * 发送文本消息
     *
     * @param content
     */
    protected fun sendText(content: String) {
        val message = AVIMTextMessage()
        message.text = content
        sendMessage(message)
    }

    /**
     * 发送图片消息
     * TODO 上传的图片最好要压缩一下
     *
     * @param imagePath
     */
    protected fun sendImage(imagePath: String?) {
        try {
            sendMessage(AVIMImageMessage(imagePath!!))
        } catch (e: IOException) {
            LCIMLogUtils.logException(e)
        }

    }

    /**
     * 发送语音消息
     *
     * @param audioPath
     */
    protected fun sendAudio(audioPath: String) {
        try {
            val audioMessage = AVIMAudioMessage(audioPath)
            sendMessage(audioMessage)
        } catch (e: IOException) {
            LCIMLogUtils.logException(e)
        }

    }

    /**
     * 发送消息
     *
     * @param message
     */
    @JvmOverloads
    fun sendMessage(message: AVIMMessage, addToList: Boolean = true) {
        closeConversation(false)
        if (addToList) {
            itemAdapter.addMessage(message)
        }
        itemAdapter.notifyDataSetChanged()
        scrollToBottom()

        val option = AVIMMessageOption()
        option.isReceipt = true
        mConversation!!.sendMessage(message, option, object : AVIMConversationCallback() {
            override fun done(e: AVIMException?) {
                itemAdapter.notifyDataSetChanged()
                if (null != e) {
                    LCIMLogUtils.logException(e)
                }
            }
        })
    }

    private fun filterException(e: Exception?): Boolean {
        if (null != e) {
            LCIMLogUtils.logException(e)
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }
        return null == e
    }

    private fun clearUnreadConut() {
        if (mConversation!!.unreadMessagesCount > 0) {
            mConversation!!.read()
        }
    }

    fun showInputBar(show: Boolean) {
        fragment_chat_inputbar.visibility = if (show) View.VISIBLE else View.GONE
        fragment_chat_tv_doctor_is_busy.visibility = if (!show) View.VISIBLE else View.GONE
    }

    fun showAudioBtn(show: Boolean) {
        fragment_chat_inputbar.showAudioBtn(show)
    }

    /**
     * close: 1 close, 0 open
     */
    fun closeConversation(close: Boolean, callback: AVIMConversationCallback? = null) {
        mConversation?.set(ATTR_IS_BLOCKED, close)
        mConversation?.updateInfoInBackground(object : AVIMConversationCallback() {
            override fun done(p0: AVIMException?) {
                updateConversationCloseUI()
                callback?.done(p0)
            }
        })
    }

    private fun updateConversationCloseUI() {
        val isBlocked = isConversationBlocked()
        val isDoctor = mHost?.isDoctor() ?: false
        if (isDoctor) {
            tv_open_conversation_hint.visibility = if (isBlocked) View.VISIBLE else View.GONE
        } else {
            fragment_chat_tv_doctor_is_busy.visibility = if (isBlocked) View.VISIBLE else View.GONE
            fragment_chat_inputbar.visibility = if (!isBlocked) View.VISIBLE else View.GONE
        }
    }

    fun isConversationBlocked() = mConversation?.get(ATTR_IS_BLOCKED) == true

    fun getConversation(): AVIMConversation? {
        return mConversation
    }

    companion object {
        private val REQUEST_IMAGE_CAPTURE = 1
        private val REQUEST_IMAGE_PICK = 2
        private val ATTR_IS_BLOCKED = "isBlocked"
    }

    interface Host {
        fun isDoctor(): Boolean
    }
}
