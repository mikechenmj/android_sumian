package com.sumian.sd.buz.homepage.bean

import com.google.gson.annotations.SerializedName

/**
 *         {
"id": 1,
"title": "第1周 认识睡眠",
"banner": "http://sleep-doctor-dev.oss-cn-shenzhen.aliyuncs.com/cbti/img_cbti_bg@3x.png",
"introduction": "这节课将会介绍睡眠和失眠的知识，让你能彻底地了解睡眠的科学和与失眠有关的定义和引起失眠的因素。赶快打开下面视频一起学习吧。",
"summary": "睡眠时间占据了生命中很大一部分，睡觉和吃饭一样，都是我们获得能量的来源。\n入睡过程中，我们的身体开始处于静止状态，但有一个部位依旧活跃，这就是——大脑。根据睡眠监测脑波显示，我们主要将睡眠分为两种状态：非快速眼动睡眠和快速眼动睡眠。\n在非快速眼动睡眠中，又可以分为三个阶段，每个阶段的睡眠状况都不一样。快速眼动睡眠顾名思义伴随着快速的眼球转动，而身体的肌肉处于最松弛的状态，在这个阶段心率，血压和体温都会升高，梦也常在这个阶段发生。\n这四个睡眠阶段共同构成了一个睡眠周期，每个周期大约90分钟。一个晚上，我们大约会经历4至6个这样的周期，越到后面的睡眠周期深层睡眠时间会越来越短，而快速动眼睡眠期则会越来越长，所以早上也是我们特别容易做梦的时间。",
"index": 1,
"is_lock": false,
"open_date_text": null,
"chapter_progress": 0
}
 */
data class CbtiChapterData(
        @SerializedName("id") val id: Int,
        @SerializedName("title") val title: String,
        @SerializedName("banner") val banner: String,
        @SerializedName("introduction") val introduction: String,
        @SerializedName("summary") val summary: String,
        @SerializedName("index") val index: Int,
        @SerializedName("is_lock") val isLock: Boolean,
        @SerializedName("open_date_text") val openDateText: String?,
        @SerializedName("chapter_progress") val chapterProgress: Int,
        @SerializedName("scale_distribution_ids") val scale_distribution_ids: String //需要评估则返回scale_distribution_ids否则返回空字符串
) {

    fun formatProgress(): String {
        return when (chapterProgress) {
            0 -> {
                if (isLock) {
                    openDateText ?: "请先完成上周课程"
                } else {
                    "进度：$chapterProgress%"
                }
            }
            100 -> {
                "已完成"
            }
            else -> {
                "进度：$chapterProgress%"
            }
        }

    }
}