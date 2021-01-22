package com.sumian.sd.examine.main.me.userinfo

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import androidx.lifecycle.Observer
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.base.BaseActivity
import com.sumian.common.image.ImageLoader
import com.sumian.common.image.ImagesScopeStorageHelper.contentUriToByte
import com.sumian.common.image.ImagesScopeStorageHelper.generateContentUri
import com.sumian.common.image.ImagesScopeStorageHelper.isContentUriFileExisted
import com.sumian.common.media.SelectImageActivity
import com.sumian.common.media.config.SelectOptions
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sd.R
import com.sumian.sd.app.App.Companion.getAppContext
import com.sumian.sd.app.AppManager.getAccountViewModel
import com.sumian.sd.app.AppManager.getSdHttpService
import com.sumian.sd.buz.account.bean.UserInfo
import com.sumian.sd.buz.account.sheet.ModifySelectBottomSheet
import com.sumian.sd.buz.account.sheet.ModifySelectBottomSheet.Companion.newInstance
import com.sumian.sd.buz.account.userProfile.ImproveUserProfileContract
import com.sumian.sd.buz.account.userProfile.ModifyUserInfoActivity
import com.sumian.sd.common.network.callback.BaseSdResponseCallback
import com.sumian.sd.common.oss.OssEngine
import com.sumian.sd.common.oss.OssEngine.Companion.uploadFile
import com.sumian.sd.common.oss.OssResponse
import com.sumian.sd.widget.sheet.PictureBottomSheet
import com.sumian.sd.widget.sheet.PictureBottomSheet.OnTakePhotoCallback
import kotlinx.android.synthetic.main.examine_user_info.*
import org.json.JSONException
import org.json.JSONObject
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.File

class ExamineUserInfoActivity : BaseActivity(), OnTakePhotoCallback {

    private var cameraFile: File? = null
    private var storageDir: File? = null
    private var mLocalImagePath: String? = null
    private var mContentUri: Uri? = null

    companion object {
        fun show() {
            ActivityUtils.startActivity(ExamineUserInfoActivity::class.java)
        }

        private val TAG = ExamineUserInfoActivity::class.java.simpleName
        private const val imagePathName = "/image/"
        private const val PIC_REQUEST_CODE_CAMERA = 0x02
    }

    override fun getLayoutId(): Int {
        return R.layout.examine_user_info
    }

    override fun initWidget() {
        super.initWidget()
        examine_title_bar.setOnBackClickListener { finish() }
        lay_avatar.setOnClickListener {
            supportFragmentManager
                    .beginTransaction()
                    .add(PictureBottomSheet.newInstance().addOnTakePhotoCallback(this@ExamineUserInfoActivity), PictureBottomSheet::class.java.simpleName)
                    .commitNowAllowingStateLoss()
        }
        lay_nickname.setOnClickListener { ModifyUserInfoActivity.show(this, ImproveUserProfileContract.IMPROVE_NICKNAME_KEY) }
        lay_area.setOnClickListener { commitModifySelectBottomSheet(ImproveUserProfileContract.IMPROVE_AREA_KEY) }
        lay_gender.setOnClickListener { commitModifySelectBottomSheet(ImproveUserProfileContract.IMPROVE_GENDER_KEY) }
        lay_birthday.setOnClickListener { commitModifySelectBottomSheet(ImproveUserProfileContract.IMPROVE_BIRTHDAY_KEY) }
        lay_height.setOnClickListener { commitModifySelectBottomSheet(ImproveUserProfileContract.IMPROVE_HEIGHT_KEY) }
        lay_weight.setOnClickListener { commitModifySelectBottomSheet(ImproveUserProfileContract.IMPROVE_WEIGHT_KEY) }
        lay_career.setOnClickListener { ModifyUserInfoActivity.show(this, ImproveUserProfileContract.IMPROVE_CAREER_KEY) }
        getAccountViewModel().getUserInfoLiveData().observe(this, Observer<UserInfo?> { userInfo ->
            updateUserProfileUI(userInfo)
        })
    }

    private fun commitModifySelectBottomSheet(modifyKey: String) {
        supportFragmentManager
                .beginTransaction()
                .add(newInstance(modifyKey), ModifySelectBottomSheet::class.java.simpleName)
                .commitNowAllowingStateLoss()
    }

