package com.sumian.common.widget.voice;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.Locale;

/**
 * Created by jzz
 * on 2018/1/4.
 * <p>
 * desc:
 */

public class VoicePlayer implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener, Handler.Callback, MediaPlayer.OnBufferingUpdateListener {

    private static final String TAG = VoicePlayer.class.getSimpleName();
    private static final int MSG_WHAT_GAP_TIMER = 0x01;
    private static final long PROGRESS_UPDATE_INTERVAL = 100L;

    private MediaPlayer mMediaPlayer;
    private onPlayStatusListener mStatusListener;
    private int mCurrentPosition = -1;
    private int mProgress = 0;
    private Handler mHandler = new Handler(Looper.getMainLooper(), this);

    public VoicePlayer() {
        if (mMediaPlayer == null) {
            MediaPlayer player = new MediaPlayer();
            player.setLooping(true);
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setOnCompletionListener(this);
            player.setOnPreparedListener(this);
            player.setOnErrorListener(this);
            player.setOnSeekCompleteListener(this);
            player.setOnBufferingUpdateListener(this);
            mMediaPlayer = player;
        }
    }

    public VoicePlayer setStatusListener(onPlayStatusListener statusListener) {
        mStatusListener = statusListener;
        return this;
    }

    public int getCurrentPlayPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    public int getDuration() {
        return mMediaPlayer.getDuration();
    }

    public void seekTo(int progress) {
        removeAllMsg();
        this.mProgress = progress;
        mMediaPlayer.seekTo(progress);
    }

    public void pause(int position) {
        removeAllMsg();
        if (isPlaying()) {
            this.mCurrentPosition = position;
            pause();
            notifyPause(position);
        }
    }

    public void release() {
        removeAllMsg();
        mMediaPlayer.release();
    }

    public VoicePlayer play(String filePath, int position, int progress) {
        Log.e(TAG, String.format(Locale.getDefault(), "play %d, %d, %s", position, progress, filePath));
        synchronized (this) {
            removeAllMsg();
            if (TextUtils.isEmpty(filePath)) {
                notifyPause(position);
                return this;
            }

            if (mCurrentPosition == position) {//如果是当前音频
                if (isPlaying()) {  //当前音频正在播放,那就暂停
                    mMediaPlayer.pause();
                    notifyPause(position);
                } else {//当前音频未在播放状态,直接播放该音频
                    //start();
                    seekTo(progress);
                    sendTimerMsg();
                    notifyPlaying();
                }

            } else {//不是当前音频,先关闭前一个如果正在播放的音频,然后里面播放当前该音频
                if (mCurrentPosition != -1) {
                    removeAllMsg();
                    notifyPrePause(mCurrentPosition);
                }
                mCurrentPosition = position;
                play(filePath, mMediaPlayer, progress);
            }
        }
        return this;
    }

    public void start() {
        mMediaPlayer.start();
    }

    public void pause() {
        if (mCurrentPosition == -1) {//未播放过音频
            return;
        }
        mMediaPlayer.pause();
    }

    public boolean isPlaying() {
        //try {
        return mMediaPlayer.isPlaying();
        //} catch (IllegalStateException e) {
        //   return false;
        // }
    }

    private void play(String filePath, MediaPlayer player, int progress) {
        this.mProgress = progress;
        removeAllMsg();
        notifyPrepare();
        try {
            player.reset();
            player.setDataSource(filePath);
            player.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        notifyComplete(mCurrentPosition);
        removeAllMsg();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        sendTimerMsg();
        if (mProgress > 0) {
            mp.seekTo(mProgress);
        } else {
            mp.start();
        }
        notifyPlaying();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        //release();
        Log.e(TAG, "onError: -------->what=" + what + "   extra=" + extra);
        notifyPause(mCurrentPosition);
        return false;
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        if (!mp.isPlaying()) {
            mp.start();
        }
        sendTimerMsg();
        Log.d(TAG, "onSeekComplete progress: " + getCurrentPlayPosition());
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        Log.e(TAG, "onBufferingUpdate: percent=" + percent);
    }


    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_WHAT_GAP_TIMER:
                Log.d(TAG, "notify progress");
                notifyProgress();
                sendGapTimerMsg();
                break;
            default:
                break;
        }
        return true;
    }

    private void notifyPrepare() {
        if (mStatusListener != null) {
            mStatusListener.onPrepareCallback(mCurrentPosition);
        }
    }

    private void notifyPlaying() {
        if (mStatusListener != null) {
            mStatusListener.onPlayCallback(mCurrentPosition);
        }
    }

    private void notifyPause(int position) {
        if (mStatusListener != null) {
            mStatusListener.onPauseCallback(position);
        }
    }

    private void notifyPrePause(int prePosition) {
        if (mStatusListener != null) {
//            mStatusListener.onPausePreCallback(prePosition, getCurrentPlayPosition());
            mStatusListener.onPausePreCallback(prePosition, 0);
        }
    }

    private void notifyComplete(int position) {
        if (mStatusListener != null) {
            mStatusListener.onCompleteCallback(position);
        }
    }

    private void notifyProgress() {
        // seek to complete 才notify，避免 getCurrentPosition = 0 闪烁
        if (mStatusListener != null) {
            mStatusListener.onProgressCallback(mCurrentPosition, getDuration(), getCurrentPlayPosition());
        }
    }

    public interface onPlayStatusListener {

        void onPrepareCallback(int position);

        void onPlayCallback(int position);

        void onPauseCallback(int position);

        void onPausePreCallback(int prePosition, int progress);

        void onProgressCallback(int position, int duration, int progress);

        void onCompleteCallback(int position);

    }

    private void removeAllMsg() {
        mHandler.removeCallbacksAndMessages(null);
    }

    private void sendTimerMsg() {
        removeAllMsg();
        mHandler.obtainMessage(MSG_WHAT_GAP_TIMER).sendToTarget();
    }

    private void sendGapTimerMsg() {
        Message gapMsg = Message.obtain();
        gapMsg.what = MSG_WHAT_GAP_TIMER;
        mHandler.sendMessageDelayed(gapMsg, PROGRESS_UPDATE_INTERVAL);
    }

}
