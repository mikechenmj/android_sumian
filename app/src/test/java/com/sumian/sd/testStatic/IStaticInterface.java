package com.sumian.sd.testStatic;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sm
 * <p>
 * on 2018/8/20
 * <p>
 * desc:
 */
public interface IStaticInterface {

    List<IStaticInterface> mData = new ArrayList<>(0);


    void todo();

    default void release() {

        for (IStaticInterface mDatum : mData) {
            mDatum.release();
        }

    }
}
