@file:Suppress("PrivatePropertyName", "DEPRECATION")

package com.sumian.sleepdoctor.improve.advisory.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback
import com.alibaba.sdk.android.oss.model.PutObjectRequest
import com.sumian.common.media.SelectImageActivity
import com.sumian.common.media.config.SelectOptions
import com.sumian.sleepdoctor.R
import com.sumian.sleepdoctor.base.ActivityLauncher
import com.sumian.sleepdoctor.base.BaseActivity
import com.sumian.sleepdoctor.improve.advisory.bean.Advisory
import com.sumian.sleepdoctor.improve.advisory.contract.PublishAdvisoryRecordContact
import com.sumian.sleepdoctor.improve.advisory.presenter.PublishAdvisoryRecordPresenter
import com.sumian.sleepdoctor.improve.widget.adapter.SimpleTextWatchAdapter
import com.sumian.sleepdoctor.improve.widget.sheet.PictureBottomSheet
import com.sumian.sleepdoctor.onlinereport.OnlineReport
import com.sumian.sleepdoctor.onlinereport.OnlineReportListActivity
import com.sumian.sleepdoctor.widget.TitleBar
import com.sumian.sleepdoctor.widget.dialog.ActionLoadingDialog
import kotlinx.android.synthetic.main.activity_main_publish_advisory_record.*
import java.util.*
import kotlin.collections.ArrayList

/**
 *
 *Created by sm
 * on 2018/6/8 10:40
 * desc:图文咨询上传
 **/
