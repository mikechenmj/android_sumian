package com.sumian.sd.diary.sleeprecord

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.base.BaseDialogPresenterActivity
import com.sumian.common.image.ImageLoader
import com.sumian.common.mvp.IPresenter
import com.sumian.common.utils.JsonUtil
import com.sumian.common.utils.ViewToImageFileListener
import com.sumian.common.utils.viewToImageFile
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.constants.FilePath
import com.sumian.sd.diary.sleeprecord.bean.ShareInfo
import com.umeng.socialize.UMShareListener
import com.umeng.socialize.bean.SHARE_MEDIA
import kotlinx.android.synthetic.main.activity_share_sleep_diary_dialog_content_1.*
import kotlinx.android.synthetic.main.activity_share_sleep_diary_dialog_content_2.*
import java.io.File

class ShareSleepDiaryDialogActivity : BaseDialogPresenterActivity<IPresenter>() {
    private var mSharePlatform = SHARE_MEDIA.WEIXIN

    companion object {
        private const val KEY_SHARE_INFO = "KEY_SHARE_INFO"
        fun start(shareInfo: ShareInfo) {
            LogUtils.d(shareInfo)
            val intent = Intent(ActivityUtils.getTopActivity(), ShareSleepDiaryDialogActivity::class.java)
            intent.putExtra(KEY_SHARE_INFO, shareInfo)
            ActivityUtils.startActivity(intent)
        }

        fun startFotTest() {
            val json = "{\n" +
                    "        \"total_diaries\":1,\n" +
                    "        \"sleep_knowledge\":{\n" +
                    "            \"id\":2,\n" +
                    "            \"question\":\"qqq2\",\n" +
                    "            \"answer\":\"aaa2\"\n" +
                    "        },\n" +
                    "        \"official_qr_code\":\"https://sleep-doctor-dev.oss-cn-shenzhen.aliyuncs.com/official_account/diary_achievement.png\"\n" +
                    "    }"
            val shareInfo = JsonUtil.fromJson(json, ShareInfo::class.java)
            start(shareInfo!!)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_share_sleep_diary_dialog
    }

    override fun initWidget() {
        super.initWidget()
        iv_close.setOnClickListener { finish() }
        val shareInfo = intent.getParcelableExtra<ShareInfo>(KEY_SHARE_INFO)
        tv_day_1.text = shareInfo.totalDiaries.toString()
        tv_day_2.text = shareInfo.totalDiaries.toString()
        tv_desc_1.text = resources.getString(R.string.today_is_xx_day_fill_sleep_diary)
        tv_desc_2.text = resources.getString(R.string.today_is_xx_day_fill_sleep_diary)
        tv_question_1.text = shareInfo.sleepKnowledge.question
        tv_question_2.text = shareInfo.sleepKnowledge.question
        tv_answer_1.text = shareInfo.sleepKnowledge.answer
        tv_answer_2.text = shareInfo.sleepKnowledge.answer
        ImageLoader.loadImage(shareInfo.officialQrCode, iv_qr)

        iv_weixin.setOnClickListener { checkPermissionForCreateImageAndShare(SHARE_MEDIA.WEIXIN) }
        iv_weixin_cicle.setOnClickListener { checkPermissionForCreateImageAndShare(SHARE_MEDIA.WEIXIN_CIRCLE) }
    }

    private fun checkPermissionForCreateImageAndShare(shareMedia: SHARE_MEDIA) {
        mSharePlatform = shareMedia
        if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1000)
        } else {
            createImageAndShare(mSharePlatform)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            createImageAndShare(mSharePlatform)
        }
    }

    private fun createImageAndShare(shareMedia: SHARE_MEDIA) {
        val file = File(FilePath.getExternalCacheDir(), "${System.currentTimeMillis()}.jpg")
        viewToImageFile(v_share_sleep_diary_root, file, 50, object : ViewToImageFileListener {
            override fun onComplete() {
                AppManager.getOpenEngine()
                        .shareImageFile(this@ShareSleepDiaryDialogActivity,
                                file,
                                "",
                                shareMedia,
                                object : UMShareListener {
                                    override fun onResult(p0: SHARE_MEDIA?) {
                                        LogUtils.d()
                                    }

                                    override fun onCancel(p0: SHARE_MEDIA?) {
                                        LogUtils.d()
                                    }

                                    override fun onError(p0: SHARE_MEDIA?, p1: Throwable?) {
                                        LogUtils.d()
                                    }

                                    override fun onStart(p0: SHARE_MEDIA?) {
                                        LogUtils.d()
                                    }
                                })
            }

            override fun onError(t: Throwable) {
                LogUtils.d(t)
                ToastUtils.showShort(R.string.error)
            }
        })
    }
}