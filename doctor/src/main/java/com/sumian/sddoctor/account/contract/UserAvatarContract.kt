package com.sumian.sddoctor.account.contract

import com.sumian.common.base.BaseShowLoadingView
import com.sumian.sddoctor.login.login.bean.DoctorInfo

interface UserAvatarContract {

    interface View : BaseShowLoadingView {
        fun onUploadAvatarSuccess(userInfo: DoctorInfo)
        fun onUploadAvatarFailed(error: String)
    }


}