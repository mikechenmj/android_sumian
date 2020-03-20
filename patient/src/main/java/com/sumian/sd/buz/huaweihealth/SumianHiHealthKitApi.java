
package com.sumian.sd.buz.huaweihealth;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.huawei.hihealth.HiHealthDataQuery;
import com.huawei.hihealth.HiHealthKitData;
import com.huawei.hihealth.IBinderInterceptor.Stub;
import com.huawei.hihealth.IHiHealthKit;
import com.huawei.hihealth.device.HiHealthDeviceInfo;
import com.huawei.hihealth.listener.ResultCallback;
import com.huawei.hihealthkit.auth.IAuthorizationListener;
import com.huawei.hihealthkit.data.HiHealthData;
import com.huawei.hihealthkit.data.HiHealthPointData;
import com.huawei.hihealthkit.data.HiHealthSetData;
import com.huawei.hihealthkit.data.store.HiRealTimeListener;
import com.huawei.hihealthkit.data.store.HiSportDataCallback;
import com.huawei.hihealthkit.data.type.HiHealthDataType;
import com.huawei.hihealthkit.data.type.HiHealthDataType.Category;
import com.sumian.sd.common.log.SdLogManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.json.JSONException;
import org.json.JSONObject;

public class SumianHiHealthKitApi implements ServiceConnection {
    private static final String TAG = "HiHealthKit";
    private static final String KIT_SHAREP_FILE_NAME = "hihealth_kit";
    private static final String KIT_SHAREP_UID_KYE_NAME = "hihealth_kit";
    private static final String KEY_FLAG_NAME = "flag";
    private static final String KEY_PACKAGE_NAME = "packageName";
    private static final String KEY_CLASS_NAME = "className";
    private static final String KEY_READ_TYPES = "readTypes";
    private static final String KEY_WRITE_TYPES = "writeTypes";
    private static final String THIRD_PARTY_PACKAGE_NAME = "third_party_package_name";
    private static final String THIRD_PARTY_APP_NAME = "third_party_app_name";
    private static final String VERSION = "version";
    private static final int DEFAULT_VALUE = 0;
    private static final int DEFAULT_LIST_SIZE = 10;
    private static final int MAX_SAMPLES_SIZE = 20;
    private static final int TIME_BIND_SERVICE_WAITTING = 30000;
    private static final Object LOCK = new Object();
    private static final int DEFAULT_TRANSMISSION_SIZE = 51200;
    private static final String DATA_INFO_SIZE = "size";
    private static final String DATA_INFO_IS_FINISHED = "is_finished";
    private static volatile Context sContext;
    private final Object bindLock;
    private ExecutorService singleThreadPool;
    private String mCallerApkName;
    private volatile IHiHealthKit mApiAidl;
    private CountDownLatch mLatch;
    private volatile boolean mIsWorking;

