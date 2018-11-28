package com.sumian.hw.report.widget.bean;

import androidx.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by jzz
 * on 2017/11/2.
 * <p>
 * desc:睡眠不足时,睡眠建议
 */

public class SleepAdvice implements Serializable {

    private String factor_detail;
    private String explanation;
    private String advice;

    public String getFactor_detail() {
        return factor_detail;
    }

    public SleepAdvice setFactor_detail(String factor_detail) {
        this.factor_detail = factor_detail;
        return this;
    }

    public String getExplanation() {
        return explanation;
    }

    public SleepAdvice setExplanation(String explanation) {
        this.explanation = explanation;
        return this;
    }

    public String getAdvice() {
        return advice;
    }

    public SleepAdvice setAdvice(String advice) {
        this.advice = advice;
        return this;
    }

    @NonNull
    @Override
    public String toString() {
        return "SleepAdvice{" +
            "factor_detail='" + factor_detail + '\'' +
            ", explanation='" + explanation + '\'' +
            ", advice='" + advice + '\'' +
            '}';
    }
}
