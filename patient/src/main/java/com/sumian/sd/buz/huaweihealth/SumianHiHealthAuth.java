
package com.sumian.sd.buz.huaweihealth;

import android.content.Context;
import android.util.Log;
import com.huawei.hihealth.HiHealthKitApi;
import com.huawei.hihealthkit.auth.IAuthorizationListener;

public class SumianHiHealthAuth {
    private static final String TAG = "SumianHiHealthAuth";

    private SumianHiHealthAuth() {
    }

    public static void requestAuthorization(Context context, int[] userAllowTypesToWrite, int[] userAllowTypesToRead, IAuthorizationListener listener) {
        Log.i("SumianHiHealthAuth", "SumianHiHealthAuth ：requestAuthorization");
        if (listener == null) {
            Log.w("SumianHiHealthAuth", "requestAuthorization listener is null");
        } else if (context == null) {
            listener.onResult(4, "context is null");
        } else {
            HiHealthKitApi.getInstance(context).requestAuthorization(userAllowTypesToWrite, userAllowTypesToRead, listener);
        }
    }

    public static void getDataAuthStatus(Context context, int writeType, IAuthorizationListener listener) {
        Log.i("SumianHiHealthAuth", "SumianHiHealthAuth ：getDataAuthStatus");
        if (listener == null) {
            Log.w("SumianHiHealthAuth", "getDataAuthStatus listener is null");
        } else if (context == null) {
            listener.onResult(4, "context is null");
        } else {
            HiHealthKitApi.getInstance(context).getDataAuthStatus(writeType, listener);
        }
    }
}
