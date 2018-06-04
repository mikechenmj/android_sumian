package com.sumian.sleepdoctor.improve.widget

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.View
import com.sumian.sleepdoctor.R

/**
 * Created by sm
 * on 2018/6/2 16:57
 * desc:  项目中,统一的详情渲染 view
 * [-----------------------------------------text ]
 * [----------------------------------icon------- ]
 * [-----------------------------------------text ]  这种布局样式的view
 */
class ContentDetailLayout : ConstraintLayout {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(context)
    }

    private fun initView(context: Context) {
        View.inflate(context, R.layout.lay_content_detail_view, this)
    }


    public fun invalidContent(iconUrl:String?,title:String,desc:String) {


    }

}
