
package com.sumian.sd.buz.huaweihealth;

import android.content.Context;
import android.util.Log;
import com.huawei.hihealth.HiHealthDataQueryOption;
import com.huawei.hihealth.listener.ResultCallback;
import com.huawei.hihealthkit.HiHealthDataQuery;
import com.huawei.hihealthkit.data.HiHealthData;
import com.huawei.hihealthkit.data.store.HiRealTimeListener;
import com.huawei.hihealthkit.data.store.HiSportDataCallback;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class SumianHiHealthDataStore {
    private static final String TAG = "SumianHiHealthDataStore";
    private static final String ERROR_INFO_CONTEXT_NULL = "context is null";

    private SumianHiHealthDataStore() {
    }

    public static void getGender(Context context, ResultCallback genderCallback) {
        Log.i("SumianHiHealthDataStore", "SumianHiHealthDataStore ：getGender");
        if (genderCallback == null) {
            Log.w("SumianHiHealthDataStore", "getGender genderCallback is null");
        } else if (context == null) {
            Log.w("SumianHiHealthDataStore", "getGender context is null");
            genderCallback.onResult(4, "context is null");
        } else {
            SumianHiHealthKitApi.getInstance(context).getGender(genderCallback);
        }
    }

    public static void getBirthday(Context context, ResultCallback birthdayCallback) {
        Log.i("SumianHiHealthDataStore", "SumianHiHealthDataStore ：getBirthday");
        if (birthdayCallback == null) {
            Log.w("SumianHiHealthDataStore", "getBirthday birthdayCallback is null");
        } else if (context == null) {
            Log.w("SumianHiHealthDataStore", "getBirthday context is null");
            birthdayCallback.onResult(4, "context is null");
        } else {
            SumianHiHealthKitApi.getInstance(context).getBirthday(birthdayCallback);
        }
    }

    public static void getHeight(Context context, ResultCallback heightCallback) {
        Log.i("SumianHiHealthDataStore", "SumianHiHealthDataStore ：getHeight");
        if (heightCallback == null) {
            Log.w("SumianHiHealthDataStore", "getHeight heightCallback is null");
        } else if (context == null) {
            Log.w("SumianHiHealthDataStore", "getHeight context is null");
            heightCallback.onResult(4, "context is null");
        } else {
            SumianHiHealthKitApi.getInstance(context).getHeight(heightCallback);
        }
    }

    public static void getWeight(Context context, ResultCallback weightCallback) {
        Log.i("SumianHiHealthDataStore", "SumianHiHealthDataStore ：getWeight");
        if (weightCallback == null) {
            Log.w("SumianHiHealthDataStore", "getWeight weightCallback is null");
        } else if (context == null) {
            Log.w("SumianHiHealthDataStore", "getWeight context is null");
            weightCallback.onResult(4, "context is null");
        } else {
            SumianHiHealthKitApi.getInstance(context).getWeight(weightCallback);
        }
    }

    public static void getDeviceList(Context context, ResultCallback deviceListCallback) {
        Log.i("SumianHiHealthDataStore", "SumianHiHealthDataStore ：getDeviceList");
        if (deviceListCallback == null) {
            Log.w("SumianHiHealthDataStore", "getDeviceList deviceListCallback is null");
        } else if (context == null) {
            Log.w("SumianHiHealthDataStore", "SumianHiHealthDataStore getDeviceList context is null");
            deviceListCallback.onResult(4, "context is null");
        } else {
            SumianHiHealthKitApi.getInstance(context).getDeviceList(deviceListCallback);
        }
    }

    public static void startReadingAtrial(Context context, ResultCallback callback) {
        Log.i("SumianHiHealthDataStore", "SumianHiHealthDataStore ：startReadingAtrial");
        if (callback == null) {
            Log.w("SumianHiHealthDataStore", "startReadingAtrial callback is null");
        } else if (context == null) {
            Log.w("SumianHiHealthDataStore", "startReadingAtrial context is null");
            callback.onResult(4, "context is null");
        } else {
            SumianHiHealthKitApi.getInstance(context).startReadingAtrial(callback);
        }
    }

    public static void stopReadingAtrial(Context context, ResultCallback callback) {
        Log.i("SumianHiHealthDataStore", "SumianHiHealthDataStore ：stopReadingAtrial");
        if (callback == null) {
            Log.w("SumianHiHealthDataStore", "stopReadingAtrial callback is null");
        } else if (context == null) {
            Log.w("SumianHiHealthDataStore", "stopReadingAtrial context is null");
            callback.onResult(4, "context is null");
        } else {
            SumianHiHealthKitApi.getInstance(context).stopReadingAtrial(callback);
        }
    }

    public static void sendDeviceCommand(Context context, String commandOptions, ResultCallback commandCallback) {
        Log.i("SumianHiHealthDataStore", "SumianHiHealthDataStore ：sendDeviceCommand");
        if (commandCallback == null) {
            Log.w("SumianHiHealthDataStore", "sendDeviceCommand callback is null");
        } else if (context == null) {
            Log.w("SumianHiHealthDataStore", "sendDeviceCommand context is null");
            commandCallback.onResult(4, "context is null");
        } else {
            SumianHiHealthKitApi.getInstance(context).sendDeviceCommand(commandOptions, commandCallback);
        }
    }

    public static void execQuery(Context context, HiHealthDataQuery hiHealthDataQuery, int timeout, ResultCallback callback) {
        Log.i("SumianHiHealthDataStore", "SumianHiHealthDataStore ：execQuery");
        if (callback == null) {
            Log.w("SumianHiHealthDataStore", "execQuery callback is null");
        } else if (context == null) {
            Log.w("SumianHiHealthDataStore", "execQuery context is null");
            callback.onResult(4, "context is null");
        } else if (hiHealthDataQuery == null) {
            Log.w("SumianHiHealthDataStore", "execQuery hiHealthDataQuery is null");
            callback.onResult(4, "hiHealthDataQuery is null");
        } else {
            HiHealthDataQueryOption healthDataQueryOption = null;
            com.huawei.hihealthkit.HiHealthDataQueryOption hiHealthDataQueryOption = hiHealthDataQuery.getHiHealthDataQueryOption();
            if (hiHealthDataQueryOption != null) {
                healthDataQueryOption = new HiHealthDataQueryOption(hiHealthDataQueryOption.getLimit(), hiHealthDataQueryOption.getOffset(), hiHealthDataQueryOption.getOrder());
            }

            com.huawei.hihealth.HiHealthDataQuery healthDataQuery = new com.huawei.hihealth.HiHealthDataQuery(hiHealthDataQuery.getSampleType(), hiHealthDataQuery.getStartTime(), hiHealthDataQuery.getEndTime(), healthDataQueryOption);
            SumianHiHealthKitApi.getInstance(context).execQuery(healthDataQuery, timeout, callback);
        }
    }

    public static void getCount(Context context, HiHealthDataQuery hiHealthDataQuery, ResultCallback callback) {
        Log.i("SumianHiHealthDataStore", "SumianHiHealthDataStore ：getCount");
        if (callback == null) {
            Log.w("SumianHiHealthDataStore", "getCount callback is null");
        } else if (context == null) {
            Log.w("SumianHiHealthDataStore", "getCount context is null");
            callback.onResult(4, "context is null");
        } else if (hiHealthDataQuery == null) {
            Log.w("SumianHiHealthDataStore", "getCount hiHealthDataQuery is null");
            callback.onResult(4, "hiHealthDataQuery is null");
        } else {
            HiHealthDataQueryOption healthDataQueryOption = null;
            com.huawei.hihealthkit.HiHealthDataQueryOption hiHealthDataQueryOption = hiHealthDataQuery.getHiHealthDataQueryOption();
            if (hiHealthDataQueryOption != null) {
                healthDataQueryOption = new HiHealthDataQueryOption(hiHealthDataQueryOption.getLimit(), hiHealthDataQueryOption.getOffset(), hiHealthDataQueryOption.getOrder());
            }

            com.huawei.hihealth.HiHealthDataQuery healthDataQuery = new com.huawei.hihealth.HiHealthDataQuery(hiHealthDataQuery.getSampleType(), hiHealthDataQuery.getStartTime(), hiHealthDataQuery.getEndTime(), healthDataQueryOption);
            SumianHiHealthKitApi.getInstance(context).getCount(healthDataQuery, callback);
        }
    }

    public static void saveSample(Context context, HiHealthData hiHealthData, ResultCallback callback) {
        Log.i("SumianHiHealthDataStore", "SumianHiHealthDataStore ：saveSample");
        if (callback == null) {
            Log.w("SumianHiHealthDataStore", "saveSample callback is null");
        } else if (context == null) {
            Log.w("SumianHiHealthDataStore", "saveSample context is null");
            callback.onResult(4, "context is null");
        } else if (hiHealthData == null) {
            Log.w("SumianHiHealthDataStore", "saveSample hiHealthDataQuery is null");
            callback.onResult(4, "hiHealthData is null");
        } else {
            SumianHiHealthKitApi.getInstance(context).saveSample(hiHealthData, callback);
        }
    }

    public static void saveSamples(Context context, List<HiHealthData> hiHealthDataList, ResultCallback callback) {
        Log.i("SumianHiHealthDataStore", "SumianHiHealthDataStore ：saveSamples");
        if (callback == null) {
            Log.w("SumianHiHealthDataStore", "saveSamples callback is null");
        } else if (context == null) {
            Log.w("SumianHiHealthDataStore", "saveSamples context is null");
            callback.onResult(4, "context is null");
        } else if (hiHealthDataList != null && !hiHealthDataList.isEmpty()) {
            SumianHiHealthKitApi.getInstance(context).saveSamples(hiHealthDataList, callback);
        } else {
            Log.w("SumianHiHealthDataStore", "saveSamples hiHealthDataList is null or empty");
            callback.onResult(4, "hiHealthDataList is null or empty");
        }
    }

    public static void deleteSamples(Context context, List<HiHealthData> hiHealthDataList, ResultCallback callback) {
        Log.i("SumianHiHealthDataStore", "SumianHiHealthDataStore ：deleteSamples");
        if (callback == null) {
            Log.w("SumianHiHealthDataStore", "deleteSamples callback is null");
        } else if (context == null) {
            Log.w("SumianHiHealthDataStore", "deleteSamples context is null");
            callback.onResult(4, "context is null");
        } else if (hiHealthDataList != null && !hiHealthDataList.isEmpty()) {
            SumianHiHealthKitApi.getInstance(context).deleteSamples(hiHealthDataList, callback);
        } else {
            Log.w("SumianHiHealthDataStore", "deleteSamples hiHealthDataList is null or empty");
            callback.onResult(4, "hiHealthDataList is null or empty");
        }
    }

    public static void deleteSample(Context context, HiHealthData hiHealthData, ResultCallback callback) {
        Log.i("SumianHiHealthDataStore", "SumianHiHealthDataStore ：deleteSample");
        if (callback == null) {
            Log.w("SumianHiHealthDataStore", "deleteSample callback is null");
        } else if (context == null) {
            Log.w("SumianHiHealthDataStore", "deleteSample context is null");
            callback.onResult(4, "context is null");
        } else if (hiHealthData == null) {
            Log.w("SumianHiHealthDataStore", "deleteSample hiHealthData is null or empty");
            callback.onResult(4, "hiHealthData is null or empty");
        } else {
            List<HiHealthData> hiHealthDataList = new ArrayList();
            hiHealthDataList.add(hiHealthData);
            SumianHiHealthKitApi.getInstance(context).deleteSamples(hiHealthDataList, callback);
        }
    }

    public static void startReadingHeartRate(Context context, HiRealTimeListener hiRealTimeListener) {
        Log.i("SumianHiHealthDataStore", "SumianHiHealthDataStore ：startReadingHeartRate");
        if (hiRealTimeListener == null) {
            Log.w("SumianHiHealthDataStore", "startReadingHeartRate hiRealTimeListener is null");
        } else if (context == null) {
            Log.w("SumianHiHealthDataStore", "startReadingHeartRate context is null");
            hiRealTimeListener.onChange(4, "context is null");
        } else {
            SumianHiHealthKitApi.getInstance(context).startReadingHeartRate(hiRealTimeListener);
        }
    }

    public static void stopReadingHeartRate(Context context, HiRealTimeListener hiRealTimeListener) {
        Log.i("SumianHiHealthDataStore", "SumianHiHealthDataStore ：stopReadingHeartRate");
        if (hiRealTimeListener == null) {
            Log.w("SumianHiHealthDataStore", "stopReadingHeartRate hiRealTimeListener is null");
        } else if (context == null) {
            Log.w("SumianHiHealthDataStore", "stopReadingHeartRate context is null");
            hiRealTimeListener.onChange(4, "context is null");
        } else {
            SumianHiHealthKitApi.getInstance(context).stopReadingHeartRate(hiRealTimeListener);
        }
    }

    public static void startReadingRri(Context context, HiRealTimeListener hiRealTimeListener) {
        Log.i("SumianHiHealthDataStore", "SumianHiHealthDataStore ：startReadingRri");
        if (hiRealTimeListener == null) {
            Log.w("SumianHiHealthDataStore", "startReadingRri hiRealTimeListener is null");
        } else if (context == null) {
            Log.w("SumianHiHealthDataStore", "startReadingRri context is null");
            hiRealTimeListener.onChange(4, "context is null");
        } else {
            SumianHiHealthKitApi.getInstance(context).startReadingRri(hiRealTimeListener);
        }
    }

    public static void stopReadingRri(Context context, HiRealTimeListener hiRealTimeListener) {
        Log.i("SumianHiHealthDataStore", "SumianHiHealthDataStore ：stopReadingRri");
        if (hiRealTimeListener == null) {
            Log.w("SumianHiHealthDataStore", "stopReadingRri hiRealTimeListener is null");
        } else if (context == null) {
            Log.w("SumianHiHealthDataStore", "stopReadingRri context is null");
            hiRealTimeListener.onChange(4, "context is null");
        } else {
            SumianHiHealthKitApi.getInstance(context).stopReadingRri(hiRealTimeListener);
        }
    }

    public static void writeToWearable(Context context, String inputType, String inputDescription, InputStream inputStream, ResultCallback writeCallback) {
        Log.i("SumianHiHealthDataStore", "SumianHiHealthDataStore ：writeToWearable");
        if (writeCallback == null) {
            Log.w("SumianHiHealthDataStore", "writeToWearable writeCallback is null");
        } else if (context == null) {
            Log.w("SumianHiHealthDataStore", "writeToWearable context is null");
            writeCallback.onResult(4, "context is null");
        } else if (inputStream == null) {
            Log.w("SumianHiHealthDataStore", "writeToWearable inputStream is null");
            writeCallback.onResult(4, "inputStream is null");
        } else {
            SumianHiHealthKitApi.getInstance(context).writeToWearable(inputType, inputDescription, inputStream, writeCallback);
        }
    }

    public static void readFromWearable(Context context, String inputType, String fileDescription, OutputStream outputStream, ResultCallback readCallback) {
        Log.i("SumianHiHealthDataStore", "SumianHiHealthDataStore ：readFromWearable");
        if (readCallback == null) {
            Log.w("SumianHiHealthDataStore", "readFromWearable writeCallback is null");
        } else if (context == null) {
            Log.w("SumianHiHealthDataStore", "readFromWearable context is null");
            readCallback.onResult(4, "context is null");
        } else if (outputStream == null) {
            Log.w("SumianHiHealthDataStore", "readFromWearable outputStream is null");
            readCallback.onResult(4, "outputStream is null");
        } else {
            SumianHiHealthKitApi.getInstance(context).readFromWearable(inputType, fileDescription, outputStream, readCallback);
        }
    }

    public static void pushMsgToWearable(Context context, String inputType, String message, ResultCallback callback) {
        Log.i("SumianHiHealthDataStore", "SumianHiHealthDataStore ：pushMsgToWearable");
        if (callback == null) {
            Log.w("SumianHiHealthDataStore", "pushMsgToWearable callback is null");
        } else if (context == null) {
            Log.w("SumianHiHealthDataStore", "pushMsgToWearable context is null");
            callback.onResult(4, "context is null");
        } else {
            SumianHiHealthKitApi.getInstance(context).pushMsgToWearable(inputType, message, callback);
        }
    }

    public static void startRealTimeSportData(Context context, HiSportDataCallback sportDataCallback) {
        Log.i("SumianHiHealthDataStore", "startRealTimeSportData enter");
        if (sportDataCallback == null) {
            Log.w("SumianHiHealthDataStore", "startRealTimeSportData sportDataCallback is null");
        } else if (context == null) {
            Log.w("SumianHiHealthDataStore", "startRealTimeSportData context is null");
            sportDataCallback.onResult(4);
        } else {
            SumianHiHealthKitApi.getInstance(context).startRealTimeSportData(sportDataCallback);
        }
    }

    public static void stopRealTimeSportData(Context context, HiSportDataCallback sportDataCallback) {
        Log.i("SumianHiHealthDataStore", "stopRealTimeSportData enter");
        if (sportDataCallback == null) {
            Log.w("SumianHiHealthDataStore", "stopRealTimeSportData sportDataCallback is null");
        } else if (context == null) {
            Log.w("SumianHiHealthDataStore", "stopRealTimeSportData context is null");
            sportDataCallback.onResult(4);
        } else {
            SumianHiHealthKitApi.getInstance(context).stopRealTimeSportData(sportDataCallback);
        }
    }
}
