package cn.leancloud.chatkit.event;

import com.avos.avoscloud.im.v2.AVIMConversation;

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/3/14 17:44
 * desc   :
 * version: 1.0
 */
public class LCIMConversationInfoChangeEvent {

    public AVIMConversation mConversation;

    public LCIMConversationInfoChangeEvent(AVIMConversation conversation) {
        mConversation = conversation;
    }
}
