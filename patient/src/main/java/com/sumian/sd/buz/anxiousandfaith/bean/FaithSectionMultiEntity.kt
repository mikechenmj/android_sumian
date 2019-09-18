package com.sumian.sd.buz.anxiousandfaith.bean
import com.chad.library.adapter.base.entity.SectionMultiEntity

/**
 * @author : chenmj
 * e-mail : 448900450@qq.com
 * time   : 2019/09/09 11:40
 * desc   :
 * version: 1.0
 */

class FaithSectionMultiEntity : SectionMultiEntity<MoodDiaryData> {

    private var type: Int = 0

    override fun getItemType(): Int {
        return type
    }

    constructor(moodDiaryData: MoodDiaryData) : this(0, moodDiaryData)

    constructor(type: Int, moodDiaryData: MoodDiaryData) : super(moodDiaryData) {
        this.type = type
    }

    constructor(isHeader: Boolean, header: String) : super(isHeader, header)
}