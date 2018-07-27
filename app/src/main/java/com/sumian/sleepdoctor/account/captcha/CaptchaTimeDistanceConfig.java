package com.sumian.sleepdoctor.account.captcha;

import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.config.SumianConfig;

import java.util.Locale;

/**
 * Created by jzz
 * on 2017/10/18.
 * desc:  验证码倒计时配置文件
 */

public final class CaptchaTimeDistanceConfig {

    private static CountDownTimer mTimer;
    private static int mRemainingTime;

    /**
     * @param tvCaptcha       tvCaptcha
     * @param isCountDown     isCountDown
     * @param captchaTimeType captchaTimeType
     */
    public static void showTimer(TextView tvCaptcha, boolean isCountDown, String captchaTimeType) {

        int timeDistance = 60;

        if (!isCountDown) {
            CaptchaTime captchaTime = SumianConfig.syncCaptchaTimeDistance(captchaTimeType);

            int remainingTime = captchaTime.getRemainingTime();
            if (remainingTime == 0) {
                //默认是0，表示没有记录
                return;
            }

            long saveTime = captchaTime.getSaveTime();

            timeDistance = (int) ((System.currentTimeMillis() - saveTime) / 1000);

            timeDistance = remainingTime - timeDistance;

            if (timeDistance <= 0) {
                return;
            }
        }

        //这里获取验证码间隔未超过60s，需要启动后续剩余计时器
        tvCaptcha.setEnabled(false);
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        mTimer = new CountDownTimer(timeDistance * 1000L, 1000L) {
            @Override
            public void onTick(long millisUntilFinished) {
                int time = (int) (millisUntilFinished / 1000L);
                tvCaptcha.setText(String.format(Locale.getDefault(), "%s%02ds", " 已发送", time));
                mRemainingTime = time;
            }

            @Override
            public void onFinish() {
                mRemainingTime = 0;
                tvCaptcha.setEnabled(true);
                tvCaptcha.setActivated(false);
                tvCaptcha.setText(R.string.send_captcha);
                NotifyOrClearCaptchaTimeDistance(tvCaptcha, captchaTimeType);
            }
        }.start();
    }

    /**
     * @param v view
     */
    public static void NotifyOrClearCaptchaTimeDistance(View v, String captchaTimeType) {

        CountDownTimer timer = CaptchaTimeDistanceConfig.mTimer;
        if (timer != null) {
            timer.cancel();
            mTimer = null;
        }

        if (v.isEnabled()) {
            SumianConfig.clearCaptchaTimeDistance(captchaTimeType);
            return;
        }

        CaptchaTime captchaTime = new CaptchaTime()
                .setRemainingTime(mRemainingTime)
                .setSaveTime(System.currentTimeMillis());

        SumianConfig.updateCaptchaTimeDistance(captchaTime, captchaTimeType);
    }


}
