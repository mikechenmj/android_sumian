package com.sumian.sd.buz.notification

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.sumian.common.notification.ISchemeResolver
import com.sumian.common.notification.SchemeResolveUtil
import com.sumian.sd.R
import com.sumian.sd.buz.advisory.activity.AdvisoryDetailActivity
import com.sumian.sd.buz.advisory.activity.AdvisoryListActivity
import com.sumian.sd.buz.anxiousandfaith.AnxiousAndFaithActivity
import com.sumian.sd.buz.cbti.activity.CBTIIntroductionActivity
import com.sumian.sd.buz.cbti.activity.CBTIMessageBoardDetailActivity
import com.sumian.sd.buz.diary.sleeprecord.SleepRecordActivity
import com.sumian.sd.buz.diaryevaluation.DiaryEvaluationDetailActivity
import com.sumian.sd.buz.diaryevaluation.DiaryEvaluationListActivity
import com.sumian.sd.buz.onlinereport.OnlineReportDetailActivity
import com.sumian.sd.buz.relaxation.RelaxationListActivity
import com.sumian.sd.buz.scale.ScaleDetailActivity
import com.sumian.sd.buz.tel.activity.TelBookingDetailActivity
import com.sumian.sd.buz.tel.activity.TelBookingListActivity
import com.sumian.sd.common.h5.SimpleWebActivity
import java.util.*

@Suppress("UNUSED_PARAMETER")
/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/11/21 22:19
 * desc   :
 * version: 1.0
 */
object SchemeResolver : ISchemeResolver {
    override fun schemeResolver(context: Context, scheme: String): Intent? {
        val uri = SchemeResolveUtil.stringToUri(scheme)
        return when (uri.host) {
            "not-jump" -> null
            "diaries" -> resolveDiaryScheme(context, uri)
            "online-reports" -> resolveOnlineReportScheme(context, uri)
            "refund" -> resolveRefundScheme(context, uri)
            "advisories" -> resolveAdvisoriesScheme(context, uri)
            "scale-distributions" -> resolveScaleScheme(context, uri)
            "referral-notice", "life-notice" -> resolveNotificationScheme(context, uri)
            "cbti-chapters" -> resolveCbtiChapterScheme(context, uri)
            "cbti-final-reports" -> resolveCbtiFinalReportScheme(context, uri)
            "relaxations" -> resolveRelaxationScheme(context, uri)
            "anxieties-and-faiths" -> resolveAnxietyFaithReminderScheme(context, uri)
            "advisory-list" -> resolveAdvisoryListScheme(context, uri)
            "booking-list" -> resolveBookingListScheme(context, uri)
            "diary-evaluation-list" -> resolveDiaryEvaluationListScheme(context, uri)
            "booking-detail" -> resolveTelBookingDetailScheme(context, uri)
            "diary-evaluations" -> resolveDiaryEvaluationScheme(context, uri)
            "message-boards" -> resolveMessageBoards(context, uri)
            "system-notifications-detail" -> resolveSystemNotificationsDetail(context, uri)
            else -> null
        }
    }

    private fun resolveSystemNotificationsDetail(context: Context, uri: Uri): Intent? {
        return SystemNotificationDetailActivity.getIntent(context,
                uri.getQueryParameter("notice_id")?.toInt() ?: 0)
    }

    private fun resolveMessageBoards(context: Context, uri: Uri): Intent? {
        val date = uri.getQueryParameter("id")
        return CBTIMessageBoardDetailActivity.getIntent(context, date?.toInt() ?: 0)
    }

    /**
     * "scheme" => 'sleepdoctor://diaries?date=1525763199&notification_id=f7c63f71-1298-49a1-9320-6985eb4bcf7c&user_id=1',   //urlencode后
     */
    fun resolveDiaryScheme(context: Context, uri: Uri): Intent {
        val date = uri.getQueryParameter("date")
        val dateInMills = date!!.toInt() * 1000L
        return SleepRecordActivity.getLaunchIntent(context, dateInMills)
    }

    /**
     * "scheme" =>  sleepdoctor://diary-evaluations?id=91&notification_id=c9b459ca-6a81-4ad8-99f3-2b2b6a06ffc2&user_id=2939
     */
    fun resolveDiaryEvaluationScheme(context: Context, uri: Uri): Intent {
        val data = uri.getQueryParameter("id")!!.toInt()
        return DiaryEvaluationDetailActivity.getLaunchIntent(context, data)
    }

    /**
     *  "scheme": "sleepdoctor://booking-detail?id=13646&plan_start_at=0&notification_id=d54211aa-cbd1-476e-838a-a00eec21801a&user_id=2554" //urlencode后
     */
    fun resolveTelBookingDetailScheme(context: Context, uri: Uri): Intent {
        val data = uri.getQueryParameter("id")!!.toInt()
        return TelBookingDetailActivity.getLaunchIntent(data)
    }

    /**
     * "scheme" =>  "sleepdoctor://diary-evaluation-list?type=0&notification_id=6e9ea5a4-8559-45ca-a5e4-9b495d5ebb2f&user_id=2040" //urlencode后
     */
    fun resolveDiaryEvaluationListScheme(context: Context, uri: Uri): Intent {
        val data = uri.getQueryParameter("type")!!.toInt()
        return DiaryEvaluationListActivity.getLaunchIntent(data)
    }

