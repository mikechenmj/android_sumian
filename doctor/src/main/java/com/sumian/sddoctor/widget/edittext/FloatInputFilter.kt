package com.sumian.sddoctor.widget.edittext

import android.text.InputFilter
import android.text.Spanned
import android.text.TextUtils

/**
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/10/29 10:40
 * desc   :
 * version: 1.0
 */
class FloatInputFilter(
        private val mIntegerLength: Int,
        private val mDecimalLength: Int
) : InputFilter {

    init {
        if (mDecimalLength < 0) {
            throw IllegalArgumentException("decimalLength must >= 0")
        }
    }

    override fun filter(
            source: CharSequence,
            start: Int,
            end: Int,
            dest: Spanned,
            dstart: Int,
            dend: Int
    ): CharSequence {
        val sourceString = source.toString()
        if (TextUtils.isEmpty(sourceString)) {
            return sourceString
        }
        val result: CharSequence
        val destString = dest.toString()

        val dotIndex = destString.indexOf(".")
        if (dotIndex == -1) { // dest 没有小数点
            val sourceDotIndex = sourceString.indexOf(".")
            val availableIntegerLength = mIntegerLength - destString.length
            result = if (sourceDotIndex == -1) { // source 没有小数点
                source.subSequence(0, Math.min(availableIntegerLength, source.length))
            } else { // source 有小数点
                if (sourceDotIndex > 0) {
                    val split = sourceString.split(".")
                    val sb = StringBuilder()
                    sb.append(split[0].subSequence(0, Math.min(availableIntegerLength, split[0].length)))
                    if (split.size > 1) {
                        sb.append(".")
                        sb.append(split[1].subSequence(0, Math.min(mDecimalLength, split[1].length)))
                    }
                    sb.toString()
                } else {
                    sourceString.subSequence(0, Math.min(mDecimalLength, source.length))
                }
            }
        } else { // dest 有小数点
            result = if (dotIndex != -1 && dstart > dotIndex) { // 光标在小数部分
                val availableDecimalLength = dotIndex + mDecimalLength + 1 - dest.length
                if (availableDecimalLength > 0) {
                    source.subSequence(0, Math.min(source.length, availableDecimalLength))
                } else {
                    ""
                }
            } else { // 光标在整数部分
                val availableIntegerLength = mIntegerLength - dotIndex
                if (availableIntegerLength > 0) {
                    source.subSequence(0, Math.min(source.length, availableIntegerLength))
                } else {
                    if (source.toString() == ".") {
                        source
                    } else {
                        ""
                    }
                }
            }
        }
        return result
    }
}