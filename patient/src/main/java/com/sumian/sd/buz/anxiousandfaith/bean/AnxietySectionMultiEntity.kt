package com.sumian.sd.buz.anxiousandfaith.bean
import com.chad.library.adapter.base.entity.SectionMultiEntity

/**
 * @author : chenmj
 * e-mail : 448900450@qq.com
 * time   : 2019/09/09 11:40
 * desc   :
 * version: 1.0
 */

class AnxietySectionMultiEntity : SectionMultiEntity<AnxietyData> {

    private var type: Int = 0

    override fun getItemType(): Int {
        return type
    }

    constructor(anxietyData: AnxietyData) : this(0, anxietyData)

    constructor(type: Int, anxietyData: AnxietyData) : super(anxietyData) {
        this.type = type
    }

    constructor(isHeader: Boolean, header: String) : super(isHeader, header)
}