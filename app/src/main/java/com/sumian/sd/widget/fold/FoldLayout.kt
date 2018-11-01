package com.sumian.sd.widget.fold

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.sumian.sd.R
import kotlinx.android.synthetic.main.lay_fold_text_container.view.*


/**
 * Created by sm
 * on 2018/5/30 13:54
 * desc:
 */
class FoldLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr), View.OnClickListener {

    init {
        View.inflate(context, R.layout.lay_fold_text_container, this)
    }

    override fun onClick(v: View) {
        tv_summary.maxLines = Integer.MAX_VALUE
        tv_show_more.setLines(Integer.MAX_VALUE)
        tv_show_more.visibility = View.GONE
    }

    fun setText(text: String) {
        tv_summary.visibility = View.GONE
        tv_summary.text = text
        tv_summary.visibility = View.VISIBLE
        if (tv_summary.lineCount > 4) {
            tv_summary.ellipsize = TextUtils.TruncateAt.END
            tv_summary.maxLines = 4

            tv_show_more.tag = true
            tv_show_more.setOnClickListener(this)
            tv_show_more.visibility = View.VISIBLE
        } else {
            if (tv_show_more.tag == null) {
                tv_show_more.visibility = View.GONE
            }
        }
    }
}
