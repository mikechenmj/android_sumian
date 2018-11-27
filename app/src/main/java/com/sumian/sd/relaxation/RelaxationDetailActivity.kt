package com.sumian.sd.relaxation

import android.content.Intent
import android.widget.SeekBar
import android.widget.TextView
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.base.BasePresenterActivity
import com.sumian.common.h5.WebViewManger
import com.sumian.common.image.ImageLoader
import com.sumian.common.mvp.IPresenter
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.h5.H5Uri
import com.sumian.sd.homepage.sheet.RelaxationShareBottomSheet
import com.sumian.sd.network.callback.BaseSdResponseCallback
import com.sumian.sd.relaxation.bean.RelaxationData
import com.sumian.sd.utils.StatusBarUtil
import kotlinx.android.synthetic.main.activity_relaxation_detail.*
import java.util.*

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/11/27 11:28
 * desc   :
 * version: 1.0
 */
class RelaxationDetailActivity : BasePresenterActivity<IPresenter>() {
    private var mRelaxationData: RelaxationData? = null

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

    override fun initWidget() {
        super.initWidget()
        StatusBarUtil.setStatusBarTextColorDark(this, true)
        iv_close.setOnClickListener { onBackPressed() }
        iv_share.setOnClickListener { RelaxationShareBottomSheet.show(supportFragmentManager, getShareUrl(), "放松训练", mRelaxationData!!.name) }
        iv_play.setOnClickListener { ToastUtils.showShort("play") }
        seek_bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateTimeTv(tv_current_time, progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }

    override fun initData() {
        super.initData()
        val id = intent.getIntExtra(KEY_RELAXATION_ID, 0)
        queryData(id)
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
        updateTimeTv(tv_current_time, 0)
        updateTimeTv(tv_total_time, 1000)
        tv_relaxation_title.text = mRelaxationData!!.name
        tv_relaxation_desc.text = mRelaxationData!!.description
        ImageLoader.loadImage(mRelaxationData!!.background!!, iv_bg)
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