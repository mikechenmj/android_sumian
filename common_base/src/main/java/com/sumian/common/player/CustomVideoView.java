package com.sumian.common.player;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * @author : Zhan Xuzhao
 * e-mail : 649912323@qq.com
 * time   : 2019/3/19 09:46
 * desc   : 解决videoview高度适配问题
 * reference: https://codeday.me/bug/20181011/291544.html
 * version: 1.0
 */
public class CustomVideoView extends VideoView {
    //最终的视频资源宽度
    private int mVideoWidth = 1080;
    //最终视频资源高度
    private int mVideoHeight = 1920;

    public CustomVideoView(Context context) {
        super(context);
    }

    public CustomVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setVideoSize(int width, int height) {
        mVideoWidth = width;
        mVideoHeight = height;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Log.i("@@@", "onMeasure");
        int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
        int height = getDefaultSize(mVideoHeight, heightMeasureSpec);
        if (mVideoWidth > 0 && mVideoHeight > 0) {
            if (mVideoWidth * height > width * mVideoHeight) {
                height = width * mVideoHeight / mVideoWidth;
            } else {
                width = height * mVideoWidth / mVideoHeight;
            }
        }
        setMeasuredDimension(width, height);
    }
}
