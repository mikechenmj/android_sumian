package com.sumian.sd.account.login;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.sumian.common.operator.AppOperator;
import com.sumian.hw.common.util.StreamUtil;
import com.sumian.hw.network.api.SleepyApi;
import com.sumian.hw.network.callback.BaseResponseCallback;
import com.sumian.sd.account.bean.City;
import com.sumian.sd.account.bean.Province;
import com.sumian.sd.account.bean.UserInfo;
import com.sumian.sd.app.AppManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;

/**
 * Created by jzz
 * on 2017/10/26.
 * <p>
 * desc:
 */

public class ModifySelectPresenter implements ModifySelectContract.Presenter {

    private static final String TAG = ModifySelectPresenter.class.getSimpleName();
    private static final int MIN_YEAR = 1920;
    private static final int MIN_HEIGHT = 80;
    private static final int MAX_HEIGHT = 250;
    private static final int MIN_WEIGHT = 20;
    private static final int MAX_WEIGHT = 200;

    private static final int DEFAULT_YEAR = 1980;
    private static final int DEFAULT_WEIGHT = 50;
    private static final int DEFAULT_HEIGHT = 170;


    private WeakReference<ModifySelectContract.View<UserInfo>> mViewWeakReference;
    private Call mCall;

    private List<Province> mProvinces;
    //private Map<Province, List<City>> mMapCities;
    private Map<City, List<String>> mMapArea;
    private UserInfo mUserInfo;

    private ModifySelectPresenter(ModifySelectContract.View<UserInfo> view) {
        view.setPresenter(this);
        this.mViewWeakReference = new WeakReference<>(view);
    }

    public static void init(ModifySelectContract.View<UserInfo> view) {
        new ModifySelectPresenter(view);
    }

    @Override
    public void release() {
        Call call = this.mCall;
        if (call == null) return;
        if (call.isExecuted()) {
            call.cancel();
        }
    }

    @Override
    public void transformFormKey(String formKey, UserInfo userInfo) {
        mUserInfo = userInfo;
        switch (formKey) {
            case ModifyUserInfoContract.KEY_AREA:
                transformProvince();
                break;
            case ModifyUserInfoContract.KEY_BIRTHDAY:
                transformBirthday(MIN_YEAR, userInfo);
                break;
            case ModifyUserInfoContract.KEY_HEIGHT:
                transformHeight(MIN_HEIGHT, MAX_HEIGHT, userInfo);
                break;
            case ModifyUserInfoContract.KEY_WEIGHT:
                transformWeight(MIN_WEIGHT, MAX_WEIGHT, userInfo);
                break;
            default:
                break;
        }
    }

    @Override
    public Object transformFormValue(String formKey, String oneValue, String twoValue, String threeValue) {
        return transformValue(formKey, oneValue, twoValue, threeValue);
    }

    @Override
    public void transformCityForProvince(String province) {
        AppOperator.runOnThread(() -> {
            List<Province> provinces = this.mProvinces;
            if (provinces == null || provinces.isEmpty()) return;

            Province p = null;
            for (int i = 0, len = provinces.size(); i < len; i++) {
                p = provinces.get(i);
                if (province.equals(p.name)) {
                    break;
                }
            }
            transformCity(p);
        });
    }

    @Override
    public void transformAreaForCity(String city) {
        AppOperator.runOnThread(() -> {

            List<String> areas = null;
            for (Map.Entry<City, List<String>> cityListEntry : mMapArea.entrySet()) {
                City key = cityListEntry.getKey();
                if (key.name.equals(city)) {
                    areas = key.area;
                    break;
                }
            }
            transformArea(areas);
        });
    }


    @Override
    public void doModifyUserInfo(String formKey, Object formValue) {
        WeakReference<ModifySelectContract.View<UserInfo>> viewWeakReference = this.mViewWeakReference;
        ModifySelectContract.View<UserInfo> view = viewWeakReference.get();
        if (view == null) return;
        view.onBegin();
        SleepyApi sleepyApi = AppManager.getHwNetEngine().getHttpService();
        Map<String, Object> map = new HashMap<>();
        map.put(formKey, formValue);
        map.put("include", "doctor");
        Call<UserInfo> call = sleepyApi.doModifyUserInfo(map);
        call.enqueue(new BaseResponseCallback<UserInfo>() {
            @Override
            protected void onSuccess(UserInfo response) {
                view.onModifySuccess(response);
                AppManager.getAccountViewModel().updateUserInfo(response);
            }

            @Override
            protected void onFailure(int code, String error) {
                view.onModifyFailed(error);
            }

            @Override
            protected void onFinish() {
                view.onFinish();
            }
        });

        this.mCall = call;
    }

