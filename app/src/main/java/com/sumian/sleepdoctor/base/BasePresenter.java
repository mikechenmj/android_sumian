package com.sumian.sleepdoctor.base;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * Created by jzz
 * on 2017/4/4.
 * <p>
 * desc:
 */

public interface BasePresenter<T> {

    List<Call> mCalls = new ArrayList<>(0);

    default void release() {
        for (int i = 0; i < mCalls.size(); i++) {
            Call call = mCalls.get(i);
            if (call != null && !call.isCanceled()) {
                call.cancel();
                mCalls.remove(call);
            }
        }
    }

}
