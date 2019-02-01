package com.sumian.sddoctor.account.contract

import com.sumian.common.mvp.BaseShowLoadingView
import com.sumian.common.mvp.IPresenter
import com.sumian.sddoctor.login.login.bean.DoctorInfo

interface UserAvatarContract {

    interface View : BaseShowLoadingView {
        fun onUploadAvatarSuccess(userInfo: DoctorInfo)
        fun onUploadAvatarFailed(error: String)
    }


    interface Presenter : IPresenter {
        fun uploadAvatar(avatarPathUrl: String)
    }
}