    /**
    "scheme" => "sleepdoctor://booking-list?list_type=0&notification_id=6e9ea5a4-8559-45ca-a5e4-9b495d5ebb2f&user_id=2040" //urlencode后
     */
    fun resolveBookingListScheme(context: Context, uri: Uri): Intent {
        val data = uri.getQueryParameter("list_type")!!.toInt()
        return TelBookingListActivity.getLaunchIntent(data)
    }

    /**
    "scheme" => "sleepdoctor://advisory-list?type=0&notification_id=6e9ea5a4-8559-45ca-a5e4-9b495d5ebb2f&user_id=2040" //urlencode后
     */
    fun resolveAdvisoryListScheme(context: Context, uri: Uri): Intent {
        val data = uri.getQueryParameter("type")!!.toInt()
        return AdvisoryListActivity.getLaunchIntent(data)
    }

    /**
     * sleepdoctor://anxietiesAndFaiths?user_id=2102
     */
    fun resolveAnxietyFaithReminderScheme(context: Context, uri: Uri): Intent {
        return AnxiousAndFaithActivity.getLaunchIntent()
    }

    /**
     * sleepdoctor://relaxations?user_id=2102
     */
    fun resolveRelaxationScheme(context: Context, uri: Uri): Intent {
        return RelaxationListActivity.getLaunchIntent()
    }

    /**
     * "scheme": "sleepdoctor://cbti-final-reports?scale_distribution_ids=1,2,3&cbti_id=1&chapter_id=1&notification_id=6e9ea5a4-8559-45ca-a5e4-9b495d5ebb2f&user_id=2102"
     */
    fun resolveCbtiFinalReportScheme(context: Context, uri: Uri): Intent {
        val payloadMap = HashMap<String, Any?>(3)
        payloadMap["scale_id"] = uri.getQueryParameter("scale_distribution_ids")
        payloadMap["cbti_id"] = uri.getQueryParameter("cbti_id")
        payloadMap["chapter_id"] = uri.getQueryParameter("chapter_id")
        return SimpleWebActivity.getLaunchIntentWithRouteData(context, "openCbtiScales", payloadMap)
    }

    /**
     * sleepdoctor://cbti-chapters?notification_id=8e194802-a2bb-47f4-a695-f03ccb5d92ad&user_id=2172&cbti_chapter_id=2"  //urlencode后
     */
    fun resolveCbtiChapterScheme(context: Context, uri: Uri): Intent {
//        val data = uri.getQueryParameter("cbti_chapter_id")
//        return CBTIWeekCoursePartActivity.getLaunchIntent(context, chapterId = data!!.toInt())
        return CBTIIntroductionActivity.getLaunchIntent(context)
    }

    /**
    医生随访提醒 - 复诊提醒
    "scheme" => 'sleepdoctor://referral-notice?id=1&notification_id=f7c63f71-1298-49a1-9320-6985eb4bcf7c',   //urlencode后
    医生随访提醒 - 生活提醒
    "scheme" => 'sleepdoctor://life-notice?id=1&notification_id=f7c63f71-1298-49a1-9320-6985eb4bcf7c',   //urlencode后
     */
    fun resolveNotificationScheme(context: Context, uri: Uri): Intent {
        return NotificationListActivity.getLaunchIntent(context)
    }

    /**
    医生发送了新的量表
    "scheme" => 'sleepdoctor://scale-distributions?id=1&notification_id=f7c63f71-1298-49a1-9320-6985eb4bcf7c&user_id=1',   //urlencode后
     */
    fun resolveScaleScheme(context: Context, uri: Uri): Intent {
        val data = uri.getQueryParameter("id")
        val title = context.getString(R.string.record_weekly_report) // td 让服务器在 scheme 加上 title 字段
        return ScaleDetailActivity.getLaunchIntent(context, title, data!!.toLong(), 0L)
    }

    /**
    图文咨询-医生回复
    "scheme" => 'sleepdoctor://advisories?id=1&notification_id=f7c63f71-1298-49a1-9320-6985eb4bcf7c&user_id=1',   //urlencode后
     */
    fun resolveAdvisoriesScheme(context: Context, uri: Uri): Intent {
        val data = uri.getQueryParameter("id")
        return AdvisoryDetailActivity.getLaunchIntent(context, data!!.toInt())
    }

    /**
    退款成功通知
    "scheme" => 'sleepdoctor://refund?order_no=1525763199&notification_id=f7c63f71-1298-49a1-9320-6985eb4bcf7c&user_id=1',   //urlencode后
     */
    fun resolveRefundScheme(context: Context, uri: Uri): Intent {
        val data = uri.getQueryParameter("order_no")
        return RefundActivity.getLaunchIntent(context, data!!)
    }

    /**
    电子报告更新
    "scheme" => 'sleepdoctor://online-reports?id=1&url=www.baidu.com&notification_id=9f3f9091-ab98-421c-ac2c-47709c80ba16&user_id=1',   //urlencode后
     */
    fun resolveOnlineReportScheme(context: Context, uri: Uri): Intent {
        val id = uri.getQueryParameter("id")?.toInt() ?: 0
        return OnlineReportDetailActivity.getLaunchIntent(context, id)
    }

}