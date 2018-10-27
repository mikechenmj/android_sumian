package com.sumian.sd.anxiousandfaith.bean

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/27 10:43
 * desc   :
 * version: 1.0
 */
data class AnxietyFaithItemViewData(val type: Int = TYPE_ANXIETY, val id: Int, val title: String, val message: String, val time: Long, val emotion: Int = 0) {
    companion object {
        const val TYPE_ANXIETY = 0
        const val TYPE_BELIEF = 1

        fun create(data: AnxietyData): AnxietyFaithItemViewData {
            return AnxietyFaithItemViewData(TYPE_ANXIETY, data.id, data.anxiety, data.solution, data.getUpdateAtInMillis())
        }

        fun create(data: FaithData): AnxietyFaithItemViewData {
            return AnxietyFaithItemViewData(TYPE_BELIEF, data.id, data.scene, data.idea, data.created_at * 1000L, data.emotion_type)
        }

        fun transformAnxietyList(inputList: List<AnxietyData>): ArrayList<AnxietyFaithItemViewData> {
            val list = ArrayList<AnxietyFaithItemViewData>()
            for (data in inputList) {
                list.add(create(data))
            }
            return list
        }

        fun transformFaithList(inputList: List<FaithData>): ArrayList<AnxietyFaithItemViewData> {
            val list = ArrayList<AnxietyFaithItemViewData>()
            for (data in inputList) {
                list.add(create(data))
            }
            return list
        }
    }
}