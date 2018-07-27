package com.sumian.app;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;

import com.sumian.app.improve.main.WelcomeActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class Test1 {

    private UiDevice mUiDevice;
    private Context mContext;

    @Rule
    public ActivityTestRule<WelcomeActivity> mActivityTestRule = new ActivityTestRule<>(WelcomeActivity.class, "com.sumain.app", Intent.FLAG_ACTIVITY_NEW_TASK, false, false);

    @Before
    public void setUp() throws Exception {
        mUiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        this.mContext = InstrumentationRegistry.getTargetContext();

    }

    @Test
    public void test() throws UiObjectNotFoundException {
        //Intent intent = new Intent(Intent.ACTION_PICK);
        // mActivityTestRule.launchActivity(intent);

        //该种方式可以一直打开 app 界面.但是 后面的点击操作无法执行
        // CountDownLatch countdown = new CountDownLatch(1);
        //  try {
        // countdown.await();
        //  } catch (InterruptedException e) {
        //    e.printStackTrace();
        //  }

//        Intent intent = mContext.getPackageManager().getLaunchIntentForPackage("com.sumian.app.test");
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        mContext.startActivity(intent);

        // mUiDevice.pressHome();
        //UiObject openApp = mUiDevice.findObject(new UiSelector().text("速眠"));
        UiObject object = new UiObject(new UiSelector().text("速眠"));
        object.longClick();

        //openApp.longClick();

    }
}
