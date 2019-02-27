package com.sumian.sd.buz.homepage.banner

import com.google.gson.JsonArray
import com.google.gson.JsonElement
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
                    for (jsonElement in response) {
                        val jsonObject = jsonElement.asJsonObject
                        if (jsonObject.get(NAME_KEY).asString == HOME_BANNERS_KEY) {
                            val asJsonArray = jsonObject.get(VALUE_KEY).asJsonArray
                            val banners = mutableListOf<Banner>()
                            var banner: Banner
                            asJsonArray.forEachIndexed { index, tmpJsonElement ->
                                val url = tmpJsonElement.asString
                                banner = Banner(id = index, position = index, url = url, text = index.toString())
                                banners.add(banner)
                            }
                            view?.onGetBannerListSuccess(banners)
                            break
                        }
                    }
                }
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                view?.onGetBannerListFailed(error = errorResponse.message)
            }
        })
    }
}