    /**
     * 当用户未设置相关用户信息时,计算出默认值 [min ~ default  ~ max]
     *
     * @param defaultValue defaultValue
     * @param minValue     minValue
     * @return DefaultValuePosition
     */
    private int calculateDefaultPosition(int defaultValue, int minValue) {
        return defaultValue - minValue;
    }

    private Object transformValue(String formKey, String oneValue, String twoValue, String threeValue) {
        Object formValue = null;
        switch (formKey) {
            case ModifyUserInfoContract.KEY_AREA:
                formValue = oneValue + "/" + twoValue + "/" + threeValue;
                break;
            case ModifyUserInfoContract.KEY_BIRTHDAY:
                formValue = oneValue + "-" + twoValue;
                break;
            case ModifyUserInfoContract.KEY_HEIGHT:
                formValue = Float.valueOf(oneValue + "." + twoValue);
                break;
            case ModifyUserInfoContract.KEY_WEIGHT:
                formValue = Float.valueOf(oneValue + "." + twoValue);
                break;
        }

        return formValue;
    }

    private void transformWeight(int minWeight, int maxWeight, UserInfo userInfo) {

        int count = maxWeight - minWeight;

        String[] weights = new String[count];
        String[] decimalWeights = new String[10];

        String weight = userInfo.getWeight();
        if (!TextUtils.isEmpty(weight)) {
            weight = String.valueOf((int) Float.parseFloat(weight));
        }

        float weightValue = userInfo.getWeightValue();
        int valueX10 = (int) (weightValue * 10);
        int numberOnePosition = valueX10 / 10 - minWeight;
        if (numberOnePosition < 0) {
            numberOnePosition = calculateDefaultPosition(DEFAULT_WEIGHT, minWeight);
        }
        int numberTwoPosition = valueX10 % 10;
        for (int i = 0; i < count; i++) {
            weights[i] = String.valueOf(minWeight + i);
            if (weights[i].equals(weight)) {
                Log.e(TAG, "position=" + i + " weight=" + weights[i]);
            }
            if (i < 10) {
                decimalWeights[i] = String.valueOf(i);
            }
        }

        WeakReference<ModifySelectContract.View<UserInfo>> viewWeakReference = this.mViewWeakReference;
        ModifySelectContract.View<UserInfo> view = viewWeakReference.get();
        if (view == null) return;
        view.transformOneDisplayedValues(numberOnePosition, ".", weights);
        view.transformTwoDisplayedValues(numberTwoPosition, "kg", decimalWeights);
    }

    private void transformHeight(int minHeight, int maxHeight, UserInfo userInfo) {
        int count = maxHeight - minHeight;
        String[] heights = new String[count];
        String[] decimalHeights = new String[10];
        float heightValue = userInfo.getHeightValue();
        int valueX10 = (int) (heightValue * 10);
        int numberOnePosition = valueX10 / 10 - minHeight;
        if (numberOnePosition < 0) {
            numberOnePosition = calculateDefaultPosition(DEFAULT_HEIGHT, minHeight);
        }
        int numberTwoPosition = valueX10 % 10;
        for (int i = 0; i < count; i++) {
            heights[i] = String.valueOf(minHeight + i);
            if (i < 10) {
                decimalHeights[i] = String.valueOf(i);
            }
        }

        WeakReference<ModifySelectContract.View<UserInfo>> viewWeakReference = this.mViewWeakReference;
        ModifySelectContract.View<UserInfo> view = viewWeakReference.get();
        if (view == null) return;
        view.transformOneDisplayedValues(numberOnePosition, ".", heights);
        view.transformTwoDisplayedValues(numberTwoPosition, "cm", decimalHeights);
    }

