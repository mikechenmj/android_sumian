package com.sumian.hw.improve.widget.report.bean;

import com.sumian.hw.improve.report.bean.SleepPackage;

import java.util.ArrayList;

/**
 * Created by sm
 * on 2018/5/7 16:08
 * desc:
 **/
public class SleepSegment {

    public int id;//睡眠数据 id
    public int fromTimeState;//状态 0：清醒，1：REM，2：浅睡，3：深睡
    public int showFromTimeIndicator;//indicator  显示时间
    public int toTimeState;//状态 0：清醒，1：REM，2：浅睡，3：深睡
    public int showToTimeIndicator;//indicator  显示时间
    public ArrayList<SleepPackage> sleepPackage;//整个睡眠数据段的睡眠特征包
    public int totalDuration;//整个睡眠段持续时间
    public boolean isClick;

    @Override
    public String toString() {
        return "SleepSegment{" +
            "id=" + id +
            ", fromTimeState=" + fromTimeState +
            ", showFromTimeIndicator=" + showFromTimeIndicator +
            ", toTimeState=" + toTimeState +
            ", showToTimeIndicator=" + showToTimeIndicator +
            ", sleepPackage=" + sleepPackage +
            ", totalDuration=" + totalDuration +
            ", isClick=" + isClick +
            '}';
    }
}
