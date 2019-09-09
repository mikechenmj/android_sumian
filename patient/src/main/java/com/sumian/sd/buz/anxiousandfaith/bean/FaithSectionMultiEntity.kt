package com.sumian.sd.buz.anxiousandfaith.bean
import com.chad.library.adapter.base.entity.SectionMultiEntity

/**
 * @author : chenmj
 * e-mail : 448900450@qq.com
 * time   : 2019/09/09 11:40
 * desc   :
 * version: 1.0
 */

class FaithSectionMultiEntity : SectionMultiEntity<FaithData> {

    private var type: Int = 0

    override fun getItemType(): Int {
        return type
    }

    constructor(faithData: FaithData) : this(0, faithData)

    constructor(type: Int, faithData: FaithData) : super(faithData) {
        this.type = type
    }

    constructor(isHeader: Boolean, header: String) : super(isHeader, header)
}