package com.sumian.sd.account.login

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.sumian.common.base.BasePresenterActivity
import com.sumian.sd.R

class ValidatePhoneNumberActivity : BasePresenterActivity<ValidatePhoneNumberContract.Presenter>() ,ValidatePhoneNumberContract.View{

    override fun getLayoutId(): Int {
        return R.layout.activity_validate_phone_number
    }

    override fun onValidateSuccess() {
    }

    override fun onValidateFailure() {
    }
}
