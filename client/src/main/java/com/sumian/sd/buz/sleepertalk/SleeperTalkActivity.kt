package com.sumian.sd.buz.sleepertalk

import android.os.Bundle
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.h5.BaseWebViewActivity
import com.sumian.common.h5.WebViewManger
import com.sumian.common.h5.bean.H5PayloadData
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sd.BuildConfig
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.sleepertalk.bean.SleeperTalkData
import com.sumian.sd.common.h5.H5Uri
import com.sumian.sd.common.network.callback.BaseSdResponseCallback
import com.sumian.sd.widget.ShareBottomSheet
import com.umeng.socialize.UMShareListener
import com.umeng.socialize.bean.SHARE_MEDIA

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/3/13 09:19
 * desc   :
 * version: 1.0
 */
class SleeperTalkActivity : BaseWebViewActivity() {
    private val mEssayId: Int
        get() {
            val id = intent.getIntExtra(KEY_ID, 0)
            return id
        }
    private var mData: SleeperTalkData? = null

    override fun showBackNav(): Boolean {
        return true
    }

    override fun initWidget() {
        super.initWidget()
        mTitleBar.showMoreIcon(R.drawable.ic_nav_share)
        mTitleBar.setOnMenuClickListener { showShareDialog() }
    }

    override fun getCompleteUrl(): String {
        val urlContent = H5Uri.NATIVE_ROUTE
                .replace("{pageData}", H5PayloadData(H5Uri.SLEEPER_TALK_PAGE, mapOf("id" to mEssayId)).toJson())
                .replace("{token}", AppManager.getAccountViewModel().token.token)
        val completeUrl = BuildConfig.BASE_H5_URL + urlContent
        return completeUrl
    }

    override fun initData() {
        super.initData()
        val id = mEssayId
        val call = AppManager.getSdHttpService().getSleeperTalkDetail(id)
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<SleeperTalkData>() {
            override fun onSuccess(response: SleeperTalkData?) {
                mData = response
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
            }
        })
    }

    private fun showShareDialog() {
        if (mData == null) {
            return
        }
        val shareUrl = WebViewManger.getInstance().getBaseUrl() + H5Uri.SLEEPER_TALK_SHARE.replace("{id}", mData!!.id.toString())
        ShareBottomSheet(this)
                .setUrl(shareUrl)
                .setTitle(mData!!.title)
                .setMessage(mData!!.introduction)
                .setIcon(R.drawable.icon_sleeper_talk_share)
                .setListener(object : UMShareListener {
                    override fun onResult(p0: SHARE_MEDIA?) {
                    }

                    override fun onCancel(p0: SHARE_MEDIA?) {
                    }

                    override fun onError(p0: SHARE_MEDIA?, p1: Throwable?) {
                    }

                    override fun onStart(p0: SHARE_MEDIA?) {
                    }
                })
                .show()
    }

    companion object {
        private const val KEY_ID = "KEY_ID"

        fun launch(essayId: Int) {
            val bundle = Bundle()
            bundle.putInt(KEY_ID, essayId)
            ActivityUtils.startActivity(bundle, SleeperTalkActivity::class.java)
        }
    }
}