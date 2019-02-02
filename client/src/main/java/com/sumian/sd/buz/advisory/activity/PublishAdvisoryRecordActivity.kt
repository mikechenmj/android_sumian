package com.sumian.sd.buz.advisory.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback
import com.alibaba.sdk.android.oss.model.PutObjectRequest
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.base.BaseViewModelActivity
import com.sumian.common.media.SelectImageActivity
import com.sumian.common.media.Util
import com.sumian.common.media.config.SelectOptions
import com.sumian.common.media.widget.PicturesPreviewer
import com.sumian.common.widget.adapter.EmptyTextWatcher
import com.sumian.sd.R
import com.sumian.sd.app.App
import com.sumian.sd.app.AppManager
import com.sumian.sd.buz.advisory.bean.Advisory
import com.sumian.sd.buz.advisory.presenter.PublishAdvisoryRecordPresenter
import com.sumian.sd.buz.advisory.utils.AdvisoryContentCacheUtils
import com.sumian.sd.buz.onlinereport.OnlineReport
import com.sumian.sd.buz.onlinereport.OnlineReportListActivity
import com.sumian.sd.buz.tel.widget.ServiceSuccessStateView
import com.sumian.sd.widget.TitleBar
import com.sumian.sd.widget.dialog.ActionLoadingDialog
import com.sumian.sd.widget.sheet.PictureBottomSheet
import kotlinx.android.synthetic.main.activity_main_publish_advisory_record.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

@Suppress("DEPRECATION")
/**
 *
 *Created by sm
 * on 2018/6/8 10:40
 * desc:图文咨询上传
 **/
