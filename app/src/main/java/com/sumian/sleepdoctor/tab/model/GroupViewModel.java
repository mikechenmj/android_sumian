package com.sumian.sleepdoctor.tab.model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.sumian.sleepdoctor.account.bean.UserProfile;
import com.sumian.sleepdoctor.tab.bean.GroupDetail;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jzz
 * on 2018/1/19.
 * desc:
 */

public class GroupViewModel extends ViewModel {

    private MutableLiveData<List<GroupDetail<UserProfile, UserProfile>>> mGroupsLiveData;
    private MutableLiveData<GroupDetail<UserProfile, UserProfile>> mGroupDetailLiveData;


    public void notifyGroups(List<GroupDetail<UserProfile, UserProfile>> groupList) {
        if (mGroupsLiveData == null) {
            mGroupsLiveData = new MutableLiveData<>();
        }

        if (groupList == null || groupList.isEmpty()) return;
        List<GroupDetail<UserProfile, UserProfile>> groups = mGroupsLiveData.getValue();
        if (groups == null) {
            groups = new ArrayList<>();
        }
        groups.addAll(groupList);
        mGroupsLiveData.postValue(groupList);
    }

    public void notifyGroupDetail(GroupDetail<UserProfile, UserProfile> groupDetail) {
        if (mGroupDetailLiveData == null) {
            mGroupDetailLiveData = new MutableLiveData<>();
        }
        mGroupDetailLiveData.postValue(groupDetail);
    }

    public LiveData<List<GroupDetail<UserProfile, UserProfile>>> getGroupsLiveData() {
        if (mGroupsLiveData == null) {
            mGroupsLiveData = new MutableLiveData<>();
        }
        return mGroupsLiveData;
    }

    public LiveData<GroupDetail<UserProfile, UserProfile>> getGroupDetail() {
        if (mGroupDetailLiveData == null) {
            mGroupDetailLiveData = new MutableLiveData<>();
        }
        return mGroupDetailLiveData;
    }

}
