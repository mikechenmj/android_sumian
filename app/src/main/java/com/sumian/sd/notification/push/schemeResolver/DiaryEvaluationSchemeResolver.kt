package com.sumian.sd.notification.push.schemeResolver

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.sumian.sd.service.diary.DiaryEvaluationDetailActivity

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/8 20:29
 *     desc   :
 *     version: 1.0
 * </pre>
 */
class DiaryEvaluationSchemeResolver : SchemeResolver {

    /**
     * 医生发送了新的量表
     * "scheme" =>  sleepdoctor://diary-evaluations?id=91&notification_id=c9b459ca-6a81-4ad8-99f3-2b2b6a06ffc2&user_id=2939
     */
    override fun resolveScheme(context: Context, uri: Uri): Intent {
        val data = uri.getQueryParameter("id").toInt()
        return DiaryEvaluationDetailActivity.getLaunchIntent(context, data)
    }
}