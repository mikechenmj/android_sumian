package com.sumian.sleepdoctor;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.sumian.sleepdoctor.main.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Created by sm
 * <p>
 * on 2018/8/9
 * <p>
 * desc:
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class DeviceSwitchEspressoTest {


    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);


    @Test
    public void switchDeviceAndServiceTab() {
        Espresso.onView(ViewMatchers.withText("设备")).perform(ViewActions.click()).check(ViewAssertions.matches(ViewMatchers.withText("设备")));
    }


}
