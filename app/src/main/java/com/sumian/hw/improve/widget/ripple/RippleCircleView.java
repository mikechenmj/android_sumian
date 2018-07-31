package com.sumian.hw.improve.widget.ripple;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by sm
 * on 2018/3/22.
 * desc:
 */

public class RippleCircleView extends View {

    private RippleAnimationView rippleAnimationView;

    public RippleCircleView(Context context) {
        super(context);
    }

    public RippleCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RippleCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RippleCircleView(RippleAnimationView rippleAnimationView, Context context) {
        super(context);
        this.rippleAnimationView = rippleAnimationView;
        this.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int radius = (Math.min(getWidth(), getHeight())) >> 1;
        canvas.drawCircle(radius, radius, radius - rippleAnimationView.rippleStrokeWidth, rippleAnimationView.paint);
    }
}
