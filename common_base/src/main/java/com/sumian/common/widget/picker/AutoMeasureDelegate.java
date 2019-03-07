package com.sumian.common.widget.picker;

import android.graphics.Paint;

public class AutoMeasureDelegate {

    public static int autoMeasureTextSize(float textSize, float totalWidth, String content) {
        Paint measurePint = new Paint();
        measurePint.setTextSize(textSize);
        float measureTextWidth = measureText(measurePint, content);
        //Log.e(TAG, "PaintTextSize=${textPaint.textSize} textSize=$textSize  measureTextWidth=$measureTextWidth  totalWidth=$totalWidth contentByCurrValue=$contentByCurrValue")
        if (measureTextWidth >= totalWidth) {//超过了控件大小，（当 measureTextWidth）需要进行缩放即 scaleTextSize=textSize-1
            return autoMeasureTextSize(textSize - 1, totalWidth, content);
        } else {//小于控件大小，直接返回
            return (int) textSize;
        }
    }

    private static float measureText(Paint textPaint, String content) {
        return (textPaint.measureText(content) + 0.5f);
    }
}
