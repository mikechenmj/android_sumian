package com.sumian.sd.testStatic;

/**
 * Created by sm
 * <p>
 * on 2018/8/20
 * <p>
 * desc:
 */
public class TestB implements IStaticInterface {

    @Override
    public void todo() {
        mData.add(this);
        System.out.println(toString());
    }

}
