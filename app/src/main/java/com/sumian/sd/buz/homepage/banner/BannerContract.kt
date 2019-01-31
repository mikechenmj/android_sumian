package com.sumian.sd.buz.homepage.banner

import com.sumian.sd.widget.banner.Banner

/**
 * Created by jzz
 *
 * on 2019/1/24
 *
 * desc:
 */
interface BannerContract {

    interface View {
        fun onGetBannerListSuccess(banners: List<Banner>)
        fun onGetBannerListFailed(error: String)
    }

    interface Presenter {
        fun getBannerList()
    }
}