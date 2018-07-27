package com.sumian.app;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.sumian.app.device.Monitor;
import com.sumian.app.improve.main.HomeActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static junit.framework.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ExampleInstrumentedTest {

    private static final String TAG = ExampleInstrumentedTest.class.getSimpleName();

    private Monitor mMonitor;

    @Rule
    public ActivityTestRule<HomeActivity> mActivityTestRule = new ActivityTestRule<>(HomeActivity.class);
    private Context appContext;

    @Before
    public void setUp() throws Exception {
        appContext = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        File cacheDir = appContext.getCacheDir();
        // File file = new File("../app/sampledata/sleepData.txt");
        Log.e(TAG, "useAppContext: --------->" + cacheDir.exists());

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        // Add your own intent extras here if applicable.
        mActivityTestRule.launchActivity(intent);
        // CountDownLatch countdown = new CountDownLatch(1);
        // countdown.await();

        mMonitor = new Monitor();
        mMonitor.loadFile();
        mMonitor.setCallback(new Monitor.MonitorCallback() {
            @Override
            public void writeData(byte[] bytes) {
                Log.e(TAG, "writeData: ------>");
            }

            @Override
            public void readData(byte[] bytes) {

            }
        });

        mMonitor.run();

        assertEquals("com.sumian.app", appContext.getPackageName());
    }
}