class PublishAdvisoryRecordActivity : BaseActivity<PublishAdvisoryRecordContact.Presenter>(),
        PublishAdvisoryRecordContact.View, TitleBar.OnBackClickListener,
        TitleBar.OnMenuClickListener, PictureBottomSheet.OnTakePhotoCallback, OSSProgressCallback<PutObjectRequest>, ActivityLauncher {

    private val TAG: String = PublishAdvisoryRecordActivity::class.java.simpleName

    private var mSelectOnlineRecords: ArrayList<OnlineReport>? = null

    private var mAdvisory: Advisory? = null

    private var mPictures: Array<String>? = null

    private var mActionLoadingDialog: ActionLoadingDialog? = null

    companion object {

        private const val ARGS_ADVISORY = "com.sumian.sleepdoctor.extras.advisory"

        private const val PICK_REPORT_REQUEST_CODE = 0x01

        fun launch(context: Context, advisory: Advisory?) {
            val extras = Bundle()
            extras.putParcelable(ARGS_ADVISORY, advisory)
            show(context, PublishAdvisoryRecordActivity::class.java, extras)
        }
    }

    override fun initBundle(bundle: Bundle?): Boolean {
        this.mAdvisory = bundle?.getParcelable(ARGS_ADVISORY)
        return super.initBundle(bundle)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main_publish_advisory_record
    }

    override fun initPresenter() {
        super.initPresenter()
        PublishAdvisoryRecordPresenter.init(this)
    }

    override fun initWidget(root: View?) {
        super.initWidget(root)
        title_bar.setOnBackClickListener(this)
        title_bar.setOnMenuClickListener(this)

        et_input.addTextChangedListener(object : SimpleTextWatchAdapter() {

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)
                et_input.post {
                    tv_input_text_count.text = String.format(Locale.getDefault(), "%d%s%d", s?.length, "/", 500)
                    if (count < 500) {
                        tv_input_text_count.setTextColor(resources.getColor(R.color.t2_color))
                    } else {
                        tv_input_text_count.setTextColor(resources.getColor(R.color.t4_color))
                        showCenterToast("字数超过限定值")
                    }
                }
            }
        })

        publish_pictures_previewer.visibility = View.GONE
        publish_pictures_previewer.showEmptyView {
            lay_picture_place.visibility = View.VISIBLE
            publish_pictures_previewer.visibility = View.GONE
        }
        lay_picture_place.setOnClickListener {

            supportFragmentManager
                    .beginTransaction()
                    .add(PictureBottomSheet.newInstance().addOnTakePhotoCallback(this), PictureBottomSheet::class.java.simpleName)
                    .commitNow()
        }

        lay_report.setOnClickListener {
            OnlineReportListActivity.launchForPick(this, PICK_REPORT_REQUEST_CODE, mSelectOnlineRecords)
        }

    }

    override fun initData() {
        super.initData()
        if (mAdvisory == null) {
            this.mPresenter.getLastAdvisory()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                if (requestCode == PICK_REPORT_REQUEST_CODE) {// pick reports
                    val reports: ArrayList<OnlineReport> = data?.getParcelableArrayListExtra("data")!!
                    mSelectOnlineRecords = reports
                    if (!reports.isEmpty()) {
                        tv_report_count.text = "已选择 ${reports.size} 份"
                        tv_report_count.visibility = View.VISIBLE
                    } else {
                        tv_report_count.visibility = View.INVISIBLE
                    }
                }
            }
        }
    }

    private fun getSelectReportIds(): ArrayList<Int> {
        val reportIds = ArrayList<Int>()
        mSelectOnlineRecords?.forEach {
            reportIds.add(it.id)
        }
        return reportIds
    }

    override fun setPresenter(presenter: PublishAdvisoryRecordContact.Presenter) {
        //super.setPresenter(presenter)
        this.mPresenter = presenter
    }

    override fun onMenuClick(v: View?) {

        val inputContent = et_input.text.toString().trim()

        if (TextUtils.isEmpty(inputContent) || inputContent.length < 10) {
            showCenterToast(R.string.more_than_ten_size)
            return
        }

        if (mPictures == null || mPictures?.isEmpty()!!) {
            this.mPresenter.publishAdvisoryRecord(advisoryId = mAdvisory?.id!!, content = inputContent, onlineReportIds = getSelectReportIds())
        } else {
            this.mPresenter.publishPictureAdvisoryRecord(advisoryId = mAdvisory?.id!!, content = inputContent, onlineReportIds = getSelectReportIds(), pictureCount = mPictures?.size!!)
        }

    }

    override fun onBack(v: View?) {
        finish()
    }

    override fun onBegin() {
        super.onBegin()
        this.mActionLoadingDialog = ActionLoadingDialog().show(supportFragmentManager)
    }

    override fun onFinish() {
        super.onFinish()
        if (this.mActionLoadingDialog?.isAdded!!) {
            this.mActionLoadingDialog?.dismiss()
        }
    }

    override fun onGetLastAdvisorySuccess(advisory: Advisory) {
        this.mAdvisory = advisory
    }

    override fun onGetLastAdvisoryFailed(error: String) {
        showCenterToast(error)
    }

    override fun onPublishAdvisoryRecordSuccess(advisory: Advisory) {
        this.mAdvisory = advisory
        this.mPresenter.getLastAdvisory()
        AdvisoryDetailActivity.launch(this, advisory)
        finish()
    }

    override fun onPublishAdvisoryRecordFailed(error: String) {
        showCenterToast(error)
    }

    override fun onGetPublishUploadStsFailed(error: String) {
        showCenterToast(error)
    }

    override fun onProgress(request: PutObjectRequest?, currentSize: Long, totalSize: Long) {
        Log.e(TAG, "progress=${currentSize * 1.0f / totalSize * 100.0f}")
    }

    override fun onGetPublishUploadStsSuccess(successMsg: String) {
        showCenterToast(successMsg)
        mPresenter.publishImages(mPictures!!, this)
    }

    override fun onStartUploadImagesCallback() {
        this.mActionLoadingDialog = ActionLoadingDialog().show(supportFragmentManager)
        this.mActionLoadingDialog?.isCancelable = false
    }

    override fun onEndUploadImagesCallback() {
        if (this.mActionLoadingDialog?.isAdded!!) {
            this.mActionLoadingDialog?.dismiss()
        }
    }

    override fun onTakePhotoCallback() {
        SelectImageActivity.show(this, SelectOptions.Builder().setHasCam(true).setSelectCount(1).setSelectedImages(mPictures).setCallback {
            it.forEach { Log.e(TAG, it) }

            mPictures = it

            if (it.isNotEmpty()) {
                publish_pictures_previewer.set(it)
                lay_picture_place.visibility = View.GONE
            }

        }.build())

    }

    override fun onPicPictureCallback() {
        this.mPictures = arrayOf()
        SelectImageActivity.show(this, SelectOptions.Builder().setHasCam(false).setSelectCount(9).setSelectedImages(mPictures).setCallback {
            it.forEach { Log.e(TAG, it) }

            mPictures = it

            if (it.isNotEmpty()) {
                publish_pictures_previewer.set(it)
                lay_picture_place.visibility = View.GONE
            }

        }.build())
    }

}