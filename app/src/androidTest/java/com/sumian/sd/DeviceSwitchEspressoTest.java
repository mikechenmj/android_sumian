package com.sumian.sd;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.sumian.sd.main.MainActivity;

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
