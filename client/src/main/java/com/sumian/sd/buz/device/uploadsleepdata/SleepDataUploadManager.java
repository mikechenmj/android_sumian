package com.sumian.sd.buz.device.uploadsleepdata;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.text.TextUtils;

import com.blankj.utilcode.util.SPUtils;
import com.google.gson.reflect.TypeToken;
import com.sumian.common.utils.JsonUtil;
import com.sumian.common.utils.SumianExecutor;
import com.sumian.sd.app.AppManager;
import com.sumian.sd.buz.account.bean.UserInfo;
import com.sumian.sd.buz.devicemanager.DeviceManager;
import com.sumian.sd.buz.devicemanager.FileHelper;
import com.sumian.sd.common.log.LogManager;
import com.sumian.sd.common.utils.EventBusUtil;
import com.sumian.sd.common.utils.StreamUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.net.ConnectivityManagerCompat;

/**
 * Created by jzz
 * on 2017/12/19.
 * desc:
 */

public class SleepDataUploadManager {
    private static final String SP_NAME = "SyncSleepDataTask";
    private static final String SP_KEY_PENDING_TASKS = "pending_tasks";
    private static final int MAX_RETRY_TIME = 3;
    private volatile List<SleepDataUploadTask> mSleepDataUploadTasks = new ArrayList<>(0);
    private volatile boolean mIsBusy;
    private volatile boolean mIsNetworkEnable;
    private BroadcastReceiver mNetworkReceiver;
    private Handler mHandler = new Handler();
    private int mRetryTime = 0;
    private SleepDataUploadTask.TaskCallback mTaskCallback = new SleepDataUploadTask.TaskCallback() {

        @Override
        public void executeTaskFinish(SleepDataUploadTask sleepDataUploadTask, boolean isSuccess, boolean retry, @Nullable String message) {
            EventBusUtil.postStickyEvent(new UploadSleepDataFinishedEvent(isSuccess));
            DeviceManager.INSTANCE.postIsUploadingSleepDataToServer(false);
            mIsBusy = false;
            mSleepDataUploadTasks.remove(sleepDataUploadTask);
            if (retry) {
                mSleepDataUploadTasks.add(sleepDataUploadTask);
            } else {
                deleteSleepDataFile(sleepDataUploadTask);
            }
            persistPendingTask();
            if (retry) {
                mRetryTime++;
                if (mRetryTime <= MAX_RETRY_TIME) {
                    startNextTaskIfPossible();
                }
            } else {
                startNextTaskIfPossible();
            }
        }
    };

    public SleepDataUploadManager(Context context) {
        loadCacheJobTaskAndRun();
        registerNetworkStateChangeListener(context);
    }

    public void checkPendingTaskAndRun() {
        mRetryTime = 0;
        startNextTaskIfPossible();
    }

