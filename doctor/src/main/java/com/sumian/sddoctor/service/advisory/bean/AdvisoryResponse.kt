package com.sumian.sddoctor.service.advisory.bean

import com.sumian.sddoctor.network.response.Meta

/**
 * Created by dq
 *
 * on 2018/8/30
 *
 * desc:
 */
data class AdvisoryResponse(var data: ArrayList<Advisory>,
                            var meta: Meta)