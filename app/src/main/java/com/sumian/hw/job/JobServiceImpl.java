package com.sumian.hw.job;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.net.ConnectivityManagerCompat;
import android.text.TextUtils;

import com.sumian.hw.common.util.StreamUtil;
import com.sumian.hw.gather.FileHelper;
import com.sumian.hw.log.LogManager;
import com.sumian.sd.app.App;
import com.sumian.sd.app.AppManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by jzz
 * on 2017/12/19.
 * desc:
 * 管理文件上传任务。
 * 内部持有一个List<JobTask> 和 一个HandlerThread，在HandlerThread线程中逐个执行JobTask。
 * JobTask队列会在内存中、磁盘中各放一份，在任务队列发生变化的时候同步两者。
 * JobServiceImpl初始化会从磁盘加载之前序列化的JobTask队列到内存中，然后取出第一个开始执行，每当任务执行成功，便从obTask队列中移除，并同步状态到磁盘中。
 */

@SuppressWarnings("ResultOfMethodCallIgnored")
public class JobServiceImpl implements JobService, JobTask.TaskCallback {

    private static final String TAG = JobServiceImpl.class.getSimpleName();
    private static final String JOB_TASK_CACHE_FILE = "job_task_cache.txt";
    private static final int MSG_WHAT_ADD_AND_RUN_TASK = 1;
    private static final int MSG_WHAT_RUN_TASK = 2;
    private static final int MSG_WHAT_COMMIT_TASK_CACHE = 3;
    private static final int MSG_WHAT_LOAD_CACHE_JOB_TASK_AND_RUN = 4;
    private static final int MSG_WHAT_EXECUTE_CALLBACK_FAILED = 5;
    private static final int MSG_WHAT_EXECUTE_CALLBACK_SUCCESS = 6;
    private static final int MSG_WHAT_SAVE_SLEEP_DATA = 7;

    private volatile List<JobTask> mJobTasks = new ArrayList<>(0);
    private volatile boolean mIsBusy;
    private volatile boolean mIsHaveNet;
    private BroadcastReceiver mNetworkReceiver;
    private JobServiceHandler mHandler;
    private static volatile JobServiceImpl INSTANCE;

