package com.sumian.sleepdoctor.cbti.video;

import android.content.Context;
import android.net.Uri;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.aliyun.vodplayer.media.AliyunPlayAuth;
import com.aliyun.vodplayer.media.AliyunVodPlayer;
import com.aliyun.vodplayer.media.IAliyunVodPlayer;

import java.io.FileDescriptor;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * Created by dq
 * <p>
 * on 2018/7/18
 * <p>
 * desc:
 */
public class AliyunPlayer extends AbstractMediaPlayer {

    private AliyunVodPlayer mAliyunPlayer;

    private AliyunPlayAuth mAliyunPlayAuth;
    private boolean mIsLooping;

    public AliyunPlayer(Context context) {
        this.mAliyunPlayer = new AliyunVodPlayer(context);
        //this.mAliyunPlayer.enableNativeLog();
        this.mAliyunPlayer.setThreadExecutorService(Executors.newSingleThreadExecutor());
        this.mAliyunPlayer.setVideoScalingMode(IAliyunVodPlayer.VideoScalingMode.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
        // this.mAliyunPlayer.setUiPlayer(true);
    }

    @Override
    public void setDisplay(SurfaceHolder surfaceHolder) {
        mAliyunPlayer.setDisplay(surfaceHolder);
    }

    @Override
    public void setDataSource(Context var1, Uri var2) throws IllegalArgumentException, SecurityException, IllegalStateException {

    }

    @Override
    public void setDataSource(Context var1, Uri var2, Map<String, String> var3) throws IllegalArgumentException, SecurityException, IllegalStateException {
    }

    @Override
    public void setDataSource(FileDescriptor var1) throws IllegalArgumentException, IllegalStateException {

    }

    @Override
    public void setDataSource(String var1) throws IllegalArgumentException, SecurityException, IllegalStateException {

    }

    @Override
    public void setDataSource(String vid, String playAuth) {
        AliyunPlayAuth.AliyunPlayAuthBuilder playAuthBuilder = new AliyunPlayAuth.AliyunPlayAuthBuilder();
        playAuthBuilder.setVid(vid);
        playAuthBuilder.setPlayAuth(playAuth);
        playAuthBuilder.setQuality(IAliyunVodPlayer.QualityValue.QUALITY_LOW);
        this.mAliyunPlayAuth = playAuthBuilder.build();
    }

    @Override
    public String getDataSource() {
        return null;
    }

    @Override
    public void prepareAsync() throws IllegalStateException {
        this.mAliyunPlayer.prepareAsync(mAliyunPlayAuth);
    }

    @Override
    public void start() throws IllegalStateException {
        this.mAliyunPlayer.start();
    }

    @Override
    public void stop() throws IllegalStateException {
        this.mAliyunPlayer.stop();

    }

    @Override
    public void pause() throws IllegalStateException {
        this.mAliyunPlayer.pause();
    }

    @Override
    public void setScreenOnWhilePlaying(boolean var1) {
        this.mAliyunPlayer.setScreenBrightness(100);
    }

    @Override
    public int getVideoWidth() {
        return this.mAliyunPlayer.getVideoWidth();
    }

    @Override
    public int getVideoHeight() {
        return this.mAliyunPlayer.getVideoHeight();
    }

    @Override
    public boolean isPlaying() {
        return this.mAliyunPlayer.isPlaying();
    }

    @Override
    public void seekTo(long var1) throws IllegalStateException {
        this.mAliyunPlayer.seekTo((int) var1);
    }

    @Override
    public long getCurrentPosition() {
        return this.mAliyunPlayer.getCurrentPosition();
    }

    @Override
    public long getDuration() {
        return this.mAliyunPlayer.getDuration();
    }

    @Override
    public void release() {
        this.mAliyunPlayer.release();
    }

    @Override
    public void reset() {
        this.mAliyunPlayer.reset();
    }

    @Override
    public void replay() {
        this.mAliyunPlayer.replay();
    }

    @Override
    public void setVolume(float var1, float var2) {
        this.mAliyunPlayer.setVolume((int) var1);
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    public void setOnPreparedListener(final OnPreparedListener var1) {
        if (var1 != null) {
            this.mAliyunPlayer.setOnPreparedListener(new IAliyunVodPlayer.OnPreparedListener() {
                @Override
                public void onPrepared() {
                    var1.onPrepared(AliyunPlayer.this);
                }
            });
        }
    }

    @Override
    public void setOnCompletionListener(final OnCompletionListener var1) {
        if (var1 != null) {
            this.mAliyunPlayer.setOnCompletionListener(new IAliyunVodPlayer.OnCompletionListener() {
                @Override
                public void onCompletion() {
                    var1.onCompletion(AliyunPlayer.this);
                }
            });
        }
    }

    @Override
    public void setOnBufferingUpdateListener(final OnBufferingUpdateListener var1) {
        if (var1 != null) {
            this.mAliyunPlayer.setOnBufferingUpdateListener(new IAliyunVodPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(int i) {
                    var1.onBufferingUpdate(AliyunPlayer.this, i);
                }
            });
        }
    }

    @Override
    public void setOnSeekCompleteListener(final OnSeekCompleteListener var1) {
        if (var1 != null) {
            this.mAliyunPlayer.setOnSeekCompleteListener(new IAliyunVodPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete() {
                    var1.onSeekComplete(AliyunPlayer.this);
                }
            });
        }
    }

