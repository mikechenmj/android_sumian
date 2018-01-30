package com.sumian.sleepdoctor.tab.bean;

import com.avos.avoscloud.im.v2.AVIMMessage;
import com.sumian.sleepdoctor.account.bean.UserProfile;

/**
 * Created by sm
 * on 2018/1/30.
 * desc:
 */

public class GroupItem {

    public GroupDetail<UserProfile, UserProfile> mGroupDetail;
    public AVIMMessage SecondLastMsg;
    public AVIMMessage LastMsg;

    @Override
    public String toString() {
        return "GroupItem{" +
                "mGroupDetail=" + mGroupDetail +
                ", SecondLastMsg=" + SecondLastMsg +
                ", LastMsg=" + LastMsg +
                '}';
    }
}
