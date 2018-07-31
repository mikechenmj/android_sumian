package com.sumian.app.widget;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnDismissListener;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.im.v2.audio.AVIMAudioRecorder;
import com.sumian.app.R;
import com.sumian.app.common.helper.ToastHelper;
import com.sumian.app.leancloud.utils.LCIMAudioHelper;
import com.sumian.app.leancloud.utils.LCIMPathUtils;

import java.io.File;

//import android.media.MediaRecorder;

/**
 * 录音的按钮
 */
public class LCIMRecordButton extends AppCompatButton implements Runnable {

    public static final int BACK_RECORDING = R.drawable.bg_keyboard;
    public static final int BACK_IDLE = R.drawable.bg_keyboard;
    public static final int SLIDE_UP_TO_CANCEL = 0;
    public static final int RELEASE_TO_CANCEL = 1;
    private static final int MIN_INTERVAL_TIME = 1000;
    private static int[] recordImageIds = {R.mipmap.info_icon_unloadimage_1,
        R.mipmap.info_icon_unloadimage_2, R.mipmap.info_icon_unloadimage_3,
        R.mipmap.info_icon_unloadimage_4, R.mipmap.info_icon_unloadimage_5,
        R.mipmap.info_icon_unloadimage_6, R.mipmap.info_icon_unloadimage_7, R.mipmap.info_icon_unloadimage_8};
    private TextView textView;
    private String outputPath = null;
    private RecordEventListener recordEventListener;
    private long startTime;
    private Dialog recordIndicator;
    private AVIMAudioRecorder audioRecorder;
    private ObtainDecibelThread thread;
    private Handler volumeHandler;
    private ImageView imageView;
    private int status;
    private OnDismissListener onDismiss = dialog -> stopRecording();

    private OnCheckRecordPermission mOnCheckRecordPermission;


    public LCIMRecordButton(Context context) {
        super(context);
        init();
    }

    public LCIMRecordButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public LCIMRecordButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LCIMRecordButton setOnCheckRecordPermission(OnCheckRecordPermission onCheckRecordPermission) {
        mOnCheckRecordPermission = onCheckRecordPermission;
        return this;
    }

    public void setSavePath(String path) {
        outputPath = path;
    }

    public void setRecordEventListener(RecordEventListener listener) {
        recordEventListener = listener;
    }

    private void init() {
        volumeHandler = new ShowVolumeHandler();
        setBackgroundResource(BACK_IDLE);
        setActivated(false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (outputPath == null)
            return false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startRecord();
                break;
            case MotionEvent.ACTION_UP:
                if (status == RELEASE_TO_CANCEL) {
                    cancelRecord();
                } else {
                    finishRecord();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getY() < 0) {
                    status = RELEASE_TO_CANCEL;
                } else {
                    status = SLIDE_UP_TO_CANCEL;
                }
                setTextViewByStatus();
                break;
            case MotionEvent.ACTION_CANCEL:
                cancelRecord();
                break;
            default:
                break;
        }
        return true;
    }

    public int getColor(int id) {
        return getContext().getResources().getColor(id);
    }

    private void setTextViewByStatus() {
        if (status == RELEASE_TO_CANCEL) {
            textView.setTextColor(getColor(R.color.lcim_commom_read));
            textView.setText(R.string.lcim_chat_record_button_releaseToCancel);
        } else if (status == SLIDE_UP_TO_CANCEL) {
            textView.setTextColor(Color.WHITE);
            textView.setText(R.string.lcim_chat_record_button_slideUpToCancel);
        }
    }

    private void startRecord() {

        LCIMAudioHelper.getInstance().stopPlayer();
        initRecordDialog();
        startTime = System.currentTimeMillis();
        setBackgroundResource(BACK_RECORDING);
        setActivated(true);

        if (mOnCheckRecordPermission == null || !mOnCheckRecordPermission.onPermissionCallback() ||
            ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ToastHelper.show(R.string.permissions_record_error);
            return;
        }
        startRecording();
        recordIndicator.show();
    }

