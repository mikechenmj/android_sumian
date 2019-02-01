package com.sumian.sddoctor.service.publish.bean

/**
 * Created by  sm
 *
 * on 2018/9/28
 *
 *desc: publish 公共信息  包括对图文咨询/周日记评估 发布
 *
 */
object Publish {

    const val EXTRAS_PUBLISH_ID = "com.sumian.sddoctor.extras.publish.id"
    const val EXTRAS_PUBLISH_TYPE = "com.sumian.sddoctor.extras.publish.type"

    const val PUBLISH_ADVISORY_TYPE = 0x01  //publish 图文咨询  类型
    const val PUBLISH_EVALUATION_TYPE = 0x02 //publish 周日记评估 类型
}