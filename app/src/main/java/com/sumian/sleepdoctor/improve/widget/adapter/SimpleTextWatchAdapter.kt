package com.sumian.sleepdoctor.improve.widget.adapter

import android.text.Editable
import android.text.TextWatcher

/**
 *
 *Created by sm
 * on 2018/6/8 16:05
 * desc:
 **/
open class SimpleTextWatchAdapter : TextWatcher {

    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }
}