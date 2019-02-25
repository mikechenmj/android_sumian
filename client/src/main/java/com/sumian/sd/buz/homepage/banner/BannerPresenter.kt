package com.sumian.sd.buz.homepage.banner

import com.google.gson.JsonObject
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sd.app.AppManager
import com.sumian.sd.common.network.callback.BaseSdResponseCallback
import com.sumian.sd.widget.banner.Banner

/**
 * Created by jzz
 *
 * on 2019/1/24
 *
 * desc:
 */
class BannerPresenter private constructor(private var view: BannerContract.View?) : BannerContract.Presenter {

    companion object {
        private const val HOME_BANNERS_KEY = "home_banners"
        private const val NAME_KEY = "name"
        private const val VALUE_KEY = "value"

        @JvmStatic
        fun init(view: BannerContract.View?): BannerContract.Presenter = BannerPresenter(view)
    }

    override fun getBannerList() {
        val call = AppManager.getSdHttpService().getConfigs()
        // todo cancel call
        call.enqueue(object : BaseSdResponseCallback<List<JsonObject>>() {
            override fun onSuccess(response: List<JsonObject>?) {
                response?.let {
                    val banners = mutableListOf<Banner>()
                    val list = response.filter { it.get("name").asString == HOME_BANNERS_KEY }
                    if (list.size > 0) {
                        list[0].asJsonArray
                                .forEachIndexed { index, tmpJsonElement ->
                                    banners.add(Banner(index, index, tmpJsonElement.asString, index.toString()))
                                }
                        view?.onGetBannerListSuccess(banners)
                    }
                }
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                view?.onGetBannerListFailed(error = errorResponse.message)
            }
        })

    }
}