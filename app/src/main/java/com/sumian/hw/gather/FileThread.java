package com.sumian.hw.gather;


import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.sumian.blue.manager.BlueManager;
import com.sumian.hw.common.util.StreamUtil;
import com.sumian.hw.network.callback.BaseResponseCallback;
import com.sumian.hw.network.request.RawDataBody;
import com.sumian.hw.network.request.UploadFileBody;
import com.sumian.hw.network.response.FileLength;
import com.sumian.hw.network.response.RawData;
import com.sumian.sleepdoctor.app.AppManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * Created by jzz
 * on 2017/10/20.
 * desc:
 */

public class FileThread extends HandlerThread {

    private static final String TAG = "OkHttp";

    private Handler mHandler;

    private long mEmgDelayMills = 6 * 1000L;
    private long mPulseDelayMills = 8 * 1000L;
    private long mSpeedDelayMills = 10 * 1000L;

    private long mUnixTime;

    FileThread() {
        super("FileThread");
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();

        this.mHandler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0xd1://读取emg文件

                        long serverEmgFileLen = (long) msg.obj;
                        // Log.e(TAG, "handleMessage: ----->emgLength=" + serverEmgFileLen + "  unixTime=" + mUnixTime);

                        File emgFile = FileHelper.getEmgFile();
                        readFile(0xd1, "emg", emgFile, serverEmgFileLen, mEmgDelayMills);

                        break;
                    case 0xd2://读取pulse文件

                        long serverPulseFileLen = (long) msg.obj;
                        //  Log.e(TAG, "handleMessage: ----->pulseLength=" + serverPulseFileLen + "  unixTime=" + mUnixTime);

                        File pulseFile = FileHelper.getPulseFile();
                        readFile(0xd2, "pulse", pulseFile, serverPulseFileLen, mPulseDelayMills);

                        break;
                    case 0xd3://读取speed文件

                        long serverSpeedFileLen = (long) msg.obj;
                        //  Log.e(TAG, "handleMessage: ----->speedFileLen=" + serverSpeedFileLen + "  unixTime=" + mUnixTime);

                        File speedFile = FileHelper.getSpeedFile();
                        readFile(0xd3, "speed", speedFile, serverSpeedFileLen, mSpeedDelayMills);

                        break;
                }
            }
        };

        FileHelper.createEmgFile();
        FileHelper.createPulseFile();
        FileHelper.createSpeedFile();

        getServerFileLength();
    }

    private void readFile(int what, String dataType, File File, long serverFileLength, long delayMills) {

        RandomAccessFile accessFile = null;
        try {
            accessFile = new RandomAccessFile(File, "rw");
            long localLength = accessFile.length();
            // Log.e(TAG, "readFile: ---------开始采集数据------->dataType=" + dataType + " 本地数据长度=" + localLength + "  服务器数据长度=" + serverFileLength);

            if (localLength <= 0) {
                StreamUtil.close(accessFile);
                Message message = Message.obtain();
                message.what = what;
                message.obj = serverFileLength;
                mHandler.sendMessageDelayed(message, delayMills);
                // Log.e(TAG, "readFile: ---------本地没有数据------->dataType=" + dataType + "    本地数据长度=" + localLength);

                return;
            }
            accessFile.seek(serverFileLength);

            long currentTimeMillis = System.currentTimeMillis();

            List<String> data = new ArrayList<>();

            while (true) {

                if (System.currentTimeMillis() - currentTimeMillis == 2000) {
                    // Log.e(TAG, "readFile: ------2s 采集一次完成----->");
                    break;
                }
                String readLine = accessFile.readLine();

                if (TextUtils.isEmpty(readLine) || "".equals(readLine)) {
                    continue;
                }
                //Log.e(TAG, "readFile: ------>" + readLine);

                data.add(readLine.trim());
            }

            if (data.isEmpty()) {
                StreamUtil.close(accessFile);
                Message message = Message.obtain();
                message.what = what;
                message.obj = serverFileLength;
                // Log.e(TAG, "readFile------本地没有数据--->" + serverFileLength + " dataType=" + dataType);
                mHandler.sendMessageDelayed(message, delayMills);
                return;
            }

            uploadFile(what, dataType, delayMills, data);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            StreamUtil.close(accessFile);
        }
    }

    private void uploadFile(int what, String dataType, long delayMills, List<String> data) {

        if (!BlueManager.init().getBluePeripheral().isConnected()) return;

        Call<RawData> emgCall = AppManager
            .getHwNetEngine()
            .getHttpService()
            .uploadRawData(new RawDataBody()
                .setType(dataType)
                .setCmd_created_at(String.valueOf(mUnixTime))
                .setData(data)
                .setMac(BlueManager.init().getBluePeripheral().getMac()));

        emgCall.enqueue(new BaseResponseCallback<RawData>() {
            @Override
            protected void onSuccess(RawData response) {
                long fileLength = response.getFile_length();
                Message message = Message.obtain();
                message.what = what;
                message.obj = fileLength;
                //Log.e(TAG, "onSuccess: ------>" + fileLength + " dataType=" + dataType);
                mHandler.sendMessageDelayed(message, delayMills);
            }

            @Override
            protected void onFailure(String error) {
                // Log.e(TAG, "onFailure: ------->" + error);
                uploadFile(what, dataType, delayMills, data);
            }

            @Override
            protected void onFinish() {

            }
        });
    }

    private void getServerFileLength() {

        List<UploadFileBody> uploadFileBodies = new ArrayList<>();
        UploadFileBody emgFileBody = new UploadFileBody()
            .setType("emg")
            .setCmd_created_at(String.valueOf(mUnixTime))
            .setMac(BlueManager.init().getBluePeripheral().getMac());

        uploadFileBodies.add(emgFileBody);

        UploadFileBody pulseFileBody = new UploadFileBody()
            .setType("pulse")
            .setCmd_created_at(String.valueOf(mUnixTime))
            .setMac(BlueManager.init().getBluePeripheral().getMac());

        uploadFileBodies.add(pulseFileBody);

        UploadFileBody speedFileBody = new UploadFileBody()
            .setType("speed")
            .setCmd_created_at(String.valueOf(mUnixTime))
            .setMac(BlueManager.init().getBluePeripheral().getMac());

        uploadFileBodies.add(speedFileBody);

        Call<List<FileLength>> emgCall = AppManager
            .getHwNetEngine()
            .getHttpService()
            .getRawFileLength(uploadFileBodies);

        emgCall.enqueue(new BaseResponseCallback<List<FileLength>>() {

            @Override
            protected void onSuccess(List<FileLength> response) {

                int msgWhat = 0;
                long fileLength;
                long delayMills = 0;
                for (FileLength fileLengthResponse : response) {
                    fileLength = fileLengthResponse.getFile_length();
                    switch (fileLengthResponse.getType()) {
                        case "emg":
                            msgWhat = 0xd1;
                            delayMills = mEmgDelayMills;
                            break;
                        case "pulse":
                            msgWhat = 0xd2;
                            delayMills = mPulseDelayMills;
                            break;
                        case "speed":
                            msgWhat = 0xd3;
                            delayMills = mSpeedDelayMills;
                            break;
                    }
                    tryReadFile(msgWhat, fileLength, delayMills);
                }

            }

            @Override
            protected void onFailure(String error) {

            }

            @Override
            protected void onFinish() {

            }
        });
    }

    void setUnixTime(long unixTime) {
        mUnixTime = unixTime;
    }

    private void tryReadFile(int what, long serverFileLength, long delayMills) {
        Handler handler = mHandler;
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = serverFileLength;
        handler.sendMessageDelayed(msg, delayMills);
    }


    public void release() {
        mHandler.removeMessages(0xd1);
        mHandler.removeMessages(0xd2);
        mHandler.removeMessages(0xd3);
        quitSafely();
    }
}