    @Override
    public void setOnVideoSizeChangedListener(final OnVideoSizeChangedListener var1) {
        if (var1 != null) {
            this.mAliyunPlayer.setOnVideoSizeChangedListener(new IAliyunVodPlayer.OnVideoSizeChangedListener() {
                @Override
                public void onVideoSizeChanged(int i, int i1) {
                    var1.onVideoSizeChanged(AliyunPlayer.this, i, i1, i, i1);
                }
            });
        }
    }

    @Override
    public void setOnErrorListener(final OnErrorListener var1) {
        if (var1 != null) {
            this.mAliyunPlayer.setOnErrorListener(new IAliyunVodPlayer.OnErrorListener() {
                @Override
                public void onError(int i, int i1, String s) {
                    var1.onError(AliyunPlayer.this, i, i1);
                }
            });
        }
    }

    @Override
    public void setOnInfoListener(final OnInfoListener var1) {
        if (var1 != null) {
            this.mAliyunPlayer.setOnInfoListener(new IAliyunVodPlayer.OnInfoListener() {
                @Override
                public void onInfo(int i, int i1) {
                    var1.onInfo(AliyunPlayer.this, i, i1);
                }
            });
        }
    }

    @Override
    public void setOnTimedTextListener(final OnTimedTextListener var1) {
        if (var1 != null) {
            this.mAliyunPlayer.setOnUrlTimeExpiredListener(new IAliyunVodPlayer.OnUrlTimeExpiredListener() {
                @Override
                public void onUrlTimeExpired(String s, String s1) {
                    var1.onTimedText(AliyunPlayer.this, s + "+++" + s1);
                }
            });
        }

    }

    @Override
    public void setAudioStreamType(int var1) {
    }

    @Override
    public void setKeepInBackground(boolean var1) {

    }

    @Override
    public int getVideoSarNum() {
        return 0;
    }

    @Override
    public int getVideoSarDen() {
        return 0;
    }

    @Override
    public void setWakeMode(Context var1, int var2) {

    }

    @Override
    public void setLooping(boolean var1) {
        this.mIsLooping = var1;
        this.mAliyunPlayer.setCirclePlay(var1);
    }

    @Override
    public boolean isLooping() {
        return this.mIsLooping;
    }

    @Override
    public void setSurface(Surface var1) {
        this.mAliyunPlayer.setSurface(var1);
    }

}
