package cn.leancloud.chatkit.utils;

import android.content.Context;
import android.text.TextUtils;

import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMReservedMessageType;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;

import cn.leancloud.chatkit.LCChatMessageInterface;
import cn.leancloud.chatkit.R;

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/3/14 20:39
 * desc   :
 * version: 1.0
 */
public class LCIMMessageUtil {
    public static CharSequence getMessageShorthand(Context context, AVIMMessage message) {
        if (message == null) {
            return "";
        }
        if (message instanceof AVIMTypedMessage) {
            AVIMReservedMessageType type = AVIMReservedMessageType.getAVIMReservedMessageType(
                    ((AVIMTypedMessage) message).getMessageType());
            switch (type) {
                case TextMessageType:
                    return ((AVIMTextMessage) message).getText();
                case ImageMessageType:
                    return context.getString(R.string.lcim_message_shorthand_image);
                case LocationMessageType:
                    return context.getString(R.string.lcim_message_shorthand_location);
                case AudioMessageType:
                    return context.getString(R.string.lcim_message_shorthand_audio);
                default:
                    CharSequence shortHand = "";
                    if (message instanceof LCChatMessageInterface) {
                        LCChatMessageInterface messageInterface = (LCChatMessageInterface) message;
                        shortHand = messageInterface.getShorthand();
                    }
                    if (TextUtils.isEmpty(shortHand)) {
                        shortHand = context.getString(R.string.lcim_message_shorthand_unknown);
                    }
                    return shortHand;
            }
        } else {
            return message.getContent();
        }
    }
}
