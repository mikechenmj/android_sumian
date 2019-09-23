package com.sumian.common.widget

import android.content.Context
import android.database.DataSetObserver
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.BaseAdapter
import com.google.android.flexbox.FlexboxLayout

class SumianFlexboxLayout : FlexboxLayout {

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

    fun setAdapter(adapter: BaseAdapter) {
        mAdapter = adapter
        adapter.registerDataSetObserver(mDataSetObserver)
        adapter.notifyDataSetChanged()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mAdapter?.unregisterDataSetObserver(mDataSetObserver)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mOnItemClickListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(parent: SumianFlexboxLayout, view: View, position: Int, id: Long)
    }
}
