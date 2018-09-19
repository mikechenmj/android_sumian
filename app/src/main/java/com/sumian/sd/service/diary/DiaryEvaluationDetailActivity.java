package com.sumian.sd.service.diary;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.animation.FastOutLinearInInterpolator;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.sumian.hw.utils.JsonUtil;
import com.sumian.sd.app.AppManager;
import com.sumian.sd.h5.SimpleWebActivity;
import com.sumian.sd.network.callback.BaseResponseCallback;
import com.sumian.sd.service.cbti.video.LogUtil;
import com.sumian.sd.service.diary.bean.DiaryEvaluationData;
import com.sumian.sd.widget.webview.SBridgeHandler;
import com.sumian.sd.widget.webview.SWebView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/4 10:25
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class DiaryEvaluationDetailActivity extends SimpleWebActivity {

    /**
     * launch latest diary evaluation
     * @param context
     */
    public static void launchLatestEvaluation(Context context) {
        Call<DiaryEvaluationData> call = AppManager.getHttpService().getLatestDiaryEvaluation(null);
        call.enqueue(new BaseResponseCallback<DiaryEvaluationData>() {
            @Override
            protected void onSuccess(@Nullable DiaryEvaluationData response) {
                if (response == null) {
                    return;
                }
                launch(context, response.getId());
            }

            @Override
            protected void onFailure(int code, @NotNull String message) {
                LogUtils.d(message);
            }
        });
    }

    public static void launch(Context context, int id) {
        Intent intent = getLaunchIntent(context, id);
        ActivityUtils.startActivity(intent);
    }

    public static Intent getLaunchIntent(Context context, int id) {
        Map<String, Object> payload = new HashMap<>(2);
        payload.put("id", id);
        Map<String, Object> page = new HashMap<>(2);
        page.put("page", "weeklyAssess");
        page.put("payload", payload);
        return SimpleWebActivity.getLaunchIntentWithRouteData(context, JsonUtil.toJson(page), DiaryEvaluationDetailActivity.class);
    }

    @Override
    protected void registerHandler(@NonNull SWebView sWebView) {
        super.registerHandler(sWebView);
        sWebView.registerHandler("analyseReport", new SBridgeHandler() {
            @Override
            public void handler(String data) {
                LogUtils.d(data);
            }
        });
    }
}