    /**
     * 注册网络状态监听,网络变为可用时，开始执行任务队列；网络变为不可用时，将mJobTasks序列化到磁盘中。
     *
     * @param context 用于注册监听
     */
    @SuppressWarnings({"ConstantConditions", "deprecation"})
    private void registerNetworkStateChangeListener(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (mNetworkReceiver == null) {
            mNetworkReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (TextUtils.isEmpty(action)) return;
                    switch (action) {
                        case ConnectivityManager.CONNECTIVITY_ACTION:
                            NetworkInfo networkInfo = ConnectivityManagerCompat.getNetworkInfoFromBroadcast(connectivityManager, intent);
                            if (networkInfo != null && networkInfo.isConnected() && networkInfo.isAvailable()) {
                                onNetworkStateChange(true);
                            } else {
                                onNetworkStateChange(false);
                            }
                            break;
                        default:
                            break;
                    }
                }
            };
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(mNetworkReceiver, filter);
    }

    private void onNetworkStateChange(boolean isNetworkEnable) {
        mIsNetworkEnable = isNetworkEnable;
        if (isNetworkEnable) {
            LogManager.appendTransparentLog("收到网络变化情况,网络可用  开始加载本地缓存的透传队列,并执行...");
            loadCacheJobTaskAndRun();
        } else {
            LogManager.appendTransparentLog("收到网络变化情况,网络不可用  缓存透传队列到本地..." + mSleepDataUploadTasks.toString());
            persistPendingTask();
        }
    }

    /**
     * 退出时，反注册监听，序列化未完成的task
     */
    public void release() {
        persistPendingTask();
    }

    private void loadCacheJobTaskAndRun() {
        String pendingTaskJson = SPUtils.getInstance(SP_NAME).getString(SP_KEY_PENDING_TASKS);
        List<SleepDataUploadTask> list = JsonUtil.fromJson(pendingTaskJson, new TypeToken<List<SleepDataUploadTask>>() {
        }.getType());
        if (list != null) {
            mSleepDataUploadTasks = list;
        }
        startNextTaskIfPossible();
    }

    private void addTaskAndRun(SleepDataUploadTask sleepDataUploadTask) {
        LogManager.appendTransparentLog("透传任务加入队列");
        if (!mSleepDataUploadTasks.contains(sleepDataUploadTask)) {
            mSleepDataUploadTasks.add(sleepDataUploadTask);
        } else {
            LogManager.appendTransparentLog("重复添加任务： " + sleepDataUploadTask.toString());
        }
        persistPendingTask();
        startNextTaskIfPossible();
    }

    private void persistPendingTask() {
        SPUtils.getInstance(SP_NAME).put(SP_KEY_PENDING_TASKS, JsonUtil.toJson(mSleepDataUploadTasks));
    }

    /**
     * 获取队列当中的第一个元素
     *
     * @return 下一个要执行的任务
     */
    private SleepDataUploadTask getNextTask() {
        return mSleepDataUploadTasks.isEmpty() ? null : mSleepDataUploadTasks.get(0);
    }

    private void startNextTaskIfPossible() {
        if (mIsBusy) {
            return;
        }
        if (!mIsNetworkEnable) {
            LogManager.appendTransparentLog("网络不可用，直接缓存任务到本地,等待网络可用,重启任务队列");
            return;
        }
        SleepDataUploadTask nextTask = getNextTask();
        if (nextTask == null) {
            LogManager.appendTransparentLog("上传队列中所有透传任务已全部执行完成");
            return;
        }
        mIsBusy = true;
        nextTask.setTaskCallback(mTaskCallback).execute();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void deleteSleepDataFile(SleepDataUploadTask sleepDataUploadTask) {
        File file = new File(sleepDataUploadTask.filePath);
        if (file.exists()) {
            file.delete();
        }
    }

    public void saveSleepData(ArrayList<String> sleepData, int type, String beginCmd, String endCmd, String monitorSn, String speedSleepSn, long receiveStartedTime, long receiveEndedTime) {
        SleepDataBean sleepDataBean = new SleepDataBean(sleepData, beginCmd, endCmd, monitorSn, speedSleepSn, receiveStartedTime, receiveEndedTime, type);
        SumianExecutor.INSTANCE.runOnBackgroundThread(() -> {
            List<String> commands = sleepDataBean.commands;
            if (commands == null || commands.isEmpty()) {
                return;
            }
            // append beginCmd and endCmd to sleep data list
            commands.add(0, sleepDataBean.beginCmd);
            commands.add(sleepDataBean.endCmd);
            // create sleep file
            File sleepFile = createSleepDataFile(sleepDataBean.endCmd);
            if (sleepFile == null) {
                return;
            }
            // write file
            boolean writeResult = writeCommandsToFile(commands, sleepFile);
            if (!writeResult) {
                return;
            }
            // create and add SleepDataUploadTask
            SleepDataUploadTask sleepDataUploadTask = new SleepDataUploadTask(sleepFile.getAbsolutePath(),
                    sleepDataBean.beginCmd, sleepDataBean.endCmd,
                    sleepDataBean.monitorSn, sleepDataBean.speedSleeperSn,
                    sleepDataBean.receiveStartedTime, sleepDataBean.receiveEndedTime,
                    sleepDataBean.type);
            mHandler.post(() -> addTaskAndRun(sleepDataUploadTask));
        });
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private File createSleepDataFile(String endCmd) {
        try {
            File sleepFile = getSleepDataFile(endCmd);
            if (sleepFile.exists()) {
                sleepFile.delete();
                sleepFile.createNewFile();
            }
            return sleepFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @NonNull
    private File getSleepDataFile(String endCmd) {
        File sleepFileDir = FileHelper.createSDDir(FileHelper.FILE_DIR);
        String fileName = getFileNameByCommand(endCmd);
        return new File(sleepFileDir, fileName);
    }

    @NonNull
    private String getFileNameByCommand(String endCmd) {
        int dataType = Integer.parseInt(endCmd.substring(4, 8), 16) >> 12;
        UserInfo userInfo = AppManager.getAccountViewModel().getUserInfo();
        int userId = userInfo.id;
        return dataType + "_" + endCmd.substring(10) +
                "_sn" + DeviceManager.INSTANCE.getMonitorSn() +
                "_" + userId +
                ".txt";
    }

    private boolean writeCommandsToFile(List<String> commands, File sleepFile) {
        FileWriter fileWriter = null;
        BufferedWriter writer = null;
        LogManager.appendTransparentLog("透传数据开始写入本地文件中" + sleepFile.getAbsolutePath() + "  file.length=" + sleepFile.length());
        try {
            fileWriter = new FileWriter(sleepFile.getPath(), false);
            writer = new BufferedWriter(fileWriter);
            for (int i = 0; i < commands.size(); i++) {
                String data = commands.get(i);
                writer.append(data).append("\r\n");
            }
            writer.flush();
            LogManager.appendTransparentLog("透传数据写入本地文件成功" + sleepFile.getAbsolutePath() + "  file.length=" + sleepFile.length());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            LogManager.appendTransparentLog(String.format("透传数据写入本地文件失败: %s", e.getMessage()));
        } finally {
            StreamUtil.close(writer, fileWriter);
        }
        return false;
    }

    private static class SleepDataBean {
        private List<String> commands;
        private String beginCmd;
        private String endCmd;
        private String monitorSn;
        private String speedSleeperSn;
        private long receiveStartedTime;//开始接收设备睡眠特征时间戳
        private long receiveEndedTime;//接收设备睡眠特征结束时间戳
        private int type;//透传数据的类型

        private SleepDataBean(List<String> commands, String beginCmd, String endCmd, String monitorSn, String speedSleeperSn, long receiveStartedTime, long receiveEndedTime, int type) {
            this.commands = commands;
            this.beginCmd = beginCmd;
            this.endCmd = endCmd;
            this.monitorSn = monitorSn;
            this.speedSleeperSn = speedSleeperSn;
            this.receiveStartedTime = receiveStartedTime;
            this.receiveEndedTime = receiveEndedTime;
            this.type = type;
        }
    }
}
