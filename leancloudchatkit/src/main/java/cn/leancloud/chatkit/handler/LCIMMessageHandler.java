package cn.leancloud.chatkit.handler;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.AVIMTypedMessageHandler;

import org.greenrobot.eventbus.EventBus;

import cn.leancloud.chatkit.LCIMManager;
import cn.leancloud.chatkit.cache.LCIMConversationItemCache;
import cn.leancloud.chatkit.event.LCIMIMTypeMessageEvent;
import cn.leancloud.chatkit.utils.LCIMLogUtils;

/**
 * Created by zhangxiaobo on 15/4/20.
 * AVIMTypedMessage 的 handler，socket 过来的 AVIMTypedMessage 都会通过此 handler 与应用交互
 * 需要应用主动调用 AVIMMessageManager.registerMessageHandler 来注册
 * 当然，自定义的消息也可以通过这种方式来处理
 */
public class LCIMMessageHandler extends AVIMTypedMessageHandler<AVIMTypedMessage> {

    private static LCIMMessageHandler sInstance;

    private LCIMMessageHandler() {
    }

    public static LCIMMessageHandler getInstance() {
        if (sInstance == null) {
            synchronized (LCIMManager.class) {
                if (sInstance == null) {
                    sInstance = new LCIMMessageHandler();
                }
            }
        }
        return sInstance;
    }

    @Override
    public void onMessage(AVIMTypedMessage message, AVIMConversation conversation, AVIMClient client) {
        if (message == null || message.getMessageId() == null) {
            LCIMLogUtils.d("may be SDK Bug, message or message id is null");
            return;
        }
        LCIMManager.getInstance().getUserId();
        if (!client.getClientId().equals(LCIMManager.getInstance().getUserId())) {
            client.close(null);
        } else {
            LCIMConversationItemCache.getInstance().insertConversation(message.getConversationId());
            sendEvent(message, conversation);
        }
    }

    @Override
    public void onMessageReceipt(AVIMTypedMessage message, AVIMConversation conversation, AVIMClient client) {
        super.onMessageReceipt(message, conversation, client);
    }

    /**
     * 发送消息到来的通知事件
     *
     * @param message
     * @param conversation
     */
    private void sendEvent(AVIMTypedMessage message, AVIMConversation conversation) {
        LCIMIMTypeMessageEvent event = new LCIMIMTypeMessageEvent();
        event.message = message;
        event.conversation = conversation;
        EventBus.getDefault().post(event);
    }

//    private void sendNotification(final AVIMTypedMessage message, final AVIMConversation conversation) {
//        if (null != conversation && null != message) {
//            final String notificationContent = message instanceof AVIMTextMessage ?
//                    ((AVIMTextMessage) message).getText() : context.getString(R.string.lcim_unspport_message_type);
//            LCIMProfileCache.getInstance().getCachedUser(message.getFrom(), new AVCallback<LCChatKitUser>() {
//                @Override
//                protected void internalDone0(LCChatKitUser userProfile, AVException e) {
//                    if (e != null) {
//                        LCIMLogUtils.logException(e);
//                    } else if (null != userProfile) {
//                        String title = userProfile.getName();
//                        Intent intent = getIMNotificationIntent(conversation.getConversationId(), message.getFrom());
//                        LCIMNotificationUtils.showNotification(context, title, notificationContent, null, intent);
//                    }
//                }
//            });
//        }
//    }
//
//    /**
//     * 点击 notification 触发的 Intent
//     * 注意要设置 package 已经 Category，避免多 app 同时引用 lib 造成消息干扰
//     *
//     * @param conversationId
//     * @param peerId
//     * @return
//     */
//    private Intent getIMNotificationIntent(String conversationId, String peerId) {
//        Intent intent = new Intent();
//        intent.setAction(LCIMConstants.CHAT_NOTIFICATION_ACTION);
//        intent.putExtra(LCIMConstants.CONVERSATION_ID, conversationId);
//        intent.putExtra(LCIMConstants.PEER_ID, peerId);
//        intent.setPackage(context.getPackageName());
//        intent.addCategory(Intent.CATEGORY_DEFAULT);
//        return intent;
//    }
}
