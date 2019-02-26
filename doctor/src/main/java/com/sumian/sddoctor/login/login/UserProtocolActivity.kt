package com.sumian.sddoctor.login.login

import com.sumian.sddoctor.BuildConfig
import com.sumian.sddoctor.constants.H5Uri
import com.sumian.sddoctor.constants.StatConstants
import com.sumian.sddoctor.h5.SddBaseWebViewActivity

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     time   : 2018/9/10 15:18
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class UserProtocolActivity : SddBaseWebViewActivity() {
    override fun getCompleteUrl(): String {
        return BuildConfig.BASE_H5_URL + H5Uri.USER_PROTOCOL
    }

    override fun getPageName(): String {
        return StatConstants.page_profile_protocol
    }
}