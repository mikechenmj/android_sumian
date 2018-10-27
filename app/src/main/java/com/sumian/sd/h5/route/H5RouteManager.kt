package com.sumian.sd.h5.route

import android.content.Context
import android.content.Intent
import com.sumian.hw.utils.JsonUtil
import com.sumian.sd.h5.SimpleWebActivity

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     time   : 2018/9/4 14:49
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class H5RouteManager private constructor() {
    companion object {
        /**
         *  page:"onlineReport",      // page name
         *  data:{                    // page data, 数据结构随着 page 变化而变化
         *          "id":1,
         *          "type":0
         *      }
         *  }
         */
        fun launch(context: Context, page: String, data: Map<String, Any>) {
            val map = HashMap<String, Any>()
            map.put("page", page)
            map.put("payload", data)
            launch(context, map)
        }

        /**
         * map: 包含 page 和 data
         *  {
         *  "page":"onlineReport",      // page name
         *  "data":{                    // page data, 数据结构随着 page 变化而变化
         *          "id":1,
         *          "type":0
         *      }
         *  }
         */
        private fun launch(context: Context, map: Map<String, Any>) {
            SimpleWebActivity.launchWithRouteData(context, JsonUtil.toJson(map))
        }

        fun getLaunchIntent(context: Context, map: Map<String, Any>): Intent? {
            return SimpleWebActivity.getLaunchIntentWithRouteData(context, JsonUtil.toJson(map))
        }
    }
}