package com.sumian.sd.account.medal.bean

import android.os.Parcelable
import com.sumian.sd.R
import com.sumian.sd.app.App
import com.sumian.sd.utils.TimeUtil
import kotlinx.android.parcel.Parcelize

/**
 * Created by jzz
 *
 * on 2019/1/23
 *
 * desc:
 */
@Parcelize
data class Record(val id: Int,
                  val rewarded_at: Int?,
                  val pop_at: Int?) : Parcelable {

    fun formatDate(): String {
        return TimeUtil.formatDate("yyyy.MM.dd ${App.getAppContext().getString(R.string.acquisition)}", rewarded_at!!.times(1000L))
    }

}