    private class JobServiceHandler extends Handler {
        private JobServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_WHAT_ADD_AND_RUN_TASK:
                    addAndRunTaskSync((JobTask) msg.obj);
                    break;
                case MSG_WHAT_RUN_TASK:
                    runTaskSync();
                    break;
                case MSG_WHAT_COMMIT_TASK_CACHE:
                    commitTaskCacheSync();
                    break;
                case MSG_WHAT_LOAD_CACHE_JOB_TASK_AND_RUN:
                    loadCacheJobTaskAndRunSync();
                    break;
                case MSG_WHAT_EXECUTE_CALLBACK_FAILED:
                    executeCallbackFailedSync((JobTask) msg.obj);
                    break;
                case MSG_WHAT_EXECUTE_CALLBACK_SUCCESS:
                    executeCallbackSuccessSync((JobTask) msg.obj);
                    break;
                case MSG_WHAT_SAVE_SLEEP_DATA:
                    saveSleepDataSync((SleepDataBean) msg.obj);
                    break;
            }
        }
    }

    public static JobServiceImpl getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (JobServiceImpl.class) {
                if (INSTANCE == null) {
                    INSTANCE = new JobServiceImpl(context);
                }
            }
        }
        return INSTANCE;
    }

    private JobServiceImpl(Context context) {
        HandlerThread handlerThread = new HandlerThread("JobService Thread");
        handlerThread.start();
        Looper looper = handlerThread.getLooper();
        mHandler = new JobServiceHandler(looper);

        loadCacheJobTaskAndRun();
        registerNetworkStateChangeListener(context);
    }

    /**
     * 注册网络状态监听,网络变为可用时，开始执行任务队列；网络变为不可用时，将mJobTasks序列化到磁盘中。
     *
     * @param context 用于注册监听
     */
    private void registerNetworkStateChangeListener(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (mNetworkReceiver == null) {
            this.mNetworkReceiver = new BroadcastReceiver() {

                @SuppressWarnings("ConstantConditions")
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (TextUtils.isEmpty(action)) return;
                    switch (action) {
                        case ConnectivityManager.CONNECTIVITY_ACTION:
                            NetworkInfo networkInfo = ConnectivityManagerCompat.getNetworkInfoFromBroadcast(connectivityManager, intent);
                            if (networkInfo != null && networkInfo.isConnected() && networkInfo.isAvailable()) {
                                mIsHaveNet = true;
                                LogManager.appendTransparentLog("收到网络变化情况,网络可用  开始加载本地缓存的透传队列,并执行...");
                                loadCacheJobTaskAndRun();
                            } else {
                                mIsHaveNet = false;
                                LogManager.appendTransparentLog("收到网络变化情况,网络不可用  缓存透传队列到本地..." + mJobTasks.toString());
                                commitTaskCache();
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

    /**
     * 退出时，反注册监听，序列化未完成的task
     *
     * @param context 用于反注册监听
     */
    @Override
    public void release(Context context) {
        //if (mNetworkReceiver != null) {
        //    context.unregisterReceiver(mNetworkReceiver);
        //}
        // mNetworkReceiver = null;
        commitTaskCache();
//        mLooper.quitSafely(); // HwApp 退出时release了，再进来没有初始化，会出问题。
    }

    /**
     * 反序列化磁盘中的任务到内存中
     */
    private void loadCacheJobTaskAndRun() {
        mHandler.sendEmptyMessage(MSG_WHAT_LOAD_CACHE_JOB_TASK_AND_RUN);
    }

    @SuppressWarnings("unchecked")
    private void loadCacheJobTaskAndRunSync() {
        File cacheFile = new File(App.Companion.getAppContext().getCacheDir(), JOB_TASK_CACHE_FILE);
        ObjectInputStream ois = null;
        try {
            if (cacheFile.exists() && cacheFile.length() > 0) {
                ois = new ObjectInputStream(new FileInputStream(cacheFile.getAbsolutePath()));
                mJobTasks = (List<JobTask>) ois.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            StreamUtil.close(ois);
        }
        runTaskSync();
    }

    @Override
    public void addAndRunTask(JobTask jobTask) {
        Message message = mHandler.obtainMessage();
        message.what = MSG_WHAT_ADD_AND_RUN_TASK;
        message.obj = jobTask;
        message.sendToTarget();
    }

    private void addAndRunTaskSync(JobTask jobTask) {
        mJobTasks.add(jobTask);
        commitTaskCache();
        runTask();
    }

    /**
     * 将mJobTasks写入本地文件之中
     */
    @Override
    public void commitTaskCache() {
        mHandler.sendEmptyMessage(MSG_WHAT_COMMIT_TASK_CACHE);
    }

    private void commitTaskCacheSync() {
        File cacheFile = new File(App.Companion.getAppContext().getCacheDir(), JOB_TASK_CACHE_FILE);
        ObjectOutputStream oos;
        try {

            if (!cacheFile.exists()) {
                boolean newFile = cacheFile.createNewFile();
                if (newFile) {
                    oos = new ObjectOutputStream(new FileOutputStream(cacheFile));
                    oos.writeObject(Collections.synchronizedList(mJobTasks));
                }
            } else {
                oos = new ObjectOutputStream(new FileOutputStream(cacheFile));
                oos.writeObject(Collections.synchronizedList(mJobTasks));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            StreamUtil.close();
        }
        mIsBusy = false;
    }

    /**
     * 获取队列当中的第一个元素
     *
     * @return 下一个要执行的任务
     */
    @Override
    public JobTask getNextTask() {
        return mJobTasks.isEmpty() ? null : mJobTasks.get(0);
    }

    @Override
    public void runTask() {
        mHandler.sendEmptyMessage(MSG_WHAT_RUN_TASK);
    }

    private void runTaskSync() {
        if (mIsBusy) {
            return;
        }
        JobTask currentTask = getNextTask();
        if (currentTask == null) {
            LogManager.appendTransparentLog("上传队列中所有透传任务已全部执行完成");
            mIsBusy = false;
            return;
        }
        mIsBusy = true;
        currentTask.setTaskCallback(JobServiceImpl.this).execute();
    }

    @Override
    public void executeCallbackFailed(JobTask jobTask) {
        Message message = mHandler.obtainMessage();
        message.what = MSG_WHAT_EXECUTE_CALLBACK_FAILED;
        message.obj = jobTask;
        message.sendToTarget();
    }

    private void executeCallbackFailedSync(JobTask jobTask) {
        mIsBusy = false;
        // 将失败任务置于队列末尾，避免阻塞后续任务。
        mJobTasks.remove(jobTask);
        mJobTasks.add(jobTask);
        commitTaskCacheSync();
        // 如果网络可用，则继续执行任务队列，反之停止执行，直到网络重新连接上。
        if (mIsHaveNet) {
            runTaskSync();
        } else {
            LogManager.appendTransparentLog("网络不可用直接缓存任务到本地,等待网络可用,重启任务队列");
        }
    }

    @Override
    public void executeCallbackSuccess(JobTask jobTask) {
        Message message = mHandler.obtainMessage();
        message.what = MSG_WHAT_EXECUTE_CALLBACK_SUCCESS;
        message.obj = jobTask;
        message.sendToTarget();
    }

    private void executeCallbackSuccessSync(JobTask jobTask) {
        mIsBusy = false;
        mJobTasks.remove(jobTask);
        commitTaskCacheSync();
        runTaskSync();
    }

    @Override
    public void saveSleepData(ArrayList<String> sleepData, int type, String beginCmd, String endCmd, String monitorSn, String speedSleepSn, long receiveStartedTime, long receiveEndedTime) {
        Message message = mHandler.obtainMessage();
        message.what = MSG_WHAT_SAVE_SLEEP_DATA;
        message.obj = new SleepDataBean(sleepData, beginCmd, endCmd, monitorSn, speedSleepSn, receiveStartedTime, receiveEndedTime, type);
        message.sendToTarget();
    }

    private boolean saveSleepDataSync(SleepDataBean sleepDataBean) {
        List<String> commands = sleepDataBean.commands;
        if (commands == null || commands.isEmpty()) {
            return false;
        }
        // append beginCmd and endCmd to sleep data list
        commands.add(0, sleepDataBean.beginCmd);
        commands.add(sleepDataBean.endCmd);
        // create sleep file
        File sleepFile = createSleepDataFile(sleepDataBean.endCmd);
        if (sleepFile == null) {
            return false;
        }
        // write file
        boolean writeResult = writeCommandsToFile(commands, sleepFile);
        if (!writeResult) {
            return false;
        }
        // create and add JobTask
        JobTask jobTask = new JobTask(sleepFile.getAbsolutePath(),
            sleepDataBean.beginCmd, sleepDataBean.endCmd,
            sleepDataBean.monitorSn, sleepDataBean.speedSleeperSn,
            sleepDataBean.receiveStartedTime, sleepDataBean.receiveEndedTime,
            sleepDataBean.type);
        addAndRunTaskSync(jobTask);
        return true;
    }

    private File createSleepDataFile(String endCmd) {
        try {
            File sleepFileDir = FileHelper.createSDDir(FileHelper.FILE_DIR);
            String fileName = getFileNameByCommand(endCmd);
            File sleepFile = new File(sleepFileDir, fileName);
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
    private String getFileNameByCommand(String endCmd) {
        int dataType = Integer.parseInt(endCmd.substring(4, 8), 16) >> 12;
        return dataType + "_" + endCmd.substring(10) +
            "_sn" + AppManager.getDeviceModel().getMonitorSn() +
            "_" + AppManager.getAccountViewModel().getUserInfo().getId() +
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
