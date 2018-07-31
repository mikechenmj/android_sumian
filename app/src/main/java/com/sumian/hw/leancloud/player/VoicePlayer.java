package com.sumian.hw.leancloud.player;

import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Created by jzz
 * on 2018/1/4.
 * <p>
 * desc:
 */

public class VoicePlayer implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    private MediaPlayer mMediaPlayer;
    private int currentPosition;

    private onPlayStatusListener mStatusListener;

    public VoicePlayer() {
        if (mMediaPlayer == null) {
            MediaPlayer player = new MediaPlayer();
            player.setLooping(false);
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setOnCompletionListener(this);
            player.setOnPreparedListener(this);
            player.setOnErrorListener(this);
            mMediaPlayer = player;
        }
    }

    public VoicePlayer setStatusListener(onPlayStatusListener statusListener) {
        mStatusListener = statusListener;
        return this;
    }

    public void release() {
        mMediaPlayer.release();
    }

    public VoicePlayer play(String filePath, int position) {
        return play(filePath, AudioManager.STREAM_MUSIC, position);
    }

    public VoicePlayer play(String filePath, int stream, int position) {
        MediaPlayer player = this.mMediaPlayer;
        if (position != currentPosition) {
            currentPosition = position;
            if (mStatusListener != null) {
                this.mStatusListener.stop();
            }
            play(filePath, stream, player);
        } else {
            if (player.isPlaying()) {
                player.stop();
                if (mStatusListener != null) {
                    this.mStatusListener.stop();
                }
            } else {
                currentPosition = position;
                play(filePath, stream, player);
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
        mp.reset();
        if (mStatusListener == null) return;
        this.mStatusListener.stop();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        if (mStatusListener == null) return;
        this.mStatusListener.play();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        release();
        return false;
    }

    public interface onPlayStatusListener {

        void play();

        void stop();

    }

}
