package com.sumian.common.widget

import android.content.Context
import android.database.DataSetObserver
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.google.android.flexbox.FlexboxLayout
import com.sumian.common.R

class SumianFlexboxLayout : FlexboxLayout {

    companion object {
        fun getSimpleLabelTextView(context: Context): TextView {
            var view = TextView(context)
            var param = LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 35f, context.resources.displayMetrics).toInt()
            )
            param.setMargins(
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, context.resources.displayMetrics).toInt(),
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, context.resources.displayMetrics).toInt(),
                    0,
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, context.resources.displayMetrics).toInt()
            )
            view.layoutParams = param
            view.setPadding(
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20f, context.resources.displayMetrics).toInt(),
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 7.5f, context.resources.displayMetrics).toInt(),
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20f, context.resources.displayMetrics).toInt(),
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 7.5f, context.resources.displayMetrics).toInt()
            )
            view.textSize = 14f
            view.gravity = Gravity.CENTER
            return view
        }

        fun updateLabelUi(view: TextView, isChecked: Boolean) {
            var backgroundRes: Int
            var textColor: Int
            if (isChecked) {
                backgroundRes = R.drawable.label_text_selected_background
                textColor = Color.WHITE
            } else {
                backgroundRes = R.drawable.label_text_un_selected_background
                textColor = view.context.resources.getColor(R.color.t2_color)
            }
            view.setBackgroundResource(backgroundRes)
            view.setTextColor(textColor)
        }
    }

    private var mAdapter: BaseAdapter? = null
    private var mOnItemClickListener: OnItemClickListener? = null
    private var mDataSetObserver = object : DataSetObserver() {
        override fun onChanged() {
            var adapter = mAdapter
            if (adapter == null) {
                return
            }
            removeAllViews()
            for (i in 0 until adapter.count) {
                var view = adapter.getView(i, null, this@SumianFlexboxLayout)
                view.setOnTouchListener { v, e ->
                    if (e.action == MotionEvent.ACTION_UP) {

                    }
                    when (e.action) {
                        MotionEvent.ACTION_UP -> {
                            mOnItemClickListener?.onItemClick(
                                    this@SumianFlexboxLayout,
                                    v,
                                    i,
                                    mAdapter?.getItemId(i) ?: 0
                            )
                        }
                        MotionEvent.ACTION_DOWN -> {
                            return@setOnTouchListener true
                        }
                    }
                    false
                }
                addView(view)
            }

        }
    }

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun geAdapter(): BaseAdapter? {
        return mAdapter
    }

    fun setAdapter(adapter: BaseAdapter?) {
        if (adapter == null) {
            mAdapter?.unregisterDataSetObserver(mDataSetObserver)
            removeAllViews()
        }
        mAdapter = adapter
        adapter?.registerDataSetObserver(mDataSetObserver)
        adapter?.notifyDataSetChanged()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mAdapter?.unregisterDataSetObserver(mDataSetObserver)
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        mOnItemClickListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(parent: SumianFlexboxLayout, view: View, position: Int, id: Long)
    }

    data class SimpleLabelBean(var label: String, var isChecked: Boolean)
}
