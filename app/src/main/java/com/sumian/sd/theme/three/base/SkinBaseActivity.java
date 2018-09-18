package com.sumian.sd.theme.three.base;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.LayoutInflaterCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.sumian.common.base.BaseActivity;
import com.sumian.sd.theme.three.IDynamicNewView;
import com.sumian.sd.theme.three.ISkinUpdate;
import com.sumian.sd.theme.three.SkinConfig;
import com.sumian.sd.theme.three.attr.base.DynamicAttr;
import com.sumian.sd.theme.three.loader.SkinInflaterFactory;
import com.sumian.sd.theme.three.loader.SkinManager;
import com.sumian.sd.theme.three.utils.SkinL;
import com.sumian.sd.theme.three.utils.SkinResourcesUtils;

import java.util.List;


/**
 * Created by _SOLID
 * Date:2016/4/14
 * Time:10:24
 * Your activity need extend
 */
public abstract class SkinBaseActivity extends BaseActivity implements ISkinUpdate, IDynamicNewView {

    private SkinInflaterFactory mSkinInflaterFactory;

    private final static String TAG = SkinBaseActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mSkinInflaterFactory = new SkinInflaterFactory(this);
        LayoutInflaterCompat.setFactory2(getLayoutInflater(), mSkinInflaterFactory);
        super.onCreate(savedInstanceState);
        //changeStatusColor();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SkinManager.getInstance().attach(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SkinManager.getInstance().detach(this);
        mSkinInflaterFactory.clean();
    }

    @Override
    public void onThemeUpdate() {
        SkinL.e(TAG, "onThemeUpdate");
        mSkinInflaterFactory.applySkin();
        //changeStatusColor();
    }

    public SkinInflaterFactory getInflaterFactory() {
        return mSkinInflaterFactory;
    }

    public void changeStatusColor() {
        if (!SkinConfig.isCanChangeStatusColor()) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int color = SkinResourcesUtils.getColorPrimaryDark();
            if (color != -1) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(SkinResourcesUtils.getColorPrimaryDark());
            }
        }
    }

    @Override
    public void dynamicAddView(View view, List<DynamicAttr> pDAttrs) {
        mSkinInflaterFactory.dynamicAddSkinEnableView(this, view, pDAttrs);
    }

    @Override
    public void dynamicAddView(View view, String attrName, int attrValueResId) {
        mSkinInflaterFactory.dynamicAddSkinEnableView(this, view, attrName, attrValueResId);
    }

    @Override
    public void dynamicAddFontView(TextView textView) {
        mSkinInflaterFactory.dynamicAddFontEnableView(this, textView);
    }

}
