package com.sumian.sd.diary.sleeprecord

import android.content.Intent
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.sumian.common.base.BaseDialogPresenterActivity
import com.sumian.common.mvp.IPresenter
import com.sumian.sd.R
import com.sumian.sd.diary.sleeprecord.bean.ShareInfo
import com.sumian.sd.homepage.SleepGuideDialogActivity

class ShareSleepDiaryDialogActivity : BaseDialogPresenterActivity<IPresenter>() {

    companion object {
        private const val KEY_SHARE_INFO = "KEY_SHARE_INFO"
        fun start(shareInfo: ShareInfo) {
            LogUtils.d(shareInfo)
            val intent = Intent(ActivityUtils.getTopActivity(), SleepGuideDialogActivity::class.java)
            intent.putExtra(KEY_SHARE_INFO, shareInfo)
            ActivityUtils.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_share_sleep_diary_dialog
    }

    override fun initWidget() {
        super.initWidget()

        val shareInfo = intent.getParcelableExtra<ShareInfo>(KEY_SHARE_INFO)
    }

}