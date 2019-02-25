package com.sumian.sd.buz.relaxation

import android.content.Intent
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.base.BaseActivity
import com.sumian.common.h5.WebViewManger
import com.sumian.common.image.ImageLoader
import com.sumian.common.network.response.ErrorResponse
import com.sumian.common.player.CommonAudioPlayer
import com.sumian.common.statistic.StatUtil
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.homepage.sheet.ShareBottomSheet
import com.sumian.sd.buz.relaxation.bean.RelaxationData
import com.sumian.sd.buz.stat.StatConstants
import com.sumian.sd.common.h5.H5Uri
import com.sumian.sd.common.network.callback.BaseSdResponseCallback
import com.sumian.sd.common.utils.StatusBarUtil
import com.umeng.socialize.UMShareListener
import com.umeng.socialize.bean.SHARE_MEDIA
import kotlinx.android.synthetic.main.activity_relaxation_detail.*
import java.util.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/11/27 11:28
 * desc   :
 * version: 1.0
 */
class RelaxationDetailActivity : BaseActivity(){
    private var mRelaxationData: RelaxationData? = null
    private val mBottomContentDefaultHeight by lazy {
        resources.getDimension(R.dimen.relaxation_detail_default_height)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_relaxation_detail
    }

    companion object {
        private val KEY_RELAXATION_ID = RelaxationDetailActivity::class.java.name + "KEY_RELAXATION_ID"
        fun start(relaxationDataId: Int) {
            val intent = Intent(ActivityUtils.getTopActivity(), RelaxationDetailActivity::class.java)
            intent.putExtra(KEY_RELAXATION_ID, relaxationDataId)
            ActivityUtils.startActivity(intent)
        }
    }

    override fun getPageName(): String {
        return StatConstants.page_relaxation_detail
    }

    override fun initWidget() {
        super.initWidget()
        StatusBarUtil.setStatusBarTextColorDark(this, true)
        iv_close.setOnClickListener { onBackPressed() }
        iv_share.setOnClickListener {
            StatUtil.event(StatConstants.click_relaxation_detail_page_share_btn)
            ShareBottomSheet.show(supportFragmentManager, getShareUrl(),
                    "放松训练正在进⾏中",
                    "快来和我⼀起，劝烦恼打个盹～",
                    "放松训练正在进⾏中，快来和我⼀起，劝烦恼打个盹～",
                    mRelaxationData?.icon ?: "",
                    object: UMShareListener {
                        override fun onResult(p0: SHARE_MEDIA?) {

                        }

                        override fun onCancel(p0: SHARE_MEDIA?) {
                        }

                        override fun onError(p0: SHARE_MEDIA?, p1: Throwable?) {
                        }

                        override fun onStart(p0: SHARE_MEDIA?) {
                            StatUtil.event(StatConstants.on_relaxation_detail_page_share_success)
                        }
                    })
        }
        iv_play.setOnClickListener { CommonAudioPlayer.playOrPause() }
        seek_bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                CommonAudioPlayer.seekTo(seekBar!!.progress)
            }
        })
    }

    override fun initData() {
        super.initData()
        CommonAudioPlayer.setStateChangeListener(mPlayStateChangeListener)
        queryData(intent.getIntExtra(KEY_RELAXATION_ID, 0))
    }

    private val mPlayStateChangeListener = object : CommonAudioPlayer.StateListener {
        override fun onPreparing() {
            showPlayBtn(false)
        }

        override fun onPrepared() {
            showPlayBtn(true)
        }

        override fun onProgressChange(progress: Int, total: Int) {
            seek_bar.progress = progress
            seek_bar.max = total
            updateTimeTv(tv_current_time, progress / 1000)
            updateTimeTv(tv_total_time, total / 1000)

        }

        override fun onPlayStatusChange(isPlaying: Boolean) {
            iv_play.isActivated = isPlaying
        }

        override fun onError() {
            ToastUtils.showShort("播放出错")
            CommonAudioPlayer.prepare(this@RelaxationDetailActivity, mRelaxationData!!.audio!!)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        CommonAudioPlayer.release()
    }

    private fun showPlayBtn(show: Boolean) {
        iv_play.visibility = if (show) View.VISIBLE else View.GONE
        iv_loading.visibility = if (!show) View.VISIBLE else View.GONE
    }

    private fun queryData(id: Int) {
        val call = AppManager.getSdHttpService().getRelaxationDetail(id)
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<RelaxationData>() {
            override fun onSuccess(response: RelaxationData?) {
                updateRelaxation(response)
            }

            override fun onFailure(errorResponse: ErrorResponse) {
            }
        })
    }

    private fun updateRelaxation(response: RelaxationData?) {
        mRelaxationData = response ?: return
        tv_relaxation_title.text = mRelaxationData!!.name
        tv_relaxation_desc.text = mRelaxationData!!.description
        ImageLoader.loadImage(mRelaxationData!!.background!!, iv_bg)
        CommonAudioPlayer.prepare(this, mRelaxationData!!.audio!!)
        space.layoutParams.height = vg_root.height - Math.min(mBottomContentDefaultHeight.toInt(), vg_content.height)
    }

    private fun getShareUrl(): String {
        val sb = StringBuilder()
        sb.append(WebViewManger.getInstance().getBaseUrl())
                .append(H5Uri.CBTI_RELAXATIONS_SHARE.replace("{id}", mRelaxationData!!.id.toString()))
        val url = sb.toString()
        LogUtils.d("load url: %s", url)
        return url
    }

    private fun formatDuration(duration: Int): String {
        val min = duration / 60
        val sec = duration % 60
        return String.format(Locale.getDefault(), "%02d:%02d", min, sec)
    }

    private fun updateTimeTv(tv: TextView, duration: Int) {
        tv.text = formatDuration(duration)
    }
}