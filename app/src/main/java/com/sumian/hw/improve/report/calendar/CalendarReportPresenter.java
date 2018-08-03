package com.sumian.hw.improve.report.calendar;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.sumian.hw.network.callback.BaseResponseCallback;
import com.sumian.sleepdoctor.app.HwAppManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;

/**
 * Created by sm
 * on 2018/3/7.
 * desc:
 * <p>
 * {
 * "1519833600":[
 * <p>
 * ],
 * "1522512000":[
 * {
 * "id":741,
 * "date":1524499200,
 * "is_read":true,
 * "is_today":false,
 * "has_doctors_evaluation":false
 * },
 * {
 * "id":743,
 * "date":1524585600,
 * "is_read":true,
 * "is_today":false,
 * "has_doctors_evaluation":true
 * },
 * {
 * "id":745,
 * "date":1524326400,
 * "is_read":true,
 * "is_today":false,
 * "has_doctors_evaluation":false
 * },
 * {
 * "id":748,
 * "date":1524412800,
 * "is_read":true,
 * "is_today":false,
 * "has_doctors_evaluation":false
 * },
 * {
 * "id":749,
 * "date":1524240000,
 * "is_read":true,
 * "is_today":false,
 * "has_doctors_evaluation":false
 * },
 * {
 * "id":750,
 * "date":1524153600,
 * "is_read":true,
 * "is_today":false,
 * "has_doctors_evaluation":false
 * },
 * {
 * "id":751,
 * "date":1524067200,
 * "is_read":true,
 * "is_today":false,
 * "has_doctors_evaluation":false
 * },
 * {
 * "id":752,
 * "date":1523980800,
 * "is_read":true,
 * "is_today":false,
 * "has_doctors_evaluation":false
 * },
 * {
 * "id":753,
 * "date":1523894400,
 * "is_read":true,
 * "is_today":false,
 * "has_doctors_evaluation":false
 * },
 * {
 * "id":754,
 * "date":1523808000,
 * "is_read":true,
 * "is_today":false,
 * "has_doctors_evaluation":false
 * },
 * {
 * "id":755,
 * "date":1523721600,
 * "is_read":true,
 * "is_today":false,
 * "has_doctors_evaluation":false
 * },
 * {
 * "id":756,
 * "date":1523548800,
 * "is_read":true,
 * "is_today":false,
 * "has_doctors_evaluation":true
 * },
 * {
 * "id":757,
 * "date":1523635200,
 * "is_read":true,
 * "is_today":false,
 * "has_doctors_evaluation":false
 * }
 * ],
 * "1525104000":[
 * {
 * "id":768,
 * "date":1525363200,
 * "is_read":true,
 * "is_today":false,
 * "has_doctors_evaluation":false
 * },
 * {
 * "id":767,
 * "date":1526400000,
 * "is_read":true,
 * "is_today":false,
 * "has_doctors_evaluation":false
 * }
 * ],
 * "has_history_unread":false,
 * "earliest_month":1514736000
 * }
 */
public class CalendarReportPresenter implements CalendarReportContract.Presenter {

    @SuppressWarnings("unused")
    private static final String TAG = CalendarReportPresenter.class.getSimpleName();

    private CalendarReportContract.View mView;

    private CalendarReportPresenter(CalendarReportContract.View view) {
        view.setPresenter(this);
        this.mView = view;
    }

    public static void init(CalendarReportContract.View view) {
        new CalendarReportPresenter(view);
    }

    @Override
    public void getOneCalendarReportInfo(long monthInDayUnixTime, boolean isInclude) {
        if (mView == null) return;
        mView.onBegin();

        Map<String, Object> map = new HashMap<>(0);
        map.put("date", monthInDayUnixTime);
        map.put("page_size", 3);
        map.put("is_include", isInclude ? 1 : 0);

        Call<JsonObject> call = HwAppManager.getHwV1HttpService().getCalendarSleepReport(map);
        mCalls.add(call);

        call.enqueue(new BaseResponseCallback<JsonObject>() {

            @SuppressWarnings("unchecked")
            @Override
            protected void onSuccess(JsonObject response) {
                JSONObject jsonObject = JSON.parseObject(response.toString());

                Integer earliestMonth = (Integer) jsonObject.get("earliest_month");
                int startMonth;
                if (earliestMonth == null) {
                    startMonth = (int) monthInDayUnixTime;
                } else {
                    startMonth = earliestMonth;
                }

                List<PagerCalendarItem> pagerCalendarItems = new ArrayList<>(0);
                PagerCalendarItem pagerCalendarItem;
                Set<Map.Entry<String, Object>> entries = jsonObject.entrySet();
                for (Map.Entry<String, Object> entry : entries) {

                    String key = entry.getKey();
                    Object value = entry.getValue();

                    if (value instanceof JSONArray) {

                        if (Integer.valueOf(key) < startMonth) continue;

                        JSONArray jsonArray = (JSONArray) value;
                        List<CalendarItemSleepReport> calendarItemSleepReports = jsonArray.toJavaList(CalendarItemSleepReport.class);

                        pagerCalendarItem = new PagerCalendarItem();

                        pagerCalendarItem.monthTimeUnix = Integer.valueOf(key);
                        pagerCalendarItem.initTimeUnix = startMonth;
                        pagerCalendarItem.mCalendarItemSleepReports = calendarItemSleepReports;

                        pagerCalendarItems.add(pagerCalendarItem);
                    }
                }

                Collections.sort(pagerCalendarItems);

                mView.onGetOneCalendarReportInfoSuccess(pagerCalendarItems);
            }

            @Override
            protected void onFailure(String error) {
                mView.onFailure(error);
            }

            @Override
            protected void onFinish() {
                super.onFinish();
                mView.onFinish();
            }
        });

    }
}
