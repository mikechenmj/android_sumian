package com.sumian.sd.buz.sleepertalk

import android.os.Bundle
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.base.BaseActivity
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.sleepertalk.bean.SleeperTalkData
import com.sumian.sd.common.network.callback.BaseSdResponseCallback
import com.sumian.sd.widget.ShareBottomSheet
import com.umeng.socialize.UMShareListener
import com.umeng.socialize.bean.SHARE_MEDIA
import kotlinx.android.synthetic.main.activity_sleeper_talk.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/3/13 09:19
 * desc   :
 * version: 1.0
 */
class SleeperTalkActivity : BaseActivity() {
    private val mEssayId: Int
        get() {
            val id = intent.getIntExtra(KEY_ID, 0)
            return id
        }
    private var mData: SleeperTalkData? = null

    override fun getLayoutId(): Int {
        return R.layout.activity_sleeper_talk
    }

    override fun showBackNav(): Boolean {
        return true
    }

    override fun initWidget() {
        super.initWidget()
        mTitleBar.showMoreIcon(R.drawable.ic_nav_share)
        mTitleBar.setOnMenuClickListener { showShareDialog() }
        iv_like.setOnClickListener { onLikeClick() }
    }

    override fun initData() {
        super.initData()
        val id = mEssayId
        val call = AppManager.getSdHttpService().getSleeperTalkDetail(id)
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<SleeperTalkData>() {
            override fun onSuccess(response: SleeperTalkData?) {
                mData = response
                updateUI(response ?: return)
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
            }
        })
    }

    private fun onLikeClick() {
        if (iv_like.isSelected) {
            cancelLike()
        } else {
            like()
        }

    }

    private fun like() {
        val call = AppManager.getSdHttpService().likeSleeperTalk(mEssayId)
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<Any>() {
            override fun onSuccess(response: Any?) {
                iv_like.isSelected = !iv_like.isSelected
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
            }
        })
    }

    private fun cancelLike() {
        val call = AppManager.getSdHttpService().cancelLikeSleeperTalk(mEssayId)
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<Any>() {
            override fun onSuccess(response: Any?) {
                iv_like.isSelected = !iv_like.isSelected
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
        ShareBottomSheet(this)
                .setUrl("http://www.baidu.com")
                .setTitle("essay")
                .setMessage("message")
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

    private fun updateUI(data: SleeperTalkData) {
        setTitle(data.title)
        web_view.loadData(data.content, "text/html; charset=utf-8", "UTF-8")
        iv_like.isSelected = data.isLike
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