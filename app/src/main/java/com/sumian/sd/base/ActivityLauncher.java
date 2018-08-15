package com.sumian.sd.base;

import android.app.Activity;
import android.content.Intent;

/**
 * <pre>
 *     @author : Zhan Xuzhao
 *     e-mail : xuzhao.z@sumian.com
 *     time   : 2018/6/5 16:49
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public interface ActivityLauncher {

    void startActivity(Intent intent);

    void startActivityForResult(Intent intent, int requestCode);

    Activity getActivity();
}
