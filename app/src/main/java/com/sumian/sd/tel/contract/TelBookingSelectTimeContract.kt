package com.sumian.sd.tel.contract

import com.sumian.common.mvp.IPresenter
import com.sumian.common.mvp.IView

/**
 * Created by sm
 *
 * on 2018/8/16
 *
 * desc:
 *
 */
interface TelBookingSelectTimeContract {

    interface View : IView {

        fun transformOneDisplayedValues(currentPosition: Int, hintText: String, displayedValues: Array<out String>)

        fun transformTwoDisplayedValues(currentPosition: Int, hintText: String, displayedValues: Array<out String>)

        fun transformThreeDisplayedValues(currentPosition: Int, hintText: String, displayedValues: Array<out String>)

    }

    interface Presenter : IPresenter {

        fun getHour(currentUnixTime: Int): Int

        fun getMinute(currentUnixTime: Int): Int

        fun calculateDate(currentUnixTime: Int)

        fun calculateHour(currentHour: Int)

        fun calculateMinute(currentMinute: Int)

        fun formatUnixTime(date: String, hour: String, minute: String): Int

    }
}