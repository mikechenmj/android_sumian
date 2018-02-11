package com.sumian.sleepdoctor.tab.bean;

import com.avos.avoscloud.im.v2.AVIMMessage;
import com.sumian.sleepdoctor.account.bean.UserProfile;

/**
 * Created by sm
 * on 2018/1/30.
 * desc:
 */

public class GroupItem {

    public GroupDetail<UserProfile, UserProfile> groupDetail;
    public AVIMMessage secondLastMsg;
    public AVIMMessage lastMsg;

    @Override
    public String toString() {
        return "GroupItem{" +
                "groupDetail=" + groupDetail +
                ", secondLastMsg=" + secondLastMsg +
                ", lastMsg=" + lastMsg +
                '}';
    }
}
