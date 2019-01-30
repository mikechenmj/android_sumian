package com.sumian.sd.account.achievement.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.sumian.common.image.loadImage
import com.sumian.sd.R
import com.sumian.sd.account.achievement.bean.ShareAchievement
import com.sumian.sd.account.achievement.formatHtml
import com.tencent.smtt.sdk.WebSettings
import kotlinx.android.synthetic.main.lay_achievement_share_view.view.*

/**
 * Created by jzz
 *
 * on 2019/1/24
 *
 * desc:  cbti  achievement share view
 */
class AchievementShareView : LinearLayout {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        orientation = LinearLayout.VERTICAL
        setBackgroundResource(R.drawable.ic_cbti_share_bg)
        View.inflate(context, R.layout.lay_achievement_share_view, this)
    }

    @SuppressLint("SetTextI18n")
    fun bindAchievement(shareAchievement: ShareAchievement) {
        iv_share_avatar.loadImage(shareAchievement.avatar, R.mipmap.ic_info_avatar_patient, R.mipmap.ic_info_avatar_patient, true)
        val achievement = shareAchievement.achievement
        tv_share_medal_title.text = achievement.title
        tv_share_get_date.text = achievement.record?.formatDate()
        tv_share_medal_content_title.text = achievement.sentence
        web_view_share.sWebView.run {
            settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
            isVerticalScrollBarEnabled = false
            this.setVerticalScrollbarOverlay(false)
            isHorizontalScrollBarEnabled = false
            this.setHorizontalScrollbarOverlay(false)
            loadDataWithBaseURL(null, formatHtml(achievement.context), "text/html", "utf-8", null)
        }
        iv_share_qr_code.loadImage(shareAchievement.qrCode, R.mipmap.ic_info_avatar_patient, R.mipmap.ic_info_avatar_patient)
    }
}