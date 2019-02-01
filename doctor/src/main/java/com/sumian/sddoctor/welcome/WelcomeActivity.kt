package com.sumian.sddoctor.welcome

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.blankj.utilcode.util.ActivityUtils
import com.sumian.sddoctor.R
import com.sumian.sddoctor.account.TokenInfo
import com.sumian.sddoctor.app.AppManager
import com.sumian.sddoctor.login.login.LoginActivity
import com.sumian.sddoctor.main.MainActivity
import com.sumian.sddoctor.util.SumianExecutor

class WelcomeActivity : AppCompatActivity() {

    companion object {
        private const val WELCOME_SHOW_TIME = 500L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        AppManager.getAccountViewModel().getTokenInfo()
                .observe(this, Observer<TokenInfo> { t -> onTokenInfoChange(t) })
    }

    private fun onTokenInfoChange(tokenInfo: TokenInfo?) {
        SumianExecutor.runOnUiThread({
            if (tokenInfo != null && !tokenInfo.isExpired()) {
                ActivityUtils.startActivity(MainActivity::class.java)
            } else {
                ActivityUtils.startActivity(LoginActivity::class.java)
            }
            finish()
        }, WELCOME_SHOW_TIME)
    }
}
