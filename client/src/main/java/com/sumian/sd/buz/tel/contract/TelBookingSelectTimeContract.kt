package com.sumian.sd.buz.tel.contract

/**
 * Created by sm
 *
 * on 2018/8/16
 *
 * desc:
 *
 */
interface TelBookingSelectTimeContract {

    interface View {

        fun transformOneDisplayedValues(currentPosition: Int, hintText: String, displayedValues: Array<out String>)

        fun transformTwoDisplayedValues(currentPosition: Int, hintText: String, displayedValues: Array<out String>)

        fun transformThreeDisplayedValues(currentPosition: Int, hintText: String, displayedValues: Array<out String>)

    }

}