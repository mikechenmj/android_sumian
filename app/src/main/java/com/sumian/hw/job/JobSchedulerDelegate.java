package com.sumian.hw.job;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.net.ConnectivityManagerCompat;
import android.text.TextUtils;

import com.blankj.utilcode.util.SPUtils;
import com.google.gson.reflect.TypeToken;
import com.sumian.common.utils.JsonUtil;
import com.sumian.hw.log.LogManager;
import com.sumian.hw.utils.StreamUtil;
import com.sumian.sd.account.bean.UserInfo;
import com.sumian.sd.app.AppManager;
import com.sumian.sd.device.DeviceManager;
import com.sumian.sd.device.FileHelper;
import com.sumian.sd.event.EventBusUtil;
import com.sumian.sd.event.UploadSleepDataFinishedEvent;
import com.sumian.sd.utils.SumianExecutor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jzz
 * on 2017/12/19.
 * desc:
 * 管理文件上传任务。
 * JobTask队列会在内存中、磁盘中各放一份，在任务队列发生变化的时候同步两者。
 * JobServiceImpl初始化会从磁盘加载之前序列化的JobTask队列到内存中，然后取出第一个开始执行，每当任务执行成功，便从obTask队列中移除，并同步状态到磁盘中。
 */

@SuppressWarnings("ResultOfMethodCallIgnored")
public class JobSchedulerDelegate {
    private static final String SP_NAME = "SyncSleepDataTask";
    private static final String SP_KEY_PENDING_TASKS = "pending_tasks";

    private static volatile JobSchedulerDelegate INSTANCE;
    private volatile List<JobTask> mJobTasks = new ArrayList<>(0);
    private volatile boolean mIsBusy;
    private volatile boolean mIsNetworkEnable;
    private BroadcastReceiver mNetworkReceiver;
    private Handler mHandler = new Handler();
    private int mRetryTime = 0;
    private static final int MAX_RETRY_TIME = 3;

    public static JobSchedulerDelegate getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (JobSchedulerDelegate.class) {
                if (INSTANCE == null) {
                    INSTANCE = new JobSchedulerDelegate(context);
                }
            }
        }
        return INSTANCE;
    }

    private JobSchedulerDelegate(Context context) {
        loadCacheJobTaskAndRun();
        registerNetworkStateChangeListener(context);
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
            LogManager.appendTransparentLog("收到网络变化情况,网络不可用  缓存透传队列到本地..." + mJobTasks.toString());
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
        List<JobTask> list = JsonUtil.fromJson(pendingTaskJson, new TypeToken<List<JobTask>>() {
        }.getType());
        if (list != null) {
            mJobTasks = list;
        }
        startNextTaskIfPossible();
    }

    private void addTaskAndRun(JobTask jobTask) {
        LogManager.appendTransparentLog("透传任务加入队列");
        mJobTasks.add(jobTask);
        persistPendingTask();
        startNextTaskIfPossible();
    }

    private void persistPendingTask() {
        SPUtils.getInstance(SP_NAME).put(SP_KEY_PENDING_TASKS, JsonUtil.toJson(mJobTasks));
    }

    /**
     * 获取队列当中的第一个元素
     *
     * @return 下一个要执行的任务
     */
    private JobTask getNextTask() {
        return mJobTasks.isEmpty() ? null : mJobTasks.get(0);
    }

    void checkPendingTaskAndRun() {
        mRetryTime = 0;
        startNextTaskIfPossible();
    }

    private void startNextTaskIfPossible() {
        if (mIsBusy) {
            return;
        }
        if (!mIsNetworkEnable) {
            LogManager.appendTransparentLog("网络不可用，直接缓存任务到本地,等待网络可用,重启任务队列");
            return;
        }
        JobTask nextTask = getNextTask();
        if (nextTask == null) {
            LogManager.appendTransparentLog("上传队列中所有透传任务已全部执行完成");
            return;
        }
        mIsBusy = true;
        nextTask.setTaskCallback(mTaskCallback).execute();
    }

    private JobTask.TaskCallback mTaskCallback = new JobTask.TaskCallback() {

        @Override
        public void executeTaskFinish(JobTask jobTask, boolean isSuccess, boolean retry, @Nullable String message) {
            EventBusUtil.postEvent(new UploadSleepDataFinishedEvent(isSuccess));
            DeviceManager.INSTANCE.postIsUploadingSleepDataToServer(false);
            mIsBusy = false;
            mJobTasks.remove(jobTask);
            if (retry) {
                mJobTasks.add(jobTask);
            } else {
                deleteSleepDataFile(jobTask);
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

    private void deleteSleepDataFile(JobTask jobTask) {
        File file = new File(jobTask.filePath);
        if (file.exists()) {
            file.delete();
        }
    }

    void saveSleepData(ArrayList<String> sleepData, int type, String beginCmd, String endCmd, String monitorSn, String speedSleepSn, long receiveStartedTime, long receiveEndedTime) {
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
            // create and add JobTask
            JobTask jobTask = new JobTask(sleepFile.getAbsolutePath(),
                    sleepDataBean.beginCmd, sleepDataBean.endCmd,
                    sleepDataBean.monitorSn, sleepDataBean.speedSleeperSn,
                    sleepDataBean.receiveStartedTime, sleepDataBean.receiveEndedTime,
                    sleepDataBean.type);
            mHandler.post(() -> addTaskAndRun(jobTask));
        });
    }

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
