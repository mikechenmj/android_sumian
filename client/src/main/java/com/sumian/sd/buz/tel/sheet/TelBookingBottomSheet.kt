package com.sumian.sd.buz.tel.sheet

import android.os.Bundle
import android.view.View
import com.sumian.common.widget.picker.NumberPickerView
import com.sumian.sd.R
import com.sumian.sd.buz.tel.contract.TelBookingSelectTimeContract
import com.sumian.sd.buz.tel.presenter.TelBookingSelectTimePresenter
import com.sumian.sd.widget.base.BaseBottomSheetView
import kotlinx.android.synthetic.main.lay_bottom_sheet_select_tel_booking_time.*

/**
 * Created by sm
 *
 * on 2018/8/16
 *
 * desc: 电话预约中,用户选择预约时间的 widget
 *
 */
class TelBookingBottomSheet : BaseBottomSheetView(), TelBookingSelectTimeContract.View, NumberPickerView.OnValueChangeListener, View.OnClickListener {

    companion object {

        private const val ARGS_TEL_BOOKING_TIME = "com.sumian.sd.args.tel.booking.time"

        private fun newInstance(telBookingTime: Int): TelBookingBottomSheet {
            return TelBookingBottomSheet().apply {
                this.arguments = Bundle().apply {
                    putInt(ARGS_TEL_BOOKING_TIME, telBookingTime)
                }
            }
        }

        fun show(fragmentManager: androidx.fragment.app.FragmentManager, telBookingTime: Int, onSelectTelBookingCallback: OnSelectTelBookingCallback) {
            fragmentManager
                    .beginTransaction()
                    .add(newInstance(telBookingTime).setOnSelectTelBookingCallback(onSelectTelBookingCallback), TelBookingBottomSheet::class.java.simpleName)
                    .commitNowAllowingStateLoss()
        }
    }

    private var mTelBookingTime: Int = 0

    private val mPresenter: TelBookingSelectTimePresenter by lazy {
        TelBookingSelectTimePresenter.init(this@TelBookingBottomSheet)
    }

    private var mOnSelectTelBookingCallback: OnSelectTelBookingCallback? = null


    fun setOnSelectTelBookingCallback(onSelectTelBookingCallback: OnSelectTelBookingCallback): TelBookingBottomSheet {
        this.mOnSelectTelBookingCallback = onSelectTelBookingCallback
        return this
    }

    override fun initBundle(arguments: Bundle?) {
        super.initBundle(arguments)
        arguments?.let {
            this.mTelBookingTime = it.getInt(ARGS_TEL_BOOKING_TIME, 0)
        }
    }

    override fun getLayout(): Int {
        return R.layout.lay_bottom_sheet_select_tel_booking_time
    }

    override fun initView(rootView: View?) {
        super.initView(rootView)
        tv_confirm.setOnClickListener(this)
        npv_one.setOnValueChangedListener(this)
    }

    override fun initData() {
        super.initData()
        mPresenter.calculateDate(mTelBookingTime)
        mPresenter.calculateHour(mPresenter.getHour(mTelBookingTime))
        mPresenter.calculateMinute(mPresenter.getMinute(mTelBookingTime))
    }

    override fun release() {
        super.release()
        mPresenter.onCleared()
    }

    override fun onClick(v: View) {
        //计算出选中的时间的 unixTime
        val onSelectTelBookingTime = mOnSelectTelBookingCallback?.onSelectTelBookingTime(mPresenter.formatUnixTime(npv_one.contentByCurrValue, npv_two.contentByCurrValue, npv_three.contentByCurrValue))
        if (onSelectTelBookingTime!!) {
            dismissAllowingStateLoss()
        }

    }

    override fun onValueChange(picker: NumberPickerView?, oldVal: Int, newVal: Int) {
    }

    override fun transformOneDisplayedValues(currentPosition: Int, hintText: String, displayedValues: Array<out String>) {
        npv_one.refreshByNewDisplayedValues(displayedValues)
        npv_one.pickedIndexRelativeToRaw = currentPosition
        npv_one.setHintText(hintText)
    }

    override fun transformTwoDisplayedValues(currentPosition: Int, hintText: String, displayedValues: Array<out String>) {
        npv_two.refreshByNewDisplayedValues(displayedValues)
        npv_two.pickedIndexRelativeToRaw = currentPosition
        npv_two.setHintText(hintText)
    }

    override fun transformThreeDisplayedValues(currentPosition: Int, hintText: String, displayedValues: Array<out String>) {
        npv_three.refreshByNewDisplayedValues(displayedValues)
        npv_three.pickedIndexRelativeToRaw = currentPosition
        npv_three.setHintText(hintText)
    }

    interface OnSelectTelBookingCallback {

        fun onSelectTelBookingTime(unixTime: Int): Boolean
    }


}