    @SuppressWarnings("ConstantConditions")
    private void initRecordDialog() {
        if (null == recordIndicator) {
            recordIndicator = new Dialog(getContext(), R.style.lcim_record_dialog_style);
            View view = inflate(getContext(), R.layout.hw_lcim_chat_record_layout, null);
            imageView = (ImageView) view.findViewById(R.id.imageView);
            textView = (TextView) view.findViewById(R.id.textView);
            recordIndicator.setContentView(view, new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
            recordIndicator.setOnDismissListener(onDismiss);

            LayoutParams lp = recordIndicator.getWindow().getAttributes();
            lp.gravity = Gravity.CENTER;
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void removeFile() {
        File file = new File(outputPath);
        if (file.exists()) {
            file.delete();
        }
    }

    private void finishRecord() {
        stopRecording();
        recordIndicator.dismiss();
        setBackgroundResource(BACK_IDLE);
        setActivated(false);
    }

    private void cancelRecord() {
        stopRecording();
        setBackgroundResource(BACK_IDLE);
        setActivated(false);
        recordIndicator.dismiss();
        Toast.makeText(getContext(), getContext().getString(R.string.lcim_chat_cancelRecord), Toast.LENGTH_SHORT).show();
        removeFile();
    }

    private void startRecording() {
        outputPath = LCIMPathUtils.getRecordPathByCurrentTime(getContext());
        try {
            if (null == audioRecorder) {
                final String localFilePath = outputPath;
                audioRecorder = new AVIMAudioRecorder(localFilePath, new AVIMAudioRecorder.RecordEventListener() {
                    @Override
                    public void onFinishedRecord(long milliSeconds, String reason) {
                        if (status == RELEASE_TO_CANCEL) {
                            removeFile();
                        } else if (null != recordEventListener) {
                            if (milliSeconds < MIN_INTERVAL_TIME) {
                                Toast.makeText(getContext(), getContext().getString(R.string.lcim_chat_record_button_pleaseSayMore), Toast.LENGTH_SHORT).show();
                                removeFile();
                            } else {
                                recordEventListener.onFinishedRecord(localFilePath, Math.round(milliSeconds / 1000));
                                outputPath = LCIMPathUtils.getRecordPathByCurrentTime(getContext());
                            }
                        }
                    }

                    @Override
                    public void onStartRecord() {
                        if (null != recordEventListener) {
                            recordEventListener.onStartRecord();
                            postDelayed(LCIMRecordButton.this, 60 * 1000L);
                        }
                    }
                });
            }
            audioRecorder.start();
            thread = new ObtainDecibelThread();
            thread.start();
            recordEventListener.onStartRecord();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void stopRecording() {
        if (thread != null) {
            thread.exit();
            thread = null;
        }
        if (audioRecorder != null) {
            audioRecorder.stop();
            audioRecorder = null;
        }
    }

    @Override
    public void run() {
        finishRecord();
        ToastHelper.show(getContext().getString(R.string.record_duration_only_60));
    }

    public interface RecordEventListener {

        void onFinishedRecord(String audioPath, int secs);

        void onStartRecord();
    }

    private class ObtainDecibelThread extends Thread {
        private volatile boolean running = true;

        public void exit() {
            running = false;
        }

        @Override
        public void run() {
            while (running) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (audioRecorder == null || !running) {
                    break;
                }
                int x = audioRecorder.getMaxAmplitude();
                if (x != 0) {
                    int f = (int) (10 * Math.log(x) / Math.log(10));
                    int index = (f - 18) / 5;
                    if (index < 0) index = 0;
                    if (index > 5) index = 5;
                    volumeHandler.sendEmptyMessage(index);
                }
            }
        }

    }

    @SuppressLint("HandlerLeak")
    class ShowVolumeHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            imageView.setImageResource(recordImageIds[msg.what]);
        }
    }


    public interface OnCheckRecordPermission {

        boolean onPermissionCallback();
    }

}