    @SuppressLint("SetTextI18n")
    private fun updateUserProfileUI(userProfile: UserInfo?) {
        if (userProfile == null) {
            return
        }
        ImageLoader.loadImage(userProfile.avatar, iv_avatar, R.mipmap.ic_info_avatar_patient)
        if (!userProfile.mobile.isNullOrEmpty()) tv_mobile.text = userProfile.mobile
        if (!userProfile.nickname.isNullOrEmpty()) tv_nickname.text = userProfile.nickname
        if (!userProfile.area.isNullOrEmpty()) tv_area.text = userProfile.area
        if (!userProfile.gender.isNullOrEmpty()) {
            tv_gender.text = when(userProfile.gender) {
                "female" -> "女"
                "male" -> "男"
                else -> "未知"
            }
        }
        if (!userProfile.birthday.isNullOrEmpty()) tv_birthday.text = userProfile.birthday
        if (!userProfile.height.isNullOrEmpty()) tv_height.text = userProfile.height + " cm"
        if (!userProfile.weight.isNullOrEmpty()) tv_weight.text = userProfile.weight + " kg"
        if (!userProfile.career.isNullOrEmpty()) tv_career.text = userProfile.career
    }

    @AfterPermissionGranted(PIC_REQUEST_CODE_CAMERA)
    override fun onTakePhotoCallback() {
        val perms = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (EasyPermissions.hasPermissions(this, *perms)) {
            cameraFile = File(generateImagePath(getAccountViewModel().token!!.user.id.toString(), getAppContext()),
                    getAccountViewModel().token!!.user.id.toString() + System.currentTimeMillis().toString() + ".jpg")
            cameraFile!!.parentFile?.mkdirs()
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            //android 7.1之后的相机处理方式
            if (Build.VERSION.SDK_INT < 24) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile))
                startActivityForResult(intent, PIC_REQUEST_CODE_CAMERA)
            } else {
                mContentUri = generateContentUri(this, cameraFile!!.name, "image/jpeg", false)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mContentUri)
                startActivityForResult(intent, PIC_REQUEST_CODE_CAMERA)
            }
        } else {
            // Request one permission
            EasyPermissions.requestPermissions(this, resources.getString(R.string.str_request_camera_message), PIC_REQUEST_CODE_CAMERA, *perms)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PIC_REQUEST_CODE_CAMERA -> if (isContentUriFileExisted(this, mContentUri!!)) {
                    val values = ContentValues(1)
                    contentResolver.update(mContentUri, values, null, null)
                    uploadAvatar(mContentUri.toString())
                }
                else -> {
                }
            }
        }
    }

    override fun onPicPictureCallback() {
        SelectImageActivity.show(this, SelectOptions.Builder()
                .setHasCam(false)
                .setSelectCount(1)
                .setSelectedImages(arrayOf())
                .setCallback { images: Array<String> ->
                    for (image in images) {
                        uploadAvatar(image.also { mLocalImagePath = it })
                    }
                }.build())
    }

    private fun generateImagePath(userName: String, applicationContext: Context): File? {
        val path: String
        val pathPrefix = "/Android/data/" + applicationContext.packageName + "/"
        path = pathPrefix + userName + imagePathName
        return File(getStorageDir(applicationContext), path)
    }

    private fun getStorageDir(applicationContext: Context): File? {
        if (storageDir == null) {
            //try to use sd card if possible
            val sdPath = Environment.getExternalStorageDirectory()
            if (sdPath.exists()) {
                return sdPath
            }
            //use application internal storage instead
            storageDir = applicationContext.filesDir
        }
        return storageDir
    }

    private fun uploadAvatar(imageUrl: String) {
        val call = getSdHttpService().uploadAvatar()
        addCall(call)
        call.enqueue(object : BaseSdResponseCallback<OssResponse?>() {
            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
            }

            override fun onSuccess(ossResponse: OssResponse?) {
                val callback: OssEngine.UploadCallback = object : OssEngine.UploadCallback {
                    override fun onSuccess(response: String?) {
                        try {
                            if (!TextUtils.isEmpty(response)) {
                                val jsonObject = JSONObject(response)
                                val avatarUrl = jsonObject.getString("avatar")
                                if (!TextUtils.isEmpty(avatarUrl)) {
                                    val userProfile = getAccountViewModel().userInfo
                                    userProfile!!.avatar = avatarUrl
                                    getAccountViewModel().updateUserInfo(userProfile)
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFailure(errorCode: String?, serviceExceptionMessage: String?) {
                    }
                }
                if (imageUrl.startsWith("content://")) {
                    val imageData = contentUriToByte(imageUrl)
                    uploadFile(ossResponse!!, imageData, callback)
                } else {
                    uploadFile(ossResponse!!, imageUrl, callback)
                }
            }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

}