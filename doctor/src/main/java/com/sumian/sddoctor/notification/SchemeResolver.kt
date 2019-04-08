package com.sumian.sddoctor.notification

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.sumian.common.notification.ISchemeResolver
import com.sumian.common.notification.SchemeResolveUtil
import com.sumian.sddoctor.booking.BookingDetailActivity
import com.sumian.sddoctor.buz.weeklyreport.WeeklyReportActivity
import com.sumian.sddoctor.me.myservice.MyServiceListActivity
import com.sumian.sddoctor.me.mywallet.PendingIncomeDetailActivity
import com.sumian.sddoctor.me.mywallet.WalletRecordDetailActivity
import com.sumian.sddoctor.me.mywallet.WithdrawDetailActivity
import com.sumian.sddoctor.service.advisory.activity.AdvisoryDetailActivity
import com.sumian.sddoctor.service.advisory.onlinereport.OnlineReportDetailActivity
import com.sumian.sddoctor.service.evaluation.activity.WeekEvaluationDetailWebActivity

@Suppress("UNUSED_PARAMETER")
/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2018/11/22 18:46
 * desc   :
 * version: 1.0
 */
object SchemeResolver : ISchemeResolver {
    override fun schemeResolver(context: Context, scheme: String): Intent? {
        val uri = SchemeResolveUtil.stringToUri(scheme)
        return when (uri.host) {
            "booking-detail" -> resolveBookingScheme(context, uri)
            "advisory-missions" -> resolveAdvisoryScheme(context, uri)
            "online-reports" -> resolveOnlineReportScheme(context, uri)
            "my-services" -> resolveMyServicesScheme(context, uri)
            "diary-evaluations" -> resolveDiaryEvaluationScheme(context, uri)
            "wallet-details" -> resolveWalletDetailScheme(context, uri)
            "withdrawals" -> resolveWithdrawDetailScheme(context, uri)
            "system-notifications-detail" -> resolveSystemNotificationsDetail(context, uri)
            "pending-income" -> resolvePendingIncomeDetail(context, uri)
            "cbti-week-report" -> resolveWeeklyReport(context, uri)
            else -> null
        }
    }

    /**
     * sd-doctor://cbti-week-report?notification_id=f8a928b0-1cb0-4e9e-b277-02f92e5d10ca&date=1554080408
     */
    private fun resolveWeeklyReport(context: Context, uri: Uri): Intent? {
        return WeeklyReportActivity.getLaunchIntent(uri.getQueryParameter("date")?.toInt() ?: 0)
    }

    /**
     * scheme=sd-doctor://pending-income?pending_income_id=2&notification_id=d36e45e4-a2fb-422b-b7d7-770b6f9c79a6&doctor_id=3
     */
    private fun resolvePendingIncomeDetail(context: Context, uri: Uri): Intent? {
        return PendingIncomeDetailActivity.getLaunchIntent(
                uri.getQueryParameter("pending_income_id")?.toInt() ?: 0)
    }

    private fun resolveSystemNotificationsDetail(context: Context, uri: Uri): Intent? {
        return SystemNotificationDetailActivity.getIntent(context,
                uri.getQueryParameter("notice_id")?.toInt() ?: 0)
    }

    /**
     *  "scheme": "sd-doctor://withdrawals?withdrawal_id=1&notification_id=c16e9f33-6d81-43db-bbd7-9db90231a21e&doctor_id=15" //urlencode后
     */
    fun resolveWithdrawDetailScheme(context: Context, uri: Uri): Intent {
        val data = uri.getQueryParameter("withdrawal_id")?.toInt() ?: 0
        return WithdrawDetailActivity.getLaunchIntent(data)
    }

    /**
     *  "scheme": "sd-doctor://wallet-details?wallet_detail_id=32&notification_id=db235c62-30da-4250-bac7-8722d013e911&doctor_id=10" //urlencode后
     */
    fun resolveWalletDetailScheme(context: Context, uri: Uri): Intent {
        val data = uri.getQueryParameter("wallet_detail_id")?.toInt() ?: 0
        return WalletRecordDetailActivity.getLaunchIntent(data)
    }

    /**
     * sd-doctor://diary-evaluations?id=203&notification_id=e0657db9-ed64-4631-9c3b-1d65919c7aa3&doctor_id=1
     */
    fun resolveDiaryEvaluationScheme(context: Context, uri: Uri): Intent {
        val id = uri.getQueryParameter("id")?.toInt() ?: 0
        return WeekEvaluationDetailWebActivity.getLaunchIntent(context, id)
    }

    /**
     * "scheme": "sd-doctor://my-services?notification_id=753ce0e7-cdf2-4b80-a5bb-961039643c7f&doctor_id=75"
     */
    fun resolveMyServicesScheme(context: Context, uri: Uri): Intent {
        return Intent(context, MyServiceListActivity::class.java)
    }

    /**
    "scheme" => 'sleepdoctor://online-reports?id=1&url=www.baidu.com&notification_id=9f3f9091-ab98-421c-ac2c-47709c80ba16&user_id=1',   //urlencode后
     */
    fun resolveOnlineReportScheme(context: Context, uri: Uri): Intent {
        val id = uri.getQueryParameter("id")?.toInt() ?: 0
        return OnlineReportDetailActivity.getLaunchIntent(context, id)
    }

    /**
     * "scheme": "sd-doctor://advisory-missions?id=21&user_id=1&notification_id=a983e026-08c9-44d2-b687-3962359820da&doctor_id=1"
     */
    fun resolveAdvisoryScheme(context: Context, uri: Uri): Intent {
        val id = uri.getQueryParameter("id")
        val userId = uri.getQueryParameter("user_id")
        return AdvisoryDetailActivity.getLaunchIntent(id!!.toInt(), userId!!.toInt())
    }

    /**
     * sleepdoctor%3A%2F%2Fcall-booking%3Fplan_start_at%3D1530183600%26notification_id%3Ddf6568da-a5a0-40d3-b7df-9021e87b178f
     */
    fun resolveBookingScheme(context: Context, uri: Uri): Intent {
        val data = uri.getQueryParameter("id")
        return BookingDetailActivity.getLaunchIntent(context, data!!.toInt())
    }
}