package com.xiao.nicevideoplayer;

/**
 * Created by dq
 * <p>
 * on 2018/7/18
 * <p>
 * desc:
 */
public interface OnVideoViewEvent {

    void onPlayReadyCallback();

    void onPauseCallback();

    void onPlayPositionCallback(int position);

    void onResetPlayCallback();

    void onPlayErrorCallback();

    void onRePlayCallbck();

    void onFrameChangeCallback(long currentFrame, long oldFrame, long totalFrame);

    void showExtraContent();
}
