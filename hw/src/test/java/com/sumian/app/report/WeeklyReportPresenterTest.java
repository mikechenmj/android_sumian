package com.sumian.app.report;

import com.sumian.app.common.util.TimeUtil;
import com.sumian.app.network.response.SleepDurationReport;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * <pre>
 *     author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/5/4 21:06
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class WeeklyReportPresenterTest {

    @Test
    public void testCalendar() {
        long date = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(date));
        TimeUtil.printCalendar(calendar);
        TimeUtil.rollDay(calendar, -7);
        TimeUtil.printCalendar(calendar);
        TimeUtil.rollDay(calendar, -7);
        TimeUtil.printCalendar(calendar);
    }

    @Test
    public void test() {
        List<SleepDurationReport> data = getRandomData();
        System.out.println("origin data");
        printData(data);
        long currentTimeMillis = System.currentTimeMillis();
        List<SleepDurationReport> resolvedData = resolveDataFromServer(currentTimeMillis, data, 5);
        System.out.println("resolved data");
        printData(resolvedData);
    }

    private void printData(List<SleepDurationReport> sleepDurationReports) {
        if (sleepDurationReports == null) {
            System.out.println("empty");
            return;
        }
        System.out.println(String.format(Locale.getDefault(), "data size: %d", sleepDurationReports.size()));
        for (SleepDurationReport report : sleepDurationReports) {
            System.out.println(report.getStart_date());
        }
    }

    private List<SleepDurationReport> getRandomData() {
        long weekStartDayTime = TimeUtil.getStartTimeOfWeek(System.currentTimeMillis());
        Calendar calendar = TimeUtil.getCalendar(weekStartDayTime);
        Random random = new Random();

        int size = random.nextInt(6);
        List<SleepDurationReport> data = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            data.add(SleepDurationReport.createFromTime(calendar.getTimeInMillis()));
            int weekInterval = random.nextInt(3) + 1;
            TimeUtil.rollDay(calendar, -7 * weekInterval);
        }
        return data;
    }

    @SuppressWarnings("SameParameterValue")
    private static List<SleepDurationReport> resolveDataFromServer(long earliestReportTime, List<SleepDurationReport> data, int minDataSize) {
        earliestReportTime = TimeUtil.getStartTimeOfWeek(earliestReportTime);
        Calendar calendar = TimeUtil.getCalendar(earliestReportTime);
        ArrayList<SleepDurationReport> resolvedData = new ArrayList<>();
        if (data == null || data.size() == 0) {
            for (int i = 0; i < minDataSize; i++) {
                SleepDurationReport fromTime = SleepDurationReport.createFromTime(calendar.getTimeInMillis());
                resolvedData.add(fromTime);
                TimeUtil.rollDay(calendar, -7);
            }
        } else {
            Iterator<SleepDurationReport> iterator = data.iterator();
            while ((iterator.hasNext())) {
                // 填补空缺数据
                SleepDurationReport next = iterator.next();
                long startDateShowInMillis = next.getStartDateShowInMillis();
                while (startDateShowInMillis < calendar.getTimeInMillis()) {
                    resolvedData.add(SleepDurationReport.createFromTime(calendar.getTimeInMillis()));
                    TimeUtil.rollDay(calendar, -7);
                }
                resolvedData.add(next);
                TimeUtil.rollDay(calendar, -7);
            }
            // 如果补空完后，数据不足 minDataSize 个则继续生产几个数据
            while (resolvedData.size() < minDataSize) {
                TimeUtil.rollDay(calendar, -7);
                resolvedData.add(SleepDurationReport.createFromTime(calendar.getTimeInMillis()));
            }
        }
        return resolvedData;
    }
}
