package com.sumian.sd.diary.sleeprecord

import android.content.Intent
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import androidx.core.view.drawToBitmap
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.base.BaseDialogPresenterActivity
import com.sumian.common.image.ImageLoader
import com.sumian.common.mvp.IPresenter
import com.sumian.common.statistic.StatUtil
import com.sumian.common.utils.JsonUtil
import com.sumian.sd.R
import com.sumian.sd.app.AppManager
import com.sumian.sd.diary.sleeprecord.bean.ShareInfo
import com.umeng.socialize.UMShareListener
import com.umeng.socialize.bean.SHARE_MEDIA
import kotlinx.android.synthetic.main.activity_share_sleep_diary_dialog_content_1.*
import kotlinx.android.synthetic.main.activity_share_sleep_diary_dialog_content_2.*

class ShareSleepDiaryDialogActivity : BaseDialogPresenterActivity<IPresenter>() {
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
        val totalDiaries = shareInfo.totalDiaries
        val textSpan = getDescText(totalDiaries)
        tv_day_1.text = totalDiaries.toString()
        tv_day_2.text = totalDiaries.toString()
        tv_desc_1.text = textSpan
        tv_desc_2.text = textSpan
        tv_question_1.text = shareInfo.sleepKnowledge.question
        tv_question_2.text = shareInfo.sleepKnowledge.question
        tv_answer_1.text = shareInfo.sleepKnowledge.answer
        tv_answer_2.text = shareInfo.sleepKnowledge.answer
        ImageLoader.loadImage(shareInfo.officialQrCode, iv_qr)
        iv_weixin.setOnClickListener { checkPermissionForCreateImageAndShare(SHARE_MEDIA.WEIXIN) }
        iv_weixin_cicle.setOnClickListener { checkPermissionForCreateImageAndShare(SHARE_MEDIA.WEIXIN_CIRCLE) }
    }

    private fun getDescText(totalDiaries: Int): SpannableString {
        val text = resources.getString(R.string.today_is_xx_day_fill_sleep_diary, totalDiaries)
        val start = text.indexOf(totalDiaries.toString())
        val end = start + totalDiaries.toString().length
        val textSpan = SpannableString(text)
        textSpan.setSpan(RelativeSizeSpan(1.2f), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        @Suppress("DEPRECATION")
        textSpan.setSpan(ForegroundColorSpan(resources.getColor(R.color.t6_color)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return textSpan
    }

    private fun checkPermissionForCreateImageAndShare(shareMedia: SHARE_MEDIA) {
        val channel = if (shareMedia == SHARE_MEDIA.WEIXIN) "微信会话" else "微信朋友圈"
        StatUtil.event("click_sleep_diary_share", mapOf("channel" to channel))
        AppManager.getOpenEngine().shareImage(this, v_share_sleep_diary_root.drawToBitmap(), shareMedia, object : UMShareListener {
            override fun onResult(p0: SHARE_MEDIA?) {
            }

            override fun onCancel(p0: SHARE_MEDIA?) {
            }

            override fun onError(p0: SHARE_MEDIA?, p1: Throwable?) {
                ToastUtils.showShort(R.string.share_failed)
            }

            override fun onStart(p0: SHARE_MEDIA?) {
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        StatUtil.event("click_sleep_diary_share_cancel")
    }

}