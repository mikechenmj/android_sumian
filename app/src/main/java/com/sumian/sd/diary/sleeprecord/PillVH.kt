package com.sumian.sd.diary.sleeprecord

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sumian.sd.R
import com.sumian.sd.diary.sleeprecord.bean.SleepPill
import java.util.*

/**
 * <pre>
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/6/1 11:36
 * desc   :
 * version: 1.0
</pre> *
 */
class PillVH private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val tvPill: TextView by lazy {
        itemView.findViewById<TextView>(R.id.tv_pill)
    }
    private val tvTime: TextView by lazy {
        itemView.findViewById<TextView>(R.id.tv_time)

    }

    fun setData(pill: SleepPill) {
        tvPill.text = String.format(Locale.getDefault(), "%s（%s）", pill.name, pill.amount)
        tvTime.text = pill.time
    }

    companion object {
        @JvmStatic
        fun create(parent: ViewGroup): PillVH {
            val context = parent.context
            val inflate = LayoutInflater.from(context).inflate(R.layout.item_pill, parent, false)
            return PillVH(inflate)
        }
    }
}