    private void transformBirthday(int minYear, UserInfo userInfo) {
        int year = Calendar.getInstance().get(Calendar.YEAR) + 1;
        int count = year - minYear;
        String[] years = new String[count];

        int monthCount = 12;
        if (userInfo.getBirthdayYear() == Calendar.getInstance().get(Calendar.YEAR)) {
            monthCount = Calendar.getInstance().get(Calendar.MONTH) + 1;
        }
        String[] months = new String[monthCount];

        int numberOnePosition = userInfo.getBirthdayYear() - minYear;
        if (numberOnePosition < 0) {
            numberOnePosition = calculateDefaultPosition(DEFAULT_YEAR, minYear);
        }
        int numberTwoPosition = userInfo.getBirthdayMonth() - 1;
        if (numberTwoPosition < 0) {
            numberTwoPosition = 0;
        }
        for (int i = 0; i < count; i++) {
            years[i] = String.valueOf(minYear + i);
        }
        for (int i = 0; i < months.length; i++) {
            months[i] = String.format(Locale.getDefault(), "%02d", i + 1);
        }
        WeakReference<ModifySelectContract.View<UserInfo>> viewWeakReference = this.mViewWeakReference;
        ModifySelectContract.View<UserInfo> view = viewWeakReference.get();
        if (view == null) return;
        view.transformOneDisplayedValues(numberOnePosition, "年", years);
        view.transformTwoDisplayedValues(numberTwoPosition, "月", months);
    }

    private void transformProvince() {
        AppOperator.runOnThread(() -> {
            String provinceJson = StreamUtil.getJson("province.json");
            if (TextUtils.isEmpty(provinceJson)) return;
            List<Province> provinces = JSON.parseArray(provinceJson, Province.class);
            Map<City, List<String>> mapArea = this.mMapArea;
            if (mapArea == null) {
                mapArea = new HashMap<>();
            }
            String[] provinceNames = new String[provinces.size()];
            int position = 0;
            for (int i = 0, len = provinces.size(); i < len; i++) {
                Province province = provinces.get(i);
                String name = province.name;
                if (name != null && name.equals(mUserInfo.getAddressArray()[0])) {
                    position = i;
                }
                provinceNames[i] = name;
                List<City> cities = province.city;
                for (City city : cities) {
                    List<String> areas = city.area;
                    if (mapArea.containsKey(city)) {
                        mapArea.get(city).addAll(areas);
                    } else {
                        mapArea.put(city, areas);
                    }
                }
            }
            WeakReference<ModifySelectContract.View<UserInfo>> viewWeakReference = mViewWeakReference;
            ModifySelectContract.View<UserInfo> view = viewWeakReference.get();
            if (view == null) return;
            view.transformOneDisplayedValues(position, null, provinceNames);
            transformCity(provinces.get(position));
            this.mProvinces = provinces;
            this.mMapArea = mapArea;
        });
    }

    private void transformCity(Province province) {
        List<City> cities = province.city;
        List<String> cityNames = new ArrayList<>();
        String cityName;
        int position = 0;
        for (int i = 0; i < cities.size(); i++) {
            City city = cities.get(i);
            cityName = city.name;
            if (cityName != null && cityName.equals(mUserInfo.getAddressArray()[1])) {
                position = i;
            }
            if ("其他".equals(cityName) || "其他市".equals(cityName)) continue;
            cityNames.add(cityName);
        }
        WeakReference<ModifySelectContract.View<UserInfo>> viewWeakReference = mViewWeakReference;
        ModifySelectContract.View<UserInfo> view = viewWeakReference.get();
        if (view == null) return;
        view.transformTwoDisplayedValues(position, null, cityNames.toArray(new String[]{}));
        transformArea(cities.get(position).area);

    }

    private void transformArea(List<String> areas) {
        if (areas == null) {
            return;
        }
        int position = 0;
        for (int i = 0; i < areas.size(); i++) {
            String areaName = areas.get(i);
            if (areaName != null && areaName.equals(mUserInfo.getAddressArray()[2])) {
                position = i;
            }
            if ("其他".equals(areaName)) areas.remove(areaName);
        }
        WeakReference<ModifySelectContract.View<UserInfo>> viewWeakReference = mViewWeakReference;
        ModifySelectContract.View<UserInfo> view = viewWeakReference.get();
        if (view == null) return;
        view.transformThreeDisplayedValues(position, null, areas.toArray(new String[]{}));
    }
}
