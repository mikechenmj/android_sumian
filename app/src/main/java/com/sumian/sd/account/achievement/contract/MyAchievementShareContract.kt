package com.sumian.sd.account.achievement.contract

import android.app.Activity
import android.view.ViewGroup
import com.sumian.common.mvp.IPresenter
import com.sumian.common.utils.ViewToImageFileListener
import com.umeng.socialize.UMShareListener
import com.umeng.socialize.bean.SHARE_MEDIA

/**
 * Created by jzz
 *
 * on 2019/1/22
 *
 * desc:
 */
interface MyAchievementShareContract {

    interface Presenter : IPresenter {
        fun share(activity: Activity, shareType: SHARE_MEDIA = SHARE_MEDIA.WEIXIN, shareView: ViewGroup, umShareListener: UMShareListener? = null)
        fun saveShareView(shareView: ViewGroup, listener: ViewToImageFileListener)
    }
}