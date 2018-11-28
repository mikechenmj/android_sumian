package com.sumian.sd.service.cbti.bean

import android.text.TextUtils
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.google.gson.annotations.SerializedName
import com.sumian.common.utils.TimeUtilV2
import com.sumian.sd.R
import com.sumian.sd.app.App

/**
 * 留言板留言
 */
data class MessageBoard(
        @SerializedName("id")
        val id: Int,
        @SerializedName("admin_id")
        val adminId: Int,//回复管理员id
        @SerializedName("anonymous")
        val anonymous: Int,//是否匿名 1：匿名 0：不匿名
        @SerializedName("commenter")
        val commenter: Commenter,
        @SerializedName("commenter_id")
        val commenterId: Int,//留言者id
        @SerializedName("commenter_type")
        val commenterType: String,//留言者类型
        @SerializedName("created_at")
        val createdAt: Int, //留言时间
        @SerializedName("deleted_at")
        val deletedAt: Any?,
        @SerializedName("message")
        val message: String,//留言内容
        @SerializedName("reply")
        val reply: String?,//回复，没有为空字符串
        @SerializedName("type")
        val type: Int,//0：CBTI课程介绍 1：第1周课程 2：第2周课程 3：第3周课程 4：第4周课程 5：第5周课程 6：第6周课程
        @SerializedName("updated_at")
        val updatedAt: Int,
        @SerializedName("visible")
        val visible: Int,//是否可见 皆为1
        @SerializedName("is_top") val isTop: Int//是否置顶 0:否 1:是
) {

    fun showReply(reply: TextView, replyLay: LinearLayout) {
        if (TextUtils.isEmpty(this.reply)) {
            reply.text = null
            replyLay.visibility = View.GONE
        } else {
            reply.text = this.reply
            replyLay.visibility = View.VISIBLE
        }
    }

    fun formatNickName(): String {
        return if (anonymous == 1) {
            App.getAppContext().getString(R.string.anonymous_nickname)
        } else {
            commenter.formatNickname()
        }
    }

    fun formatWriteTime(): String = TimeUtilV2.formatDate("yyyy.MM.dd", createdAt * 1000L)

    fun isTopping(): Boolean = (isTop == 1)
}