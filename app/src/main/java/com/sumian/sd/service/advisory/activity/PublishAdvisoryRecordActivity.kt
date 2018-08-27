package com.sumian.sd.service.advisory.activity

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
import com.sumian.common.media.SelectImageActivity
import com.sumian.common.media.Util
import com.sumian.common.media.config.SelectOptions
import com.sumian.common.media.widget.PicturesPreviewer
import com.sumian.sd.BuildConfig
import com.sumian.sd.R
import com.sumian.sd.service.advisory.bean.Advisory
import com.sumian.sd.service.advisory.contract.PublishAdvisoryRecordContact
import com.sumian.sd.service.advisory.presenter.PublishAdvisoryRecordPresenter
import com.sumian.sd.service.advisory.utils.AdvisoryContentCacheUtils
import com.sumian.sd.app.App
import com.sumian.sd.app.AppManager
import com.sumian.sd.base.SdBaseActivity
import com.sumian.sd.onlinereport.OnlineReport
import com.sumian.sd.onlinereport.OnlineReportListActivity
import com.sumian.sd.widget.TitleBar
import com.sumian.sd.widget.adapter.SimpleTextWatchAdapter
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
class PublishAdvisoryRecordActivity : SdBaseActivity<PublishAdvisoryRecordContact.Presenter>(),
        PublishAdvisoryRecordContact.View, TitleBar.OnBackClickListener,
        TitleBar.OnMenuClickListener, PictureBottomSheet.OnTakePhotoCallback, OSSProgressCallback<PutObjectRequest>, EasyPermissions.PermissionCallbacks, PicturesPreviewer.OnPreviewerCallback {


    private val TAG: String = PublishAdvisoryRecordActivity::class.java.simpleName

    private var mSelectOnlineRecords: ArrayList<OnlineReport>? = null

    private var mAdvisoryId: Int = 0

    private var mPictures = mutableListOf<String>()

    private var mActionLoadingDialog: ActionLoadingDialog? = null

    private var cameraFile: File? = null

    private val imagePathName = "/image/"

    private var mLocalImagePath: String? = null

    companion object {

        private const val ARGS_ADVISORY = "com.sumian.sleepdoctor.extras.advisory"

        private const val PICK_REPORT_CODE_PHOTO = 0x01

        private const val PIC_REQUEST_CODE_CAMERA = 0x02

        private const val REQUEST_WRITE_PERMISSION = 0x03


        fun show(context: Context, advisoryId: Int) {
            val extras = Bundle()
            extras.putInt(ARGS_ADVISORY, advisoryId)
            show(context, PublishAdvisoryRecordActivity::class.java, extras)
        }
    }

    override fun initBundle(bundle: Bundle?): Boolean {
        bundle?.let {
            this.mAdvisoryId = it.getInt(ARGS_ADVISORY, 0)
        }
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
        if (mAdvisoryId == 0) {
            this.mPresenter.getLastAdvisory()
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

    override fun setPresenter(presenter: PublishAdvisoryRecordContact.Presenter) {
        //super.setPresenter(presenter)
        this.mPresenter = presenter
    }

    override fun onMenuClick(v: View) {

        val inputContent = et_input.text.toString().trim()

        if (TextUtils.isEmpty(inputContent) || inputContent.length < 10) {
            showCenterToast(R.string.more_than_ten_size)
            return
        }

        if (mPictures.isEmpty()) {
            this.mPresenter.publishAdvisoryRecord(advisoryId = mAdvisoryId, content = inputContent, onlineReportIds = getSelectReportIds())
        } else {
            this.mPresenter.publishPictureAdvisoryRecord(advisoryId = mAdvisoryId, content = inputContent, onlineReportIds = getSelectReportIds(), pictureCount = mPictures.size)
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

    private var mAdvisory: Advisory? = null

    override fun onGetLastAdvisorySuccess(advisory: Advisory) {
        this.mAdvisory = advisory
        this.mAdvisoryId = advisory.id
    }

    override fun onGetLastAdvisoryFailed(error: String) {
        showCenterToast(error)
    }

    override fun onPublishAdvisoryRecordSuccess(advisory: Advisory) {
        et_input.post {
            et_input.text = null
        }
        AdvisoryContentCacheUtils.clearCache(advisoryId = advisory.id)
        this.mAdvisory = advisory
        this.mAdvisoryId = advisory.id
        //this.mPresenter.getLastAdvisory()
        AdvisoryDetailActivity.show(this@PublishAdvisoryRecordActivity, advisoryId = advisory.id)
        finish()
    }

    override fun onPublishAdvisoryRecordFailed(error: String) {
        showCenterToast(error)
    }

    override fun onGetPublishUploadStsFailed(error: String) {
        showCenterToast(error)
    }

    override fun onProgress(request: PutObjectRequest?, currentSize: Long, totalSize: Long) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "progress=${currentSize * 1.0f / totalSize * 100.0f}")
        }
    }

    override fun onGetPublishUploadStsSuccess(successMsg: String) {
        showCenterToast(successMsg)
        mPresenter.publishImages(Util.toPathArray(mPictures)!!, this)
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
            it.forEach {
                Log.e(TAG, it)
            }
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
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
                .commitNow()
    }

}