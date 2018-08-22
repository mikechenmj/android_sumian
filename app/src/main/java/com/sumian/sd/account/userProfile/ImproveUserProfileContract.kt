package com.sumian.sd.account.userProfile

import com.sumian.sd.base.SdBasePresenter
import com.sumian.sd.base.SdBaseView

/**
 * Created by jzz
 * on 2018/1/18.
 * desc:
 */

interface ImproveUserProfileContract {


    interface View : SdBaseView<Presenter> {

        fun onImproveUserProfileSuccess()
    }

    interface Presenter : SdBasePresenter<Any> {

        fun improveUserProfile(improveKey: String, newUserProfile: String)

    }

    companion object {

        const val IMPROVE_NICKNAME_KEY = "nickname"
        const val IMPROVE_NAME_KEY = "name"
        const val IMPROVE_CAREER_KEY = "career"
        const val IMPROVE_BIRTHDAY_KEY = "birthday"
        const val IMPROVE_HEIGHT_KEY = "height"
        const val IMPROVE_WEIGHT_KEY = "weight"
        const val IMPROVE_GENDER_KEY = "gender"
        const val IMPROVE_EDUCATION_KEY = "education"
    }
}
