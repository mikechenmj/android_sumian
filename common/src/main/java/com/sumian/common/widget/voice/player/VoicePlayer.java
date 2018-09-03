package com.sumian.common.widget.voice.player;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import java.io.IOException;

/**
 * Created by jzz
 * on 2018/1/4.
 * <p>
 * desc:单例播放器
 */

public class VoicePlayer implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener, Handler.Callback {

    private static volatile VoicePlayer INSTANCE = null;

    private static final int MSG_WHAT_GAP_TIMER = 0x01;

    private MediaPlayer mMediaPlayer;
    private int currentPosition;

    private onPlayStatusListener mStatusListener;

    private Handler mHandler = new Handler(Looper.getMainLooper(), this);

    public static VoicePlayer getInstance() {
        if (INSTANCE == null) {
            synchronized (VoicePlayer.class) {
                if (INSTANCE == null) {
                    INSTANCE = new VoicePlayer();
                }
            }
        }
        return INSTANCE;
    }

    private VoicePlayer() {

        if (mMediaPlayer == null) {
            MediaPlayer player = new MediaPlayer();
            player.setLooping(true);
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setOnCompletionListener(this);
            player.setOnPreparedListener(this);
            player.setOnErrorListener(this);
            player.setOnSeekCompleteListener(this);
            mMediaPlayer = player;
        }
    }

    public VoicePlayer setStatusListener(onPlayStatusListener statusListener) {
        mStatusListener = statusListener;
        return this;
    }

    public int getCurrentPlayPosition() {
        return mMediaPlayer.getCurrentPosition() / 1000;
    }


    public int getDuration() {
        return mMediaPlayer.getDuration() / 1000;
    }

    public void seekTo(int progress) {
        removeAllMsg();
        mMediaPlayer.seekTo(progress * 1000);
    }

    public void pause() {
        removeAllMsg();
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
    }

    public void release() {
        removeAllMsg();
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            mMediaPlayer.stop();
        }
        if (mStatusListener != null) {
            mStatusListener.stop();
        }
        //mMediaPlayer.release();
    }

    public VoicePlayer play(String filePath, int position) {
        return play(filePath, AudioManager.STREAM_MUSIC, position);
    }

    public VoicePlayer play(String filePath, int stream, int position) {
        synchronized (this) {
            if (TextUtils.isEmpty(filePath)) {
                if (mStatusListener != null) {
                    this.mStatusListener.stop();
                }
                return this;
            }

            MediaPlayer player = this.mMediaPlayer;
            if (position != currentPosition) {
                currentPosition = position;
                if (mStatusListener != null) {
                    this.mStatusListener.stop();
                }
                play(filePath, stream, player);
            } else {
                if (player.isPlaying()) {
                    player.pause();
                    player.stop();
                    if (mStatusListener != null) {
                        this.mStatusListener.stop();
                    }
                } else {
                    currentPosition = position;
                    play(filePath, stream, player);
                }
            }
        }
        return this;
    }

    private void play(String filePath, int stream, MediaPlayer player) {
        try {
            player.reset();
            player.setAudioStreamType(stream);
            player.setDataSource(filePath);
            player.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onCompletion(MediaPlayer mp) {
        mp.stop();
        if (mStatusListener == null) return;
        this.mStatusListener.stop();
        removeAllMsg();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        sendTimerMsg();
        if (mStatusListener == null) return;
        this.mStatusListener.play();
        mp.start();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        release();
        if (mStatusListener != null) {
            this.mStatusListener.stop();
        }
        return false;
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        if (!mp.isPlaying()) {
            mp.start();
        }
        sendTimerMsg();
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_WHAT_GAP_TIMER:
                notifyProgress();
                sendGapTimerMsg();
                break;
        }
        return true;
    }

    private void notifyProgress() {
        if (mStatusListener != null) {
            mStatusListener.onProgressCallback(getDuration(), getCurrentPlayPosition());
        }
    }

    public interface onPlayStatusListener {

        void play();

        void stop();

        void onProgressCallback(int duration, int progress);

    }

    private void removeAllMsg() {
        mHandler.removeCallbacksAndMessages(null);
    }

    private void sendTimerMsg() {
        removeAllMsg();
        notifyProgress();
        mHandler.sendEmptyMessage(MSG_WHAT_GAP_TIMER);
    }

    private void sendGapTimerMsg() {
        Message gapMsg = Message.obtain();
        gapMsg.what = MSG_WHAT_GAP_TIMER;
        mHandler.sendMessageDelayed(gapMsg, 1000L);
    }

}
