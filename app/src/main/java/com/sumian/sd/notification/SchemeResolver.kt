package com.sumian.sd.notification

import android.content.Context
import android.content.Intent
import com.sumian.common.notification.ISchemeResolver
import com.sumian.common.notification.SchemeResolveUtil
import com.sumian.sd.notification.push.schemeResolver.*

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
            "online-reports" -> OnlineReportSchemeResolver().resolveScheme(context, uri)
            "refund" -> RefundSchemeResolver().resolveScheme(context, uri)
            "advisories" -> AdvisoriesSchemeResolver().resolveScheme(context, uri)
            "scale-distributions" -> ScaleSchemeResolver().resolveScheme(context, uri)
            "referral-notice", "life-notice" -> NotificationSchemeResolver().resolveScheme(context, uri)
            "cbti-chapters" -> CbtiChapterSchemeResolver().resolveScheme(context, uri)
            "cbti-final-reports" -> CbtiFinalReportSchemeResolver().resolveScheme(context, uri)
            "relaxations" -> RelaxationSchemeResolver().resolveScheme(context, uri)
            "anxieties-and-faiths" -> AnxietyFaithReminderSchemeResolver().resolveScheme(context, uri)
            "advisory-list" -> AdvisoryListSchemeResolver().resolveScheme(context, uri)
            "booking-list" -> BookingListSchemeResolver().resolveScheme(context, uri)
            "diary-evaluation-list" -> DiaryEvaluationListSchemeResolver().resolveScheme(context, uri)
            "booking-detail" -> TelBookingDetailSchemeResolver().resolveScheme(context, uri)
            "diary-evaluations" -> DiaryEvaluationSchemeResolver().resolveScheme(context, uri)
            "message-boards" -> null
            else -> null
        }
    }
}