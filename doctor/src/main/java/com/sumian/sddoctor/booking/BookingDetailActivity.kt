package com.sumian.sddoctor.booking

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ToastUtils
import com.sumian.common.base.BaseActivity
import com.sumian.common.network.response.ErrorResponse
import com.sumian.sddoctor.R
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.booking.bean.BookingDetail
import com.sumian.sddoctor.constants.StatConstants
import com.sumian.sddoctor.network.callback.BaseSdResponseCallback
import com.sumian.sddoctor.patient.fragment.PatientInfoWebFragment
import kotlinx.android.synthetic.main.activity_booking_detail.*

class BookingDetailActivity : BaseActivity() {
    private var mAdapter: BookingDetailAdapter? = null

    override fun getLayoutId(): Int {
        return R.layout.activity_booking_detail
    }

    companion object {
        private const val KEY_BOOKING_ID = "BOOKING_ID"
        private const val INVALID_BOOKING_ID = -1
        fun getLaunchIntent(context: Context, bookingId: Int): Intent {
            val intent = Intent(context, BookingDetailActivity::class.java)
            intent.putExtra(KEY_BOOKING_ID, bookingId)
            return intent
        }

        fun launch(context: Context, bookingId: Int) {
            ActivityUtils.startActivity(getLaunchIntent(context, bookingId))
        }
    }

    override fun getPageName(): String {
        return StatConstants.page_service_phone_reserve_detail
    }

    override fun initWidget() {
        super.initWidget()
        title_bar.setOnBackClickListener { onBackPressed() }
        tab_layout.setupWithViewPager(view_pager)
    }

    override fun initData() {
        super.initData()
        val bookingId = intent.getIntExtra(KEY_BOOKING_ID, INVALID_BOOKING_ID)
        if (bookingId == INVALID_BOOKING_ID) {
            return
        }
        val call = AppManager.getHttpService().getBookingDetail(bookingId)
        addCall(call)
        showLoading()
        call.enqueue(object : BaseSdResponseCallback<BookingDetail>() {
            override fun onSuccess(response: BookingDetail?) {
                if (response == null) {
                    return
                }
                mAdapter = BookingDetailAdapter(this@BookingDetailActivity, supportFragmentManager, response)
                view_pager.adapter = mAdapter
            }

            override fun onFailure(errorResponse: ErrorResponse) {
                ToastUtils.showShort(errorResponse.message)
            }

            override fun onFinish() {
                dismissLoading()
            }
        })
    }

    class BookingDetailAdapter(context: Context, fragmentManager: FragmentManager, bookingDetail: BookingDetail) : FragmentPagerAdapter(fragmentManager) {
        private val mContext = context
        private val mBookingId = bookingDetail

        private val bookingDetailFragment by lazy { BookingDetailFragment.newInstance(mBookingId.id) }
        private val patientInfoWebFragment by lazy { PatientInfoWebFragment.newInstance(mBookingId.userId, false) }

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> bookingDetailFragment
                1 -> patientInfoWebFragment
                else -> throw RuntimeException("Invalid position")
            }
        }

        override fun getCount(): Int {
            return 2
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> mContext.getString(R.string.booking_information)
                1 -> mContext.getString(R.string.patient_record)
                else -> throw RuntimeException("Invalid position")
            }
        }
    }

    override fun onBackPressed() {
        val fragment = mAdapter?.getItem(view_pager.currentItem)
        if (fragment is PatientInfoWebFragment && fragment.onBack()) {
            return
        }
        super.onBackPressed()
    }
}
