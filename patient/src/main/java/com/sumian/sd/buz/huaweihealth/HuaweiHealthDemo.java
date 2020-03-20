package com.sumian.sd.buz.huaweihealth;

import android.content.Context;
import android.util.Log;

import com.huawei.hihealth.error.HiHealthError;
import com.huawei.hihealth.listener.ResultCallback;
import com.huawei.hihealthkit.HiHealthDataQuery;
import com.huawei.hihealthkit.HiHealthDataQueryOption;
import com.huawei.hihealthkit.auth.HiHealthAuth;
import com.huawei.hihealthkit.auth.IAuthorizationListener;
import com.huawei.hihealthkit.data.HiHealthData;
import com.huawei.hihealthkit.data.HiHealthPointData;
import com.huawei.hihealthkit.data.HiHealthSetData;
import com.huawei.hihealthkit.data.store.HiHealthDataStore;
import com.huawei.hihealthkit.data.store.HiRealTimeListener;
import com.huawei.hihealthkit.data.type.HiHealthDataType;
import com.huawei.hihealthkit.data.type.HiHealthPointType;
import com.huawei.hihealthkit.data.type.HiHealthRealTimeType;
import com.huawei.hihealthkit.data.type.HiHealthSetType;
import com.huawei.hihealthkit.data.type.HiHealthUserInfoType;
import com.sumian.sd.main.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HuaweiHealthDemo {

    public static void requestAuthorization(Context context) {
        int[] read = new int[]{HiHealthPointType.DATA_POINT_STEP_SUM,
                HiHealthPointType.DATA_POINT_DISTANCE_SUM,
                HiHealthPointType.DATA_POINT_CALORIES_SUM,
//                HiHealthPointType.DATA_POINT_ALTITUDE_OFFSET_SUM,//不支持
                HiHealthPointType.DATA_POINT_EXERCISE_INTENSITY,
                HiHealthSetType.DATA_SET_WALK_METADATA,
                HiHealthSetType.DATA_SET_RUN_METADATA,
                HiHealthSetType.DATA_SET_RIDE_METADATA,
                HiHealthSetType.DATA_SET_BLOOD_PRESSURE,
                HiHealthSetType.DATA_SET_BLOOD_SUGAR,
                HiHealthSetType.DATA_SET_WEIGHT_EX,
                HiHealthSetType.DATA_SET_CORE_SLEEP,
                HiHealthSetType.DATA_SET_HEART,
                HiHealthSetType.DATA_SET_WALK_METADATA, 101001, 101002, 50001};
        int[] write = new int[]{HiHealthSetType.DATA_SET_BLOOD_SUGAR};
        HiHealthAuth.requestAuthorization(context, write, read, new IAuthorizationListener() {
            @Override
            public void onResult(int i, Object o) {
                if (i != 0) {
                    return;
                }
                if (i == 0) {
                    getDataAuthStatus(context);
//                    getGender(context);
//                    getBirthday(context);
//                    getHeight(context);
//                    getWeight(context);
//                    execQuery(context, HiHealthSetType.DATA_SET_CORE_SLEEP);
//                    execQuery(context, HiHealthPointType.DATA_POINT_STEP_SUM);
//                    execQuery(context, HiHealthPointType.DATA_POINT_DISTANCE_SUM);
//                    execQuery(context, HiHealthPointType.DATA_POINT_CALORIES_SUM);
//                    execQuery(context, HiHealthPointType.DATA_POINT_EXERCISE_INTENSITY);
//                    execQuery(context, HiHealthSetType.DATA_SET_WALK_METADATA);
//                    getCount(context);
                    startReadingHeartRate(context);
                    startReadingRri(context);
                }
            }
        });
    }

    public static void getDataAuthStatus(Context context) {
        int write = HiHealthSetType.DATA_SET_BLOOD_SUGAR;
        HiHealthAuth.getDataAuthStatus(context, write,
                new IAuthorizationListener() {
                    @Override
                    public void onResult(int i, Object o) {
                        if (i != 0) {
                            return;
                        }
                        List<Integer> list = (List) o;
                        int permission = list.get(0);
                        String perStr = "";
                        switch (permission) {
                            case 0:
                                perStr = "未申请";
                                break;
                            case 1:
                                perStr = "已允许";
                                break;
                            case 2:
                                perStr = "已拒绝";
                                break;
                        }
                    }
                });
    }

    public static void getGender(Context context) {
        HiHealthDataStore.getGender(context, new ResultCallback() {
            @Override
            public void onResult(int errorCode, Object gender) {
                if (errorCode == HiHealthError.SUCCESS) {
                    int value = (int) gender;
                    String genderStr = "";
                    switch ((int) gender) {
                        case 0:
                            genderStr = "女";
                            break;
                        case 1:
                            genderStr = "男";
                            break;
                        case 2:
                            genderStr = "未知";
                            break;
                    }
                } else {
                }
            }
        });
    }

    public static void getBirthday(Context context) {
        HiHealthDataStore.getBirthday(context, new ResultCallback() {
            @Override
            public void onResult(int errorCode, Object birthday) {
                if (errorCode == HiHealthError.SUCCESS) {
                } else {
                }
            }
        });
    }

    public static void getHeight(Context context) {
        HiHealthDataStore.getHeight(context, new ResultCallback() {
            @Override
            public void onResult(int errorCode, Object height) {
                if (errorCode == HiHealthError.SUCCESS) {
                } else {
                }
            }
        });
    }

    public static void getWeight(Context context) {
        HiHealthDataStore.getWeight(context, new ResultCallback() {
            @Override
            public void onResult(int errorCode, Object weight) {
                if (errorCode == HiHealthError.SUCCESS) {
                } else {
                }
            }
        });
    }

    public static void execQuery(Context context, int type) {
        int timeout = 0;
        long endTime = System.currentTimeMillis();
        long startTime = endTime - 8640000000L * 30;
        if (type < 0) {
            type = HiHealthSetType.DATA_SET_CORE_SLEEP;
        }
        final int finType = type;
        HiHealthDataQuery hiHealthDataQuery = new HiHealthDataQuery(type,
                startTime, endTime, new HiHealthDataQueryOption());
        HiHealthDataStore.execQuery(context, hiHealthDataQuery, timeout, new
                ResultCallback() {
                    @Override
                    public void onResult(int i, Object data) {
                        if (data != null) {
                            List<HiHealthData> dataList = (ArrayList<HiHealthData>) data;
                            for (HiHealthData hiHealthData : dataList) {
                                if (hiHealthData instanceof HiHealthSetData) {
                                    HiHealthSetData hiHealthSetData = (HiHealthSetData) hiHealthData;
                                    for (Object entry : hiHealthSetData.getMap().entrySet()) {
                                    }
                                } else if (hiHealthData instanceof HiHealthPointData) {
                                    HiHealthPointData hiHealthPointData = (HiHealthPointData) hiHealthData;
                                }
//
                            }

                        }
                    }
                });

    }

    public static void getCount(Context context) {
        long endTime = System.currentTimeMillis();
        long startTime = endTime - 86400000L * 30;
        startTime = endTime - 63072000 * 1000L;
        int type = HiHealthSetType.DATA_SET_CORE_SLEEP;
        HiHealthDataQuery hiHealthDataQuery = new HiHealthDataQuery(type,
                startTime, endTime, new HiHealthDataQueryOption());
        HiHealthDataStore.getCount(context, hiHealthDataQuery, new
                ResultCallback() {
                    @Override
                    public void onResult(int i, Object data) {
                        if (data != null) {
                        }
                    }
                });
    }

    public static void startReadingHeartRate(Context context) {
        HiHealthDataStore.startReadingHeartRate(context, new HiRealTimeListener() {
            @Override
            public void onResult(int state) {
                if (state == HiHealthError.SUCCESS) {
                } else {
                }
            }

            @Override
            public void onChange(int errCode, String value) {
                if (errCode == HiHealthError.SUCCESS) {
                    try {
                        JSONArray jsonArray = new JSONArray(value);
                        String heartRateStr = jsonArray.getString(0);
                        JSONObject jsonObject = new JSONObject(heartRateStr);
                        long timeStamp = jsonObject.getLong("time_info");
                        int rate = jsonObject.getInt("hr_info");
                    } catch (JSONException e) {
                    }
                } else {
                }
            }
        });
    }

    public static void startReadingRri(Context context) {
        HiHealthDataStore.startReadingRri(context, new HiRealTimeListener() {
            @Override
            public void onResult(int state) {
                if (state == HiHealthError.SUCCESS){
                }else {
                }
            }

            @Override
            public void onChange(int errCode, String value) {
                try {
                    JSONObject jsonObject = new JSONObject(value);
                    String str = jsonObject.getString("value");
                } catch (JSONException e){
                }
            }
        });
    }
}
