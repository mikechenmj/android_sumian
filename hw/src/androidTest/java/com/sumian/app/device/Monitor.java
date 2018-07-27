package com.sumian.app.device;

import com.sumian.app.common.util.BlueByteUtil;
import com.sumian.app.common.util.StreamUtil;

import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Monitor implements Runnable {

    public static final int DEFAULT_MAX_RUN_COUNT = 10;

    private File mDataFile;

    private MonitorCallback mCallback;

    private Calendar mCalendar;
    private int mCount = 0;
    private int mIndex = 0;

    public void setCallback(MonitorCallback callback) {
        mCallback = callback;
    }

    public Monitor() {
        this.mCalendar = Calendar.getInstance();
        //直接从3月1日进行数据采集
        mCalendar.set(2018, 2, 1, 0, 0, 0);
    }

    @Before
    public void loadFile() throws Exception {
        mDataFile = new File("../app/sampledata/sleepData.txt");
    }

    @Test
    public void ready() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(Monitor.this);
    }

    @Test
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void run() {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(mDataFile));
            String line;
            while ((line = br.readLine()) != null) {
                switch (line) {
                    case "558e11a1015ac2c2b45ac260e9":
                        line = line.replace("5ac2c2b45ac260e9", mCount == 0 ? getCurrentHexUnixTime() + getCurrentHexUnixTime() : getNextHexUnixTime() + getCurrentHexUnixTime());
                        mIndex = 1;
                        break;
                    case "558e11a10f5ac2c2b4":
                        mIndex++;
                        mCount++;
                        line = line.replace("5ac2c2b4", getCurrentHexUnixTime());
                        if (mCount > DEFAULT_MAX_RUN_COUNT) {
                            break;
                        } else {
                            Thread.sleep(1500);
                            System.out.println("执行次数-------->" + mCount);
                            // run();
                        }
                        break;
                    default:
                        mIndex++;
                        break;
                }

                Thread.sleep((long) (Math.random() * 150 + 200));

                System.out.println("cmd=" + line + "  index=" + mIndex);

                if (mCallback != null) {
                    mCallback.writeData(BlueByteUtil.hex2byte(line));
                }
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            StreamUtil.close(br);
        }

    }

    private String getCurrentHexUnixTime() {
        String unixTime = String.format(Locale.getDefault(), "%08X", mCalendar.getTimeInMillis() / 1000L);
        //System.out.println("unixTime=" + unixTime + "\r\n" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(mCalendar.getTimeInMillis())));
        return unixTime;
    }

    private String getNextHexUnixTime() {
        //1519920000  2018-03-02 00:00:00
        //0x5A982380
        int date = mCalendar.get(Calendar.DATE);
        if (date == mCalendar.getActualMaximum(Calendar.DATE)) {//下一个就跨月份了
            mCalendar.roll(Calendar.MONTH, 1);
        } else {
            mCalendar.roll(Calendar.DATE, 1);
        }

        String unixTime = String.format(Locale.getDefault(), "%08X", mCalendar.getTimeInMillis() / 1000L);

        System.out.println("unixTime=" + unixTime + "\r\n" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(mCalendar.getTimeInMillis())));
        return unixTime;
    }

    public interface MonitorCallback {

        void writeData(byte[] bytes);

        void readData(byte[] bytes);
    }


}
