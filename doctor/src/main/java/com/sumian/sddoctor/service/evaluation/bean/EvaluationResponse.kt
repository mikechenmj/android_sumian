package com.sumian.sddoctor.service.evaluation.bean

import com.sumian.sddoctor.network.response.Meta

/**
 * Created by dq
 *
 * on 2018/8/30
 *
 * desc:  周评估 Response
 */
data class EvaluationResponse(var data: ArrayList<WeekEvaluation>,
                              var meta: Meta)