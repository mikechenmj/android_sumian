package com.sumian.sd.testStatic;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by sm
 * <p>
 * on 2018/8/20
 * <p>
 * desc:
 */
public class StaticTestMain {

    private TestA mTestA;
    private TestB mTestB;

    @Before
    public void load() {

        mTestA = new TestA();

    }

    @Test
    public void test() {
        mTestB = new TestB();
        new TestC().release();
    }

    @After

    public void release() {


    }
}