class PublishAdvisoryRecordActivity : BaseViewModelActivity<PublishAdvisoryRecordPresenter>(), TitleBar.OnBackClickListener,
        TitleBar.OnMenuClickListener, PictureBottomSheet.OnTakePhotoCallback, OSSProgressCallback<PutObjectRequest>, EasyPermissions.PermissionCallbacks, PicturesPreviewer.OnPreviewerCallback {

    private var mSelectOnlineRecords: ArrayList<OnlineReport>? = null

    private var mAdvisoryId: Int = 0
    private var mIsAskAgain = false

    private var mPictures = mutableListOf<String>()

    private var mActionLoadingDialog: ActionLoadingDialog? = null

    private var cameraFile: File? = null

    private val imagePathName = "/image/"

    private var mLocalImagePath: String? = null

    companion object {

        private val TAG: String = PublishAdvisoryRecordActivity::class.java.simpleName

        const val INVALID_ADVISORY_ID = 0

        private const val ARGS_ADVISORY = "com.sumian.sleepdoctor.extras.advisory"
        private const val ARGS_ADVISORY_ACTION = "com.sumian.sleepdoctor.extras.advisory.action"

        private const val PICK_REPORT_CODE_PHOTO = 0x01

        private const val PIC_REQUEST_CODE_CAMERA = 0x02

        private const val REQUEST_WRITE_PERMISSION = 0x03

        @JvmStatic
        fun show(context: Context, advisoryId: Int = INVALID_ADVISORY_ID, isAskAgain: Boolean = false) {
            val extras = Bundle().apply {
                putInt(ARGS_ADVISORY, advisoryId)
                putBoolean(ARGS_ADVISORY_ACTION, isAskAgain)
            }
            ActivityUtils.startActivity(extras, PublishAdvisoryRecordActivity::class.java)
        }
    }

    override fun initBundle(bundle: Bundle) {
        bundle.let {
            this.mAdvisoryId = it.getInt(ARGS_ADVISORY, INVALID_ADVISORY_ID)
            this.mIsAskAgain = it.getBoolean(ARGS_ADVISORY_ACTION, false)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main_publish_advisory_record
    }


    override fun initWidget() {
        super.initWidget()
        PublishAdvisoryRecordPresenter.init(this)
        title_bar.setOnBackClickListener(this)
        title_bar.setOnMenuClickListener(this)

        et_input.addTextChangedListener(object : EmptyTextWatcher() {

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)
                et_input.post {
                    val inputLength = s?.length ?: 0
                    if (inputLength > 500) {
                        tv_input_text_count.setTextColor(resources.getColor(R.color.t4_color))
                    } else {
                        tv_input_text_count.setTextColor(resources.getColor(R.color.t2_color))
                    }
                    tv_input_text_count.text = String.format(Locale.getDefault(), "%d%s%d", inputLength, "/", 500)
                }
            }
        })

        publish_pictures_previewer.visibility = View.GONE
        publish_pictures_previewer.setOnPreviewerCallback(this)
        publish_pictures_previewer.showEmptyView {
            lay_picture_place.visibility = View.VISIBLE
            publish_pictures_previewer.visibility = View.GONE
        }
        lay_picture_place.setOnClickListener {
            showPictureBottomSheet()
        }

        lay_report.setOnClickListener {
            OnlineReportListActivity.launchForPick(this, PICK_REPORT_CODE_PHOTO, mSelectOnlineRecords)
        }
    }

    override fun initData() {
        super.initData()
        if (mAdvisoryId <= 0) {
            this.mViewModel?.getLastAdvisory()
        }
        requestWritePermissions()
    }

    @AfterPermissionGranted(REQUEST_WRITE_PERMISSION)
    private fun requestWritePermissions() {
        val perms = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (EasyPermissions.hasPermissions(this, *perms)) {
            Log.e(TAG, "已经有 sd 卡读写权限")
        } else {
            // Request one permission
            Log.e(TAG, "没有 sd 卡读写权限")
            EasyPermissions.requestPermissions(this, "请求获取照片的访问权限", REQUEST_WRITE_PERMISSION, *perms)
        }
    }

    override fun onResume() {
        super.onResume()
        if (mAdvisoryId != 0) {
            val cacheContent = AdvisoryContentCacheUtils.checkAndLoadCacheContent(mAdvisoryId)
            cacheContent?.let {
                et_input.setText(it)
                et_input.setSelection(it.length)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (mAdvisoryId != 0) {
            AdvisoryContentCacheUtils.saveContent2Cache(mAdvisoryId, et_input.text.toString().trim())
        }
    }

    fun setPresenter(presenter: PublishAdvisoryRecordPresenter) {
        //super.setPresenter(presenter)
        this.mViewModel = presenter
    }

    override fun onMenuClick(v: View) {

        val inputContent = et_input.text.toString().trim()

        if (TextUtils.isEmpty(inputContent) || inputContent.length < 10) {
            ToastUtils.showShort(R.string.more_than_ten_size)
            return
        }

        if (inputContent.length > 500) {
            ToastUtils.showShort(getString(R.string.max_input_error))
            return
        }

        if (mPictures.isEmpty()) {
            this.mViewModel?.publishAdvisoryRecord(advisoryId = mAdvisoryId, content = inputContent, onlineReportIds = getSelectReportIds())
        } else {
            this.mViewModel?.publishPictureAdvisoryRecord(advisoryId = mAdvisoryId, content = inputContent, onlineReportIds = getSelectReportIds(), pictureCount = mPictures.size)
        }

    }

    override fun onBack(v: View?) {
        finish()
    }

    fun onBegin() {
        this.mActionLoadingDialog = ActionLoadingDialog().show(supportFragmentManager)
    }

    fun onFinish() {
        if (this.mActionLoadingDialog?.isAdded!!) {
            this.mActionLoadingDialog?.dismiss()
        }
    }

    private var mAdvisory: Advisory? = null

    fun onGetLastAdvisorySuccess(advisory: Advisory) {
        this.mAdvisory = advisory
        this.mAdvisoryId = advisory.id
    }

    fun onGetLastAdvisoryFailed(error: String) {
        ToastUtils.showShort(error)
    }

    fun onPublishAdvisoryRecordSuccess(advisory: Advisory) {
        et_input.post {
            et_input.text = null
        }
        AdvisoryContentCacheUtils.clearCache(advisoryId = advisory.id)
        this.mAdvisory = advisory
        this.mAdvisoryId = advisory.id
        //this.mViewModel.getLastAdvisory()

        title_bar.hideMore().more.visibility = View.INVISIBLE

        if (mIsAskAgain) {//追问，直接进入详情页
            AdvisoryDetailActivity.show(this@PublishAdvisoryRecordActivity, advisoryId = advisory.id)
            finish()
        } else {//不是追问
            runOnUiThread {
                service_state_view.setOnServiceSuccessCallback(object : ServiceSuccessStateView.OnServiceSuccessCallback {
                    override fun showServiceDetailCallback() {
                        AdvisoryDetailActivity.show(this@PublishAdvisoryRecordActivity, advisoryId = advisory.id)
                        finish()
                    }

                    override fun goBackHome() {
                        finish()
                    }
                }).show()
            }
        }
    }

    fun onPublishAdvisoryRecordFailed(error: String) {
        ToastUtils.showShort(error)
    }

    fun onGetPublishUploadStsFailed(error: String) {
        ToastUtils.showShort(error)
    }

    override fun onProgress(request: PutObjectRequest?, currentSize: Long, totalSize: Long) {
    }

    fun onGetPublishUploadStsSuccess(successMsg: String) {
        ToastUtils.showShort(successMsg)
        mViewModel?.publishImages(Util.toPathArray(mPictures)!!, this)
    }

    fun onStartUploadImagesCallback() {
        this.mActionLoadingDialog = ActionLoadingDialog().show(supportFragmentManager)
        this.mActionLoadingDialog?.isCancelable = false
    }

    fun onEndUploadImagesCallback() {
        if (this.mActionLoadingDialog?.isAdded!!) {
            this.mActionLoadingDialog?.dismiss()
        }
    }

    @AfterPermissionGranted(PIC_REQUEST_CODE_CAMERA)
    override fun onTakePhotoCallback() {
        val perms = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
        if (EasyPermissions.hasPermissions(this, *perms)) {

            cameraFile = File(generateImagePath(AppManager.getAccountViewModel().token.user.id.toString(), App.getAppContext()), (AppManager.getAccountViewModel().token.user.id + System.currentTimeMillis()).toString() + ".jpg")

            cameraFile?.parentFile?.mkdirs()

            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            //android 7.1之后的相机处理方式
            if (Build.VERSION.SDK_INT < 24) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile))
                startActivityForResult(intent, PIC_REQUEST_CODE_CAMERA)
            } else {
                val contentValues = ContentValues(1)
                contentValues.put(MediaStore.Images.Media.DATA, cameraFile?.absolutePath)
                val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                startActivityForResult(intent, PIC_REQUEST_CODE_CAMERA)
            }
        } else {
            // Request one permission
            EasyPermissions.requestPermissions(this, resources.getString(R.string.str_request_camera_message), PIC_REQUEST_CODE_CAMERA, *perms)
        }
    }

    override fun onPicPictureCallback() {
        SelectImageActivity.show(this, SelectOptions.Builder().setHasCam(false).setSelectCount(9).setSelectedImages(publish_pictures_previewer.paths).setCallback { it ->
            mPictures = it.toMutableList()
            if (it.isNotEmpty()) {
                publish_pictures_previewer.set(Util.toPathArray(mPictures))
                lay_picture_place.visibility = View.GONE
            }
        }.build())
    }

    override fun onLoadMore() {
        showPictureBottomSheet()
    }

    override fun onClearPicture() {
        if (mPictures.isNotEmpty()) {
            mPictures.clear()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {

    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                when (requestCode) {
                    PICK_REPORT_CODE_PHOTO -> {// pick reports
                        data?.let {
                            val reports: ArrayList<OnlineReport> = it.getParcelableArrayListExtra("data")
                            mSelectOnlineRecords = reports
                            if (!reports.isEmpty()) {
                                tv_report_count.text = "已选择 ${reports.size} 份"
                                tv_report_count.visibility = View.VISIBLE
                            } else {
                                tv_report_count.visibility = View.INVISIBLE
                            }
                        }
                    }
                    PIC_REQUEST_CODE_CAMERA -> {// capture new image
                        cameraFile?.let { it ->

                            if (!it.exists()) {
                                return@let
                            }

                            this.mLocalImagePath = it.absolutePath

                            publish_pictures_previewer.paths?.let {
                                mPictures = it.toMutableList()
                            }

                            mLocalImagePath?.let {
                                mPictures.add(it)
                            }

                            if (mPictures.isNotEmpty()) {
                                publish_pictures_previewer.set(Util.toPathArray(mPictures)!!)
                                lay_picture_place.visibility = View.GONE
                            }
                        }
                    }
                }
            }
        }
    }

    private fun generateImagePath(userName: String, applicationContext: Context): File {
        val path: String
        val pathPrefix = "/Android/data/" + applicationContext.packageName + "/"
        path = pathPrefix + userName + imagePathName
        return File(getStorageDir(applicationContext), path)
    }

    private fun getStorageDir(applicationContext: Context): File {
        //try to use sd card if possible
        val sdPath = Environment.getExternalStorageDirectory()
        if (sdPath.exists()) {
            return sdPath
        }
        //use application internal storage instead
        val storageDir: File? = applicationContext.filesDir
        return storageDir!!
    }

    private fun getSelectReportIds(): ArrayList<Int> {
        val reportIds = ArrayList<Int>()
        mSelectOnlineRecords?.forEach {
            reportIds.add(it.id)
        }
        return reportIds
    }

    private fun showPictureBottomSheet() {
        supportFragmentManager
                .beginTransaction()
                .add(PictureBottomSheet.newInstance().addOnTakePhotoCallback(this), PictureBottomSheet::class.java.simpleName)
                .commitNowAllowingStateLoss()
    }

}