    private SumianHiHealthKitApi() {
        this.bindLock = new Object();
        this.mIsWorking = false;
        Log.i("HiHealthKit", "SumianHiHealthKitApi construct");
        this.mCallerApkName = sContext.getPackageName();
        this.singleThreadPool = Executors.newSingleThreadExecutor();
        this.singleThreadPool.execute(new Runnable() {
            public void run() {
                SumianHiHealthKitApi.this.bindService();
            }
        });
    }

    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.i("HiHealthKit", "onServiceConnected");
            int uid = Binder.getCallingUid();
            String packageName = sContext.getPackageManager().getNameForUid(uid);
            Log.d("HiHealthKit", "getCallingUid uid:" + uid + " packageName1:" + packageName);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    IBinder binder = null;
                    try {
                        binder = Stub.asInterface(service).getServiceBinder((String)null);
                        if (binder == null) {
                            SdLogManager.INSTANCE.logHuaweiHealth("绑定基础服务失败");
                        }
                        Log.i("HiHealthKit", "binder: " + binder);
                        mApiAidl = com.huawei.hihealth.IHiHealthKit.Stub.asInterface(binder);
                        Log.i("HiHealthKit", "mApiAidl: " + mApiAidl);
                        if (mApiAidl == null) {
                            SdLogManager.INSTANCE.logHuaweiHealth("绑定基础服务成功，但无法获取指定业务 mApiAidl");
                            Log.w("HiHealthKit", "onServiceConnected error !");
                        }
                        synchronized(bindLock) {
                            bindLock.notifyAll();
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
    }

    public void onServiceDisconnected(ComponentName name) {
        Log.i("HiHealthKit", "onServiceDisconnected");
        if (this.mLatch != null && this.mIsWorking) {
            Log.i("HiHealthKit", "onServiceDisconnected() latch countDown");
            this.mLatch.countDown();
        }

        this.mApiAidl = null;
    }

    public static SumianHiHealthKitApi getInstance(Context context) {
        Log.i("HiHealthKit", "SumianHiHealthKitApi getInstance");
        if (sContext == null) {
            sContext = context.getApplicationContext();
        }

        return SumianHiHealthKitApi.Instance.INSTANCE;
    }

    private void bindService() {
        synchronized(LOCK) {
            if (this.mApiAidl == null) {
                Intent intent = new Intent("com.huawei.health.action.KIT_SERVICE");
                intent.setClassName("com.huawei.health", "com.huawei.hihealthservice.HiHealthService");
                intent.setPackage("com.huawei.health");

                try {
                    sContext.bindService(intent, this, Context.BIND_AUTO_CREATE);
                } catch (SecurityException var7) {
                    Log.e("HiHealthKit", "bindService exception" + var7.getMessage());
                }

                label59: {
                    synchronized(this.bindLock) {
                        try {
                            if (this.mApiAidl == null) {
                                boolean isReady = true;

                                while(true) {
                                    if (!isReady) {
                                        break label59;
                                    }

                                    this.bindLock.wait(30000L);
                                    isReady = false;
                                }
                            }

                            Log.i("HiHealthKit", "bindService bind mApiAidl is not null = " + this.mApiAidl);
                        } catch (InterruptedException var8) {
                            Log.e("HiHealthKit", "bindService() InterruptedException = " + var8.getMessage());
                            break label59;
                        }
                    }

                    return;
                }

                Log.i("HiHealthKit", "bindService bind over mApiAidl is " + this.mApiAidl);
            }

        }
    }

    private void updateUuid(int uuid) {
        if (sContext != null) {
            SharedPreferences sharedPreferences = sContext.getSharedPreferences("hihealth_kit", 0);
            if (sharedPreferences != null) {
                sharedPreferences.edit().putInt("hihealth_kit", uuid).apply();
            }
        }
    }

    private int getLastUuid() {
        if (sContext == null) {
            return 0;
        } else {
            SharedPreferences sharedPreferences = sContext.getSharedPreferences("hihealth_kit", 0);
            return sharedPreferences == null ? 0 : sharedPreferences.getInt("hihealth_kit", 0);
        }
    }

    public void requestAuthorization(int[] userAllowTypesToWrite, int[] userAllowTypesToRead, final IAuthorizationListener listener) {
        final int[] writePermissionArray = this.removeDuplicateElements(userAllowTypesToWrite);
        final int[] readPermissionArray = this.removeDuplicateElements(userAllowTypesToRead);
        this.singleThreadPool.execute(new Runnable() {
            public void run() {
                if (listener != null) {
                    SumianHiHealthKitApi.this.bindService();
                    if (SumianHiHealthKitApi.this.mApiAidl == null) {
                        listener.onResult(1, "requestAuthorization mApiAidl is null");
                        Log.w("HiHealthKit", "requestAuthorization mApiAidl is null");
                    } else {
                        try {
                            SumianHiHealthKitApi.this.mApiAidl.requestAuthorization(SumianHiHealthKitApi.this.getLastUuid(), writePermissionArray, readPermissionArray, new com.huawei.hihealth.IBaseCallback.Stub() {
                                public void onResult(int errCode, Map map) throws RemoteException {
                                    if (errCode == 0 && map != null) {
                                        int uuid = Integer.parseInt((String)map.get("flag"));
                                        SumianHiHealthKitApi.this.updateUuid(uuid);
                                        listener.onResult(0, "success");
                                    } else {
                                        listener.onResult(4, "remote fail");
                                    }

                                }
                            });
                            Log.i("HiHealthKit", "requestAuthorization end");
                        } catch (RemoteException var2) {
                            Log.e("HiHealthKit", "requestAuthorization RemoteException");
                            listener.onResult(4, "requestAuthorization fail");
                        } catch (Exception var3) {
                            Log.e("HiHealthKit", "requestAuthorization Exception");
                            listener.onResult(4, "requestAuthorization fail");
                        }

                    }
                }
            }
        });
    }

    private int[] removeDuplicateElements(int[] sourceArray) {
        if (sourceArray != null && sourceArray.length != 0) {
            ArrayList<Integer> tmpResult = new ArrayList(10);
            int[] resultArray = sourceArray;
            int i = sourceArray.length;

            for(int var5 = 0; var5 < i; ++var5) {
                int elements = resultArray[var5];
                if (!tmpResult.contains(elements)) {
                    tmpResult.add(elements);
                }
            }

            resultArray = new int[tmpResult.size()];

            for(i = 0; i < resultArray.length; ++i) {
                resultArray[i] = (Integer)tmpResult.get(i);
            }

            return resultArray;
        } else {
            return null;
        }
    }

    public void getDataAuthStatus(final int writeType, final IAuthorizationListener listener) {
        this.singleThreadPool.execute(new Runnable() {
            public void run() {
                if (listener != null) {
                    SumianHiHealthKitApi.this.bindService();
                    if (SumianHiHealthKitApi.this.mApiAidl == null) {
                        listener.onResult(1, "getDataAuthStatus mApiAidl is null");
                        Log.w("HiHealthKit", "getDataAuthStatus mApiAidl is null");
                    } else {
                        try {
                            SumianHiHealthKitApi.this.mApiAidl.getDataAuthStatus(SumianHiHealthKitApi.this.getLastUuid(), writeType, new com.huawei.hihealth.IDataOperateListener.Stub() {
                                public void onResult(int errCode, List list) throws RemoteException {
                                    if (errCode == 0 && list != null) {
                                        listener.onResult(0, list);
                                    } else {
                                        listener.onResult(4, (Object)null);
                                    }

                                }
                            });
                            Log.i("HiHealthKit", "getDataAuthStatus end");
                        } catch (RemoteException var2) {
                            Log.e("HiHealthKit", "getDataAuthStatus RemoteException");
                            listener.onResult(4, "fail");
                        } catch (Exception var3) {
                            Log.e("HiHealthKit", "getDataAuthStatus Exception");
                            listener.onResult(4, "fail");
                        }

                    }
                }
            }
        });
    }

    public void getGender(final ResultCallback genderListener) {
        this.singleThreadPool.execute(new Runnable() {
            public void run() {
                SumianHiHealthKitApi.this.bindService();
                if (SumianHiHealthKitApi.this.mApiAidl == null) {
                    SumianHiHealthKitApi.this.notifyCallback(genderListener, 1, "getGender mApiAidl is null");
                    Log.w("HiHealthKit", "getGender mApiAidl is null");
                } else {
                    try {
                        SumianHiHealthKitApi.this.mApiAidl.getGender(SumianHiHealthKitApi.this.getLastUuid(), new com.huawei.hihealth.ICommonListener.Stub() {
                            public void onSuccess(int intent, List datas) throws RemoteException {
                                Log.i("HiHealthKit", "enter KitAPI getGender onSuccess");
                                if (datas != null && datas.size() > 0) {
                                    int gender = (Integer)datas.get(0);
                                    SumianHiHealthKitApi.this.notifyCallback(genderListener, 0, gender);
                                } else {
                                    SumianHiHealthKitApi.this.notifyCallback(genderListener, 1, "failed");
                                }
                            }

                            public void onFailure(int errCode, List errMsg) throws RemoteException {
                                SumianHiHealthKitApi.this.notifyCallback(genderListener, 1, "failed");
                                Log.i("HiHealthKit", "get gender onfailure");
                            }
                        });
                    } catch (RemoteException var2) {
                        SumianHiHealthKitApi.this.notifyCallback(genderListener, 1, "failed");
                        Log.i("HiHealthKit", "get gender RemoteException");
                    }

                }
            }
        });
    }

    public void getBirthday(final ResultCallback birthdayListener) {
        this.singleThreadPool.execute(new Runnable() {
            public void run() {
                SumianHiHealthKitApi.this.bindService();
                if (SumianHiHealthKitApi.this.mApiAidl == null) {
                    SumianHiHealthKitApi.this.notifyCallback(birthdayListener, 1, "getBirthday mApiAidl is null");
                    Log.w("HiHealthKit", "getBirthday mApiAidl is null");
                } else {
                    try {
                        SumianHiHealthKitApi.this.mApiAidl.getBirthday(SumianHiHealthKitApi.this.getLastUuid(), new com.huawei.hihealth.ICommonListener.Stub() {
                            public void onSuccess(int intent, List datas) throws RemoteException {
                                Log.i("HiHealthKit", "enter KitAPI getBirthday onSuccess");
                                if (datas != null && datas.size() > 0) {
                                    int birthday = (Integer)datas.get(0);
                                    SumianHiHealthKitApi.this.notifyCallback(birthdayListener, 0, birthday);
                                } else {
                                    SumianHiHealthKitApi.this.notifyCallback(birthdayListener, 1, "failed");
                                }
                            }

                            public void onFailure(int errCode, List errMsg) throws RemoteException {
                                Log.i("HiHealthKit", "get birthday onfailure");
                                SumianHiHealthKitApi.this.notifyCallback(birthdayListener, 1, "failed");
                            }
                        });
                    } catch (RemoteException var2) {
                        Log.i("HiHealthKit", "get birthday RemoteException");
                        SumianHiHealthKitApi.this.notifyCallback(birthdayListener, 1, "failed");
                    }

                }
            }
        });
    }

    public void getHeight(final ResultCallback heightListener) {
        this.singleThreadPool.execute(new Runnable() {
            public void run() {
                SumianHiHealthKitApi.this.bindService();
                if (SumianHiHealthKitApi.this.mApiAidl == null) {
                    SumianHiHealthKitApi.this.notifyCallback(heightListener, 1, "getHeight mApiAidl is null");
                    Log.w("HiHealthKit", "getHeight mApiAidl is null");
                } else {
                    try {
                        SumianHiHealthKitApi.this.mApiAidl.getHeight(SumianHiHealthKitApi.this.getLastUuid(), new com.huawei.hihealth.ICommonListener.Stub() {
                            public void onSuccess(int intent, List datas) throws RemoteException {
                                Log.i("HiHealthKit", "getHeight:onSuccess");
                                if (datas != null && datas.size() > 0) {
                                    int height = (Integer)datas.get(0);
                                    Log.d("HiHealthKit", "getHeight height: " + height);
                                    SumianHiHealthKitApi.this.notifyCallback(heightListener, 0, height);
                                } else {
                                    SumianHiHealthKitApi.this.notifyCallback(heightListener, 1, "failed");
                                }
                            }

                            public void onFailure(int errCode, List errMsg) throws RemoteException {
                                Log.i("HiHealthKit", "getHeight onfailure");
                                SumianHiHealthKitApi.this.notifyCallback(heightListener, 1, "failed");
                            }
                        });
                    } catch (RemoteException var2) {
                        Log.i("HiHealthKit", "getHeight RemoteException");
                        SumianHiHealthKitApi.this.notifyCallback(heightListener, 1, "failed");
                    }

                }
            }
        });
    }

    public void getWeight(final ResultCallback weightListener) {
        this.singleThreadPool.execute(new Runnable() {
            public void run() {
                SumianHiHealthKitApi.this.bindService();
                if (SumianHiHealthKitApi.this.mApiAidl == null) {
                    SumianHiHealthKitApi.this.notifyCallback(weightListener, 1, "getWeight mApiAidl is null");
                    Log.w("HiHealthKit", "getWeight mApiAidl is null");
                } else {
                    try {
                        SumianHiHealthKitApi.this.mApiAidl.getWeight(SumianHiHealthKitApi.this.getLastUuid(), new com.huawei.hihealth.ICommonListener.Stub() {
                            public void onSuccess(int intent, List datas) throws RemoteException {
                                Log.i("HiHealthKit", "enter KitAPI getWeight onSuccess");
                                if (datas != null && datas.size() > 0) {
                                    float weight = (Float)datas.get(0);
                                    Log.i("HiHealthKit", "getWeight onSuccess weight: " + weight);
                                    SumianHiHealthKitApi.this.notifyCallback(weightListener, 0, weight);
                                } else {
                                    SumianHiHealthKitApi.this.notifyCallback(weightListener, 1, "failed");
                                }
                            }

                            public void onFailure(int errCode, List errMsg) throws RemoteException {
                                Log.i("HiHealthKit", "get weight onfailure");
                                SumianHiHealthKitApi.this.notifyCallback(weightListener, 1, "failed");
                            }
                        });
                    } catch (RemoteException var2) {
                        Log.i("HiHealthKit", "get weight RemoteException");
                        SumianHiHealthKitApi.this.notifyCallback(weightListener, 1, "failed");
                    }

                }
            }
        });
    }

    public void execQuery(final HiHealthDataQuery hiHealthDataQuery, final int timeout, final ResultCallback resultCallback) {
        Log.i("HiHealthKit", "enter execQuery");
        this.singleThreadPool.execute(new Runnable() {
            public void run() {
                SumianHiHealthKitApi.this.bindService();
                if (SumianHiHealthKitApi.this.mApiAidl == null) {
                    SumianHiHealthKitApi.this.notifyCallback(resultCallback, 1, "execQuery mApiAidl is null");
                    Log.w("HiHealthKit", "execQuery mApiAidl is null");
                } else {
                    try {
                        SumianHiHealthKitApi.this.mApiAidl.execQuery(SumianHiHealthKitApi.this.getLastUuid(), hiHealthDataQuery, timeout, new com.huawei.hihealth.IDataReadResultListener.Stub() {
                            public void onResult(List datas, int errorCode, int resultType) {
                                Log.i("HiHealthKit", "enter KitAPI execQuery onSuccess");
                                if (datas != null) {
                                    Log.i("HiHealthKit", "datas size =" + datas.size() + ", error code = " + errorCode);
                                    List kitList = new ArrayList(10);
                                    Category category = HiHealthDataType.getCategory(hiHealthDataQuery.getSampleType());
                                    switch(category) {
                                    case POINT:
                                        SumianHiHealthKitApi.this.handlePointData(datas, kitList);
                                        break;
                                    case SET:
                                        Log.i("HiHealthKit", "enter set");
                                        SumianHiHealthKitApi.this.handleSetData(datas, kitList);
                                        break;
                                    case SESSION:
                                        SumianHiHealthKitApi.this.handleSetData(datas, kitList);
                                        break;
                                    case SEQUENCE:
                                        SumianHiHealthKitApi.this.handleSetData(datas, kitList);
                                    }

                                    resultCallback.onResult(errorCode, kitList);
                                } else {
                                    Log.i("HiHealthKit", "datas == null");
                                    SumianHiHealthKitApi.this.notifyCallback(resultCallback, errorCode, (Object)null);
                                }

                            }
                        });
                    } catch (RemoteException var2) {
                        Log.i("HiHealthKit", "exec query RemoteException");
                    }

                }
            }
        });
    }

    public void getCount(final HiHealthDataQuery hiHealthDataQuery, final ResultCallback resultCallback) {
        Log.i("HiHealthKit", "enter getCount");
        this.singleThreadPool.execute(new Runnable() {
            public void run() {
                SumianHiHealthKitApi.this.bindService();
                if (SumianHiHealthKitApi.this.mApiAidl == null) {
                    SumianHiHealthKitApi.this.notifyCallback(resultCallback, 1, "getCount mApiAidl is null");
                    Log.w("HiHealthKit", "getCount mApiAidl is null");
                } else {
                    try {
                        SumianHiHealthKitApi.this.mApiAidl.getCount(SumianHiHealthKitApi.this.getLastUuid(), hiHealthDataQuery, new com.huawei.hihealth.IDataReadResultListener.Stub() {
                            public void onResult(List datas, int errorCode, int resultType) {
                                Log.i("HiHealthKit", "enter KitAPI getCount onSuccess");
                                if (datas != null) {
                                    Integer count = (Integer)datas.get(0);
                                    resultCallback.onResult(errorCode, count == null ? 0 : count);
                                } else {
                                    resultCallback.onResult(errorCode, 0);
                                }

                            }
                        });
                    } catch (RemoteException var2) {
                        Log.i("HiHealthKit", "getCount RemoteException");
                    }

                }
            }
        });
    }

    private void setDeviceInfo(HiHealthKitData hiHealthKitData, HiHealthData hiHealthData) {
        String uniqueCode = hiHealthKitData.getString("device_uniquecode");
        if (!TextUtils.isEmpty(uniqueCode)) {
            String deviceName = hiHealthKitData.getString("device_name");
            String deviceModel = hiHealthKitData.getString("device_model");
            HiHealthDeviceInfo hiHealthDevice = new HiHealthDeviceInfo(uniqueCode, deviceName, deviceModel);
            hiHealthData.setSourceDevice(hiHealthDevice);
        }
    }

    private void handlePointData(List datas, List kitList) {
        if (datas != null) {
            Log.i("HiHealthKit", "datas size = " + datas.size());
            Iterator var3 = datas.iterator();

            while(var3.hasNext()) {
                Object obj = var3.next();
                HiHealthKitData hiHealthKitData = (HiHealthKitData)obj;
                HiHealthPointData hiHealthPointData = new HiHealthPointData(hiHealthKitData.getType(), hiHealthKitData.getStartTime(), hiHealthKitData.getEndTime(), hiHealthKitData.getIntValue(), 0);
                this.setDeviceInfo(hiHealthKitData, hiHealthPointData);
                kitList.add(hiHealthPointData);
            }
        } else {
            Log.i("HiHealthKit", "point data null");
        }

    }

    private void handleSetData(List datas, List kitList) {
        if (datas != null) {
            Log.i("HiHealthKit", "datas size = " + datas.size());
            Iterator var3 = datas.iterator();

            while(var3.hasNext()) {
                Object obj = var3.next();
                HiHealthKitData hiHealthKitData = (HiHealthKitData)obj;
                HiHealthSetData hiHealthSetData = new HiHealthSetData(hiHealthKitData.getType(), hiHealthKitData.getMap(), hiHealthKitData.getStartTime(), hiHealthKitData.getEndTime());
                this.setDeviceInfo(hiHealthKitData, hiHealthSetData);
                kitList.add(hiHealthSetData);
            }
        }

    }

    public void saveSample(final HiHealthData hiHealthData, final ResultCallback resultCallback) {
        this.singleThreadPool.execute(new Runnable() {
            public void run() {
                SumianHiHealthKitApi.this.bindService();
                if (SumianHiHealthKitApi.this.mApiAidl == null) {
                    Log.w("HiHealthKit", "saveSample mApiAidl is null");
                    SumianHiHealthKitApi.this.notifyCallback(resultCallback, 1, "saveSample mApiAidl is null");
                } else {
                    try {
                        int type = hiHealthData.getType();
                        Category category = HiHealthDataType.getCategory(type);
                        HiHealthKitData hiHealthKitData = new HiHealthKitData();
                        switch(category) {
                        case SET:
                            Log.i("HiHealthKit", "sample set");
                            SumianHiHealthKitApi.this.convertToSet((HiHealthSetData)hiHealthData, hiHealthKitData);
                        case POINT:
                        case SESSION:
                        case SEQUENCE:
                        case REALTIME:
                        case USERINFO:
                        case UNKOWN:
                        default:
                            Log.i("HiHealthKit", String.valueOf(hiHealthKitData.getStartTime()));
                            SumianHiHealthKitApi.this.mApiAidl.saveSample(SumianHiHealthKitApi.this.getLastUuid(), hiHealthKitData, new com.huawei.hihealth.IDataOperateListener.Stub() {
                                public void onResult(int errorCode, List datas) {
                                    Log.i("HiHealthKit", "enter saveSample result");
                                    resultCallback.onResult(errorCode, datas);
                                }
                            });
                        }
                    } catch (RemoteException var4) {
                        Log.i("HiHealthKit", "save sample RemoteException");
                        resultCallback.onResult(4, (Object)null);
                    }

                }
            }
        });
    }

    public void saveSamples(final List<HiHealthData> hiHealthDataList, final ResultCallback resultCallback) {
        this.singleThreadPool.execute(new Runnable() {
            public void run() {
                SumianHiHealthKitApi.this.bindService();
                if (SumianHiHealthKitApi.this.mApiAidl == null) {
                    SumianHiHealthKitApi.this.notifyCallback(resultCallback, 1, "saveSamples mApiAidl is null");
                    Log.w("HiHealthKit", "saveSamples mApiAidl is null");
                } else if (hiHealthDataList != null && hiHealthDataList.size() <= 20) {
                    final int[] code = new int[]{4};
                    final Object[] message = new Object[1];

                    try {
                        Iterator var3 = hiHealthDataList.iterator();

                        while(var3.hasNext()) {
                            HiHealthData hiHealthData = (HiHealthData)var3.next();
                            SumianHiHealthKitApi.this.mLatch = new CountDownLatch(1);
                            SumianHiHealthKitApi.this.mIsWorking = true;
                            int type = hiHealthData.getType();
                            Category category = HiHealthDataType.getCategory(type);
                            HiHealthKitData hiHealthKitData = new HiHealthKitData();
                            switch(category) {
                            case SET:
                                Log.i("HiHealthKit", "sample set");
                                SumianHiHealthKitApi.this.convertToSet((HiHealthSetData)hiHealthData, hiHealthKitData);
                            case POINT:
                            case SESSION:
                            case SEQUENCE:
                            case REALTIME:
                            case USERINFO:
                            case UNKOWN:
                            default:
                                Log.i("HiHealthKit", String.valueOf(hiHealthKitData.getStartTime()));
                                SumianHiHealthKitApi.this.mApiAidl.saveSample(SumianHiHealthKitApi.this.getLastUuid(), hiHealthKitData, new com.huawei.hihealth.IDataOperateListener.Stub() {
                                    public void onResult(int errorCode, List datas) {
                                        Log.i("HiHealthKit", "enter saveSample result");
                                        if (errorCode == 0) {
                                            code[0] = 0;
                                            message[0] = datas;
                                        } else {
                                            code[0] = errorCode;
                                            message[0] = datas;
                                        }

                                        if (SumianHiHealthKitApi.this.mLatch != null) {
                                            SumianHiHealthKitApi.this.mLatch.countDown();
                                        }

                                    }
                                });

                                try {
                                    SumianHiHealthKitApi.this.mLatch.await();
                                } catch (InterruptedException var14) {
                                    Log.e("HiHealthKit", "saveSample InterruptedException");
                                }

                                SumianHiHealthKitApi.this.mIsWorking = false;
                                SumianHiHealthKitApi.this.mLatch = null;
                            }
                        }
                    } catch (RemoteException var15) {
                        Log.i("HiHealthKit", "save sample RemoteException");
                        code[0] = 4;
                        message[0] = "RemoteException";
                    } catch (Exception var16) {
                        Log.i("HiHealthKit", "save sample Exception");
                        code[0] = 4;
                        message[0] = "Exception";
                    } finally {
                        if (null != resultCallback) {
                            resultCallback.onResult(code[0], message[0]);
                        }

                        Log.i("HiHealthKit", "saveSamples end");
                    }

                } else {
                    resultCallback.onResult(2, "too much datas!");
                }
            }
        });
    }

    public void deleteSamples(final List<HiHealthData> hiHealthDataList, final ResultCallback resultCallback) {
        this.singleThreadPool.execute(new Runnable() {
            public void run() {
                SumianHiHealthKitApi.this.bindService();
                if (SumianHiHealthKitApi.this.mApiAidl == null) {
                    SumianHiHealthKitApi.this.notifyCallback(resultCallback, 1, "deleteSamples mApiAidl is null");
                    Log.w("HiHealthKit", "deleteSamples mApiAidl is null");
                } else if (hiHealthDataList != null && hiHealthDataList.size() <= 20) {
                    final int[] code = new int[]{4};
                    final Object[] message = new Object[1];

                    try {
                        List<HiHealthKitData> hiHealthKitDataList = new ArrayList();
                        Iterator var4 = hiHealthDataList.iterator();

                        while(var4.hasNext()) {
                            HiHealthData hiHealthData = (HiHealthData)var4.next();
                            int type = hiHealthData.getType();
                            Category category = HiHealthDataType.getCategory(type);
                            HiHealthKitData hiHealthKitData = new HiHealthKitData();
                            switch(category) {
                            case SET:
                                Log.i("HiHealthKit", "sample set");
                                SumianHiHealthKitApi.this.convertToSet((HiHealthSetData)hiHealthData, hiHealthKitData);
                            case POINT:
                            case SESSION:
                            case SEQUENCE:
                            case REALTIME:
                            case USERINFO:
                            case UNKOWN:
                            default:
                                Log.i("HiHealthKit", String.valueOf(hiHealthKitData.getStartTime()));
                                hiHealthKitDataList.add(hiHealthKitData);
                            }
                        }

                        SumianHiHealthKitApi.this.mApiAidl.deleteSamples(SumianHiHealthKitApi.this.getLastUuid(), hiHealthKitDataList, new com.huawei.hihealth.IDataOperateListener.Stub() {
                            public void onResult(int errorCode, List datas) {
                                Log.i("HiHealthKit", "enter saveSample result");
                                if (errorCode == 0) {
                                    code[0] = 0;
                                    message[0] = datas;
                                } else {
                                    code[0] = errorCode;
                                    message[0] = datas;
                                }

                            }
                        });
                    } catch (RemoteException var13) {
                        Log.i("HiHealthKit", "deleteSamples RemoteException");
                        code[0] = 4;
                        message[0] = "RemoteException";
                    } catch (Exception var14) {
                        Log.i("HiHealthKit", "deleteSamples Exception");
                        code[0] = 4;
                        message[0] = "Exception";
                    } finally {
                        if (null != resultCallback) {
                            resultCallback.onResult(code[0], message[0]);
                        }

                        Log.i("HiHealthKit", "saveSamples end");
                    }

                } else {
                    resultCallback.onResult(2, "too much datas!");
                }
            }
        });
    }

    public void startReadingHeartRate(final HiRealTimeListener hiRealTimeListener) {
        this.singleThreadPool.execute(new Runnable() {
            public void run() {
                if (hiRealTimeListener != null) {
                    SumianHiHealthKitApi.this.bindService();
                    if (SumianHiHealthKitApi.this.mApiAidl == null) {
                        hiRealTimeListener.onResult(1);
                        Log.w("HiHealthKit", "startReadingHeartRate mApiAidl is null");
                    } else {
                        try {
                            SumianHiHealthKitApi.this.mApiAidl.startReadingHeartRate(SumianHiHealthKitApi.this.getLastUuid(), new com.huawei.hihealth.IRealTimeDataCallback.Stub() {
                                public void onResult(int errCode) throws RemoteException {
                                    Log.i("HiHealthKit", "startReadingHeartRate onResult:" + errCode);
                                    hiRealTimeListener.onResult(errCode);
                                }

                                public void onChange(int errCode, String value) throws RemoteException {
                                    hiRealTimeListener.onChange(errCode, value);
                                }
                            });
                            Log.i("HiHealthKit", "startReadingHeartRate end");
                        } catch (RemoteException var2) {
                            Log.e("HiHealthKit", "startReadingHeartRate RemoteException");
                            hiRealTimeListener.onResult(4);
                        } catch (Exception var3) {
                            Log.e("HiHealthKit", "startReadingHeartRate Exception");
                            hiRealTimeListener.onResult(4);
                        }

                    }
                }
            }
        });
    }

    public void stopReadingHeartRate(final HiRealTimeListener hiRealTimeListener) {
        this.singleThreadPool.execute(new Runnable() {
            public void run() {
                if (hiRealTimeListener != null) {
                    SumianHiHealthKitApi.this.bindService();
                    if (SumianHiHealthKitApi.this.mApiAidl == null) {
                        hiRealTimeListener.onResult(1);
                        Log.w("HiHealthKit", "stopReadingHeartRate mApiAidl is null");
                    } else {
                        try {
                            SumianHiHealthKitApi.this.mApiAidl.stopReadingHeartRate(SumianHiHealthKitApi.this.getLastUuid(), new com.huawei.hihealth.IRealTimeDataCallback.Stub() {
                                public void onResult(int errCode) throws RemoteException {
                                    Log.i("HiHealthKit", "stopReadingHeartRate onResult:" + errCode);
                                    hiRealTimeListener.onResult(errCode);
                                }

                                public void onChange(int errCode, String value) throws RemoteException {
                                    hiRealTimeListener.onChange(errCode, value);
                                }
                            });
                            Log.i("HiHealthKit", "stopReadingHeartRate end");
                        } catch (RemoteException var2) {
                            Log.e("HiHealthKit", "stopReadingHeartRate RemoteException");
                            hiRealTimeListener.onResult(4);
                        } catch (Exception var3) {
                            Log.e("HiHealthKit", "stopReadingHeartRate Exception");
                            hiRealTimeListener.onResult(4);
                        }

                    }
                }
            }
        });
    }

    public void startReadingRri(final HiRealTimeListener hiRealTimeListener) {
        this.singleThreadPool.execute(new Runnable() {
            public void run() {
                if (hiRealTimeListener != null) {
                    SumianHiHealthKitApi.this.bindService();
                    if (SumianHiHealthKitApi.this.mApiAidl == null) {
                        hiRealTimeListener.onResult(1);
                        Log.w("HiHealthKit", "startReadingRri mApiAidl is null");
                    } else {
                        try {
                            SumianHiHealthKitApi.this.mApiAidl.startReadingRRI(SumianHiHealthKitApi.this.getLastUuid(), new com.huawei.hihealth.IRealTimeDataCallback.Stub() {
                                public void onResult(int errCode) throws RemoteException {
                                    Log.i("HiHealthKit", "startReadingRRI onResult:" + errCode);
                                    hiRealTimeListener.onResult(errCode);
                                }

                                public void onChange(int errCode, String value) throws RemoteException {
                                    hiRealTimeListener.onChange(errCode, value);
                                }
                            });
                            Log.i("HiHealthKit", "startReadingRRI end");
                        } catch (RemoteException var2) {
                            Log.e("HiHealthKit", "startReadingRRI RemoteException");
                            hiRealTimeListener.onResult(4);
                        } catch (Exception var3) {
                            Log.e("HiHealthKit", "startReadingRRI Exception");
                            hiRealTimeListener.onResult(4);
                        }

                    }
                }
            }
        });
    }

    public void stopReadingRri(final HiRealTimeListener hiRealTimeListener) {
        this.singleThreadPool.execute(new Runnable() {
            public void run() {
                if (hiRealTimeListener != null) {
                    SumianHiHealthKitApi.this.bindService();
                    if (SumianHiHealthKitApi.this.mApiAidl == null) {
                        hiRealTimeListener.onResult(1);
                        Log.w("HiHealthKit", "stopReadingRri mApiAidl is null");
                    } else {
                        try {
                            SumianHiHealthKitApi.this.mApiAidl.stopReadingRRI(SumianHiHealthKitApi.this.getLastUuid(), new com.huawei.hihealth.IRealTimeDataCallback.Stub() {
                                public void onResult(int errCode) throws RemoteException {
                                    Log.i("HiHealthKit", "stopReadingRRI onResult:" + errCode);
                                    hiRealTimeListener.onResult(errCode);
                                }

                                public void onChange(int errCode, String value) throws RemoteException {
                                    hiRealTimeListener.onChange(errCode, value);
                                }
                            });
                            Log.i("HiHealthKit", "stopReadingRRI end");
                        } catch (RemoteException var2) {
                            Log.e("HiHealthKit", "stopReadingRRI RemoteException");
                            hiRealTimeListener.onResult(4);
                        } catch (Exception var3) {
                            Log.e("HiHealthKit", "stopReadingRRI Exception");
                            hiRealTimeListener.onResult(4);
                        }

                    }
                }
            }
        });
    }

    public void getDeviceList(final ResultCallback resultCallback) {
        this.singleThreadPool.execute(new Runnable() {
            public void run() {
                SumianHiHealthKitApi.this.bindService();
                if (SumianHiHealthKitApi.this.mApiAidl == null) {
                    SumianHiHealthKitApi.this.notifyCallback(resultCallback, 1, "getDeviceList mApiAidl is null");
                    Log.w("HiHealthKit", "getDeviceList mApiAidl is null");
                } else {
                    try {
                        SumianHiHealthKitApi.this.mApiAidl.getDeviceList(SumianHiHealthKitApi.this.getLastUuid(), new com.huawei.hihealth.IRealTimeDataCallback.Stub() {
                            public void onResult(int errCode) throws RemoteException {
                                Log.i("HiHealthKit", "getDeviceList onResult");
                                SumianHiHealthKitApi.this.notifyCallback(resultCallback, errCode, (Object)null);
                            }

                            public void onChange(int errCode, String value) throws RemoteException {
                                SumianHiHealthKitApi.this.notifyCallback(resultCallback, errCode, value);
                            }
                        });
                    } catch (RemoteException var2) {
                        Log.i("HiHealthKit", "getDeviceList RemoteException");
                        SumianHiHealthKitApi.this.notifyCallback(resultCallback, 1, "failed");
                    }

                }
            }
        });
    }

    public void sendDeviceCommand(final String commandOptions, final ResultCallback resultCallback) {
        this.singleThreadPool.execute(new Runnable() {
            public void run() {
                SumianHiHealthKitApi.this.bindService();
                if (SumianHiHealthKitApi.this.mApiAidl == null) {
                    SumianHiHealthKitApi.this.notifyCallback(resultCallback, 1, "sendDeviceCommand mApiAidl is null");
                    Log.w("HiHealthKit", "sendDeviceCommand mApiAidl is null");
                } else {
                    try {
                        SumianHiHealthKitApi.this.mApiAidl.sendDeviceCommand(SumianHiHealthKitApi.this.getLastUuid(), commandOptions, new com.huawei.hihealth.IRealTimeDataCallback.Stub() {
                            public void onResult(int errCode) throws RemoteException {
                                Log.i("HiHealthKit", "sendDeviceCommand onResult errCode = " + errCode);
                                SumianHiHealthKitApi.this.notifyCallback(resultCallback, errCode, (Object)null);
                            }

                            public void onChange(int errCode, String value) throws RemoteException {
                                SumianHiHealthKitApi.this.notifyCallback(resultCallback, errCode, value);
                            }
                        });
                    } catch (RemoteException var2) {
                        Log.i("HiHealthKit", "sendDeviceCommand RemoteException");
                        SumianHiHealthKitApi.this.notifyCallback(resultCallback, 1, "failed");
                    }

                }
            }
        });
    }

    public void startReadingAtrial(final ResultCallback resultCallback) {
        this.singleThreadPool.execute(new Runnable() {
            public void run() {
                SumianHiHealthKitApi.this.bindService();
                if (SumianHiHealthKitApi.this.mApiAidl == null) {
                    SumianHiHealthKitApi.this.notifyCallback(resultCallback, 1, "startReadingAtrial mApiAidl is null");
                    Log.w("HiHealthKit", "startReadingAtrial mApiAidl is null");
                } else {
                    try {
                        SumianHiHealthKitApi.this.mApiAidl.startReadingAtrial(SumianHiHealthKitApi.this.getLastUuid(), new com.huawei.hihealth.IRealTimeDataCallback.Stub() {
                            public void onResult(int errCode) throws RemoteException {
                                Log.i("HiHealthKit", "startReadingAtrial onResult errCode = " + errCode);
                                SumianHiHealthKitApi.this.notifyCallback(resultCallback, errCode, (Object)null);
                            }

                            public void onChange(int errCode, String value) throws RemoteException {
                                SumianHiHealthKitApi.this.notifyCallback(resultCallback, errCode, value);
                            }
                        });
                    } catch (RemoteException var2) {
                        Log.i("HiHealthKit", "startReadingAtrial RemoteException");
                        SumianHiHealthKitApi.this.notifyCallback(resultCallback, 1, "failed");
                    }

                }
            }
        });
    }

    public void stopReadingAtrial(final ResultCallback resultCallback) {
        this.singleThreadPool.execute(new Runnable() {
            public void run() {
                SumianHiHealthKitApi.this.bindService();
                if (SumianHiHealthKitApi.this.mApiAidl == null) {
                    SumianHiHealthKitApi.this.notifyCallback(resultCallback, 1, "stopReadingAtrial mApiAidl is null");
                    Log.w("HiHealthKit", "stopReadingAtrial mApiAidl is null");
                } else {
                    try {
                        SumianHiHealthKitApi.this.mApiAidl.stopReadingAtrial(SumianHiHealthKitApi.this.getLastUuid(), new com.huawei.hihealth.IRealTimeDataCallback.Stub() {
                            public void onResult(int errCode) throws RemoteException {
                                Log.i("HiHealthKit", "stopReadingAtrial onResult errCode = " + errCode);
                                SumianHiHealthKitApi.this.notifyCallback(resultCallback, errCode, (Object)null);
                            }

                            public void onChange(int errCode, String value) throws RemoteException {
                                SumianHiHealthKitApi.this.notifyCallback(resultCallback, errCode, value);
                            }
                        });
                    } catch (RemoteException var2) {
                        Log.i("HiHealthKit", "stopReadingAtrial RemoteException");
                        SumianHiHealthKitApi.this.notifyCallback(resultCallback, 1, "failed");
                    }

                }
            }
        });
    }

    public void pushMsgToWearable(final String inputType, final String message, final ResultCallback callback) {
        this.singleThreadPool.execute(new Runnable() {
            public void run() {
                SumianHiHealthKitApi.this.bindService();
                if (SumianHiHealthKitApi.this.mApiAidl == null) {
                    Log.e("HiHealthKit", "pushMsgToWearable:mApiAidl is null");
                    SumianHiHealthKitApi.this.notifyCallback(callback, 1, "failed");
                } else {
                    try {
                        SumianHiHealthKitApi.this.mApiAidl.pushMsgToWearable(inputType, message, new com.huawei.hihealth.ICommonCallback.Stub() {
                            public void onResult(int errCode, String messagex) throws RemoteException {
                                callback.onResult(errCode, messagex);
                            }
                        });
                    } catch (RemoteException var2) {
                        Log.e("HiHealthKit", "pushMsgToWearable RemoteException");
                        SumianHiHealthKitApi.this.notifyCallback(callback, 1, "failed");
                    }

                }
            }
        });
    }

    public void readFromWearable(final String inputType, final String fileDescription, final OutputStream outputStream, final ResultCallback readCallback) {
        this.singleThreadPool.execute(new Runnable() {
            public void run() {
                SumianHiHealthKitApi.this.bindService();
                if (SumianHiHealthKitApi.this.mApiAidl == null) {
                    Log.i("HiHealthKit", "readFromWearable:mApiAidl is null");
                    SumianHiHealthKitApi.this.notifyCallback(readCallback, 1, "failed");
                } else {
                    try {
                        SumianHiHealthKitApi.this.mApiAidl.readFromWearable(inputType, fileDescription, new com.huawei.hihealth.IReadCallback.Stub() {
                            public void onResult(int errCode, String message, byte[] data) throws RemoteException {
                                if (errCode == 0) {
                                    try {
                                        if (outputStream != null && data != null) {
                                            outputStream.write(data);
                                        }
                                    } catch (IOException var5) {
                                        Log.i("HiHealthKit", "readFromWearable IOException");
                                        SumianHiHealthKitApi.this.notifyCallback(readCallback, 1, "failed");
                                        return;
                                    }

                                    readCallback.onResult(0, message);
                                } else {
                                    readCallback.onResult(errCode, message);
                                }

                            }
                        });
                    } catch (RemoteException var2) {
                        Log.i("HiHealthKit", "readFromWearable RemoteException");
                        SumianHiHealthKitApi.this.notifyCallback(readCallback, 1, "failed");
                    }

                }
            }
        });
    }

    public void writeToWearable(final String inputType, final String inputDescription, final InputStream inputStream, final ResultCallback writeCallback) {
        Log.i("HiHealthKit", "writeToWearable");
        this.singleThreadPool.execute(new Runnable() {
            public void run() {
                SumianHiHealthKitApi.this.bindService();
                if (SumianHiHealthKitApi.this.mApiAidl == null) {
                    Log.i("HiHealthKit", "writeToWearable:mApiAidl is null");
                    SumianHiHealthKitApi.this.notifyCallback(writeCallback, 1, "failed");
                } else {
                    if (inputStream != null) {
                        Log.i("HiHealthKit", "writeToWearable is a big file.");

                        try {
                            int available = inputStream.available();
                            int size = available;
                            boolean isFinished = false;
                            byte[] defaultBuffer = new byte['저'];

                            while(available > 0) {
                                byte[] data = null;
                                byte[] datax;
                                if (available >= 51200) {
                                    datax = defaultBuffer;
                                } else {
                                    datax = new byte[available];
                                    isFinished = true;
                                }

                                int readSize = inputStream.read(datax);
                                available -= readSize;
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("size", size);
                                jsonObject.put("is_finished", isFinished);
                                SumianHiHealthKitApi.this.writeToWearable(inputType, inputDescription, datax, jsonObject.toString(), writeCallback);
                            }
                        } catch (IOException var18) {
                            Log.i("HiHealthKit", "writeToWearable IOException");
                            SumianHiHealthKitApi.this.notifyCallback(writeCallback, 1, "failed");
                            SumianHiHealthKitApi.this.writeToWearable(inputType, inputDescription, (byte[])null, (String)null, writeCallback);
                        } catch (JSONException var19) {
                            Log.i("HiHealthKit", "writeToWearable JSONException");
                            SumianHiHealthKitApi.this.notifyCallback(writeCallback, 1, "failed");
                            SumianHiHealthKitApi.this.writeToWearable(inputType, inputDescription, (byte[])null, (String)null, writeCallback);
                        } finally {
                            try {
                                inputStream.close();
                            } catch (IOException var17) {
                                Log.i("HiHealthKit", "writeToWearable:close inputStream IOException");
                            }

                        }
                    } else {
                        Log.i("HiHealthKit", "writeToWearable is not a big file.");
                        SumianHiHealthKitApi.this.writeToWearable(inputType, inputDescription, (byte[])null, (String)null, writeCallback);
                    }

                }
            }
        });
    }

    public void startRealTimeSportData(final HiSportDataCallback callback) {
        Log.i("HiHealthKit", "startRealTimeSportData");
        if (callback == null) {
            Log.w("HiHealthKit", "startRealTimeSportData callback is null");
        } else {
            this.singleThreadPool.execute(new Runnable() {
                public void run() {
                    SumianHiHealthKitApi.this.bindService();
                    if (SumianHiHealthKitApi.this.mApiAidl == null) {
                        Log.w("HiHealthKit", "fetchRealTimeSportData mApiAidl is null");
                        callback.onResult(1);
                    } else {
                        try {
                            SumianHiHealthKitApi.this.mApiAidl.registerRealTimeSportCallback(new com.huawei.hihealth.ISportDataCallback.Stub() {
                                public void onResult(int errorCode) throws RemoteException {
                                    Log.i("HiHealthKit", "startRealTimeSportData onResult errCode = " + errorCode);
                                    callback.onResult(errorCode);
                                }

                                public void onDataChanged(int sportState, Bundle bundle) {
                                    Log.i("HiHealthKit", "startRealTimeSportData onDataChanged sportState = " + sportState);
                                    Log.i("HiHealthKit", "startRealTimeSportData onDataChanged bundle = " + bundle);
                                    callback.onDataChanged(sportState, bundle);
                                }
                            });
                        } catch (RemoteException var2) {
                            Log.w("HiHealthKit", "startRealTimeSportData RemoteException");
                            callback.onResult(1);
                        }

                    }
                }
            });
        }
    }

    public void stopRealTimeSportData(final HiSportDataCallback callback) {
        Log.i("HiHealthKit", "stopRealTimeSportData");
        if (callback == null) {
            Log.w("HiHealthKit", "stopRealTimeSportData callback is null");
        } else {
            this.singleThreadPool.execute(new Runnable() {
                public void run() {
                    SumianHiHealthKitApi.this.bindService();
                    if (SumianHiHealthKitApi.this.mApiAidl == null) {
                        Log.w("HiHealthKit", "stopRealTimeSportData mApiAidl is null");
                        callback.onResult(1);
                    } else {
                        try {
                            SumianHiHealthKitApi.this.mApiAidl.unregisterRealTimeSportCallback(new com.huawei.hihealth.ICommonCallback.Stub() {
                                public void onResult(int resultCode, String message) throws RemoteException {
                                    Log.i("HiHealthKit", "stopRealTimeSportData errorCode  = " + resultCode + ", message = " + message);
                                    callback.onResult(resultCode);
                                }
                            });
                        } catch (RemoteException var2) {
                            Log.w("HiHealthKit", "stopRealTimeSportData RemoteException");
                            callback.onResult(1);
                        }

                    }
                }
            });
        }
    }

    private void writeToWearable(String inputType, String inputDescription, byte[] data, String info, final ResultCallback writeCallback) {
        try {
            this.mApiAidl.writeToWearable(inputType, inputDescription, data, info, new com.huawei.hihealth.IWriteCallback.Stub() {
                public void onResult(int errCode, String message) throws RemoteException {
                    writeCallback.onResult(errCode, message);
                }
            });
        } catch (RemoteException var7) {
            Log.i("HiHealthKit", "writeToWearable RemoteException");
            this.notifyCallback(writeCallback, 1, "failed");
        }

    }

    private void convertToSet(HiHealthSetData hiHealthData, HiHealthKitData hiHealthKitData) {
        if (hiHealthData != null && hiHealthKitData != null) {
            Log.i("HiHealthKit", "converToSet not null");
            long startTime = hiHealthData.getStartTime();
            long endTime = hiHealthData.getEndTime();
            HiHealthDeviceInfo hiHealthDevice = hiHealthData.getSourceDevice();
            if (hiHealthDevice != null) {
                hiHealthKitData.putString("device_uniquecode", hiHealthDevice.getDeviceUniqueCode());
                hiHealthKitData.putString("device_name", hiHealthDevice.getDeviceName());
                hiHealthKitData.putString("device_model", hiHealthDevice.getDeviceModel());
            }

            int type = hiHealthData.getType();
            Map dataMaps = hiHealthData.getMap();
            hiHealthKitData.setStartTime(startTime);
            hiHealthKitData.setEndTime(endTime);
            hiHealthKitData.setType(type);
            hiHealthKitData.setMap(dataMaps);
        } else {
            Log.i("HiHealthKit", "convertToSet fail input null");
        }

    }

    private void notifyCallback(ResultCallback listener, int stateCode, Object detailMessage) {
        if (listener != null) {
            listener.onResult(stateCode, detailMessage);
        }

    }

    private static class Instance {
        public static final SumianHiHealthKitApi INSTANCE = new SumianHiHealthKitApi();

        private Instance() {
        }
    }
}
