package com.sumian.sd.examine.main.me

import androidx.lifecycle.*
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.account.bean.UserInfo
import com.sumian.sd.buz.account.model.AccountManager

class ExamineMeViewModel: ViewModel() {

    val userInfoData = AccountManager.getUserInfoLiveData()
}