package com.sumian.sddoctor.service.cbti.base;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * Created by jzz
 * on 2017/4/4.
 * <p>
 * desc:
 */

public interface SdBasePresenter<T> {

    List<Call> mCalls = new ArrayList<>(0);

    default void release() {
        for (int i = 0; i < mCalls.size(); i++) {
            Call call = mCalls.get(i);
            if (call.isExecuted()) {
                call.cancel();
                mCalls.remove(call);
            }
        }
    }

    default void addCall(Call call) {
        mCalls.add(call);
    }

    default void removeCall(Call call) {
        mCalls.remove(call);
    }
}
