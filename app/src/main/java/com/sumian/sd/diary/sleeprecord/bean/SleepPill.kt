package com.sumian.sd.diary.sleeprecord.bean

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * <pre>
 * @author : Zhan Xuzhao
 * e-mail : xuzhao.z@sumian.com
 * time   : 2018/6/1 9:14
 * desc   :
 * version: 1.0
</pre> *
 */
@Parcelize
class SleepPill(var name: String, var amount: String, var time: String) : Parcelable {
    /**
     * name : 艾司唑仑
     * amount : 2.75片
     * time : 午饭前／后
     */


    override fun toString(): String {
        return "$name($amount, $time)"
    }
}
