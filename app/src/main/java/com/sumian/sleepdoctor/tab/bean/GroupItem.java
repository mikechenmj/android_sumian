package com.sumian.sleepdoctor.tab.bean;

import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.sumian.sleepdoctor.account.bean.UserProfile;

/**
 * Created by sm
 * on 2018/1/30.
 * desc:
 */

public class GroupItem {

    public GroupDetail<UserProfile, UserProfile> groupDetail;
    public AVIMTypedMessage secondLastMsg;
    public AVIMTypedMessage lastMsg;
    public int unReadMsgCount;
    public boolean isMsgMentioned;


    @Override
    public String toString() {
        return "GroupItem{" +
                "groupDetail=" + groupDetail +
                ", secondLastMsg=" + secondLastMsg +
                ", lastMsg=" + lastMsg +
                ", unReadMsgCount=" + unReadMsgCount +
                ", isMsgMentioned=" + isMsgMentioned +
                '}';
    }
}
