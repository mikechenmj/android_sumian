package com.sumian.app.upgrade.dialog;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sumian.sleepdoctor.R;
import com.sumian.app.widget.BaseDialogFragment;

import java.util.Locale;

import butterknife.BindView;

/**
 * Created by jzz
 * on 2017/11/3.
 * <p>
 * desc:
 */

public class VersionDialog extends BaseDialogFragment {

    private static final String TAG = VersionDialog.class.getSimpleName();

    TextView mTvFirmwareTitle;
    TextView mTvProgress;
    ProgressBar mProgressBar;

    private int mDialogType;
    private String mDialogTitle;

    public static VersionDialog newInstance(int dialogType, String title) {
        VersionDialog versionDialog = new VersionDialog();
        Bundle args = new Bundle();
        args.putInt("dialogType", dialogType);
        args.putString("dialogTitle", title);
        versionDialog.setArguments(args);
        return versionDialog;
    }

    @Override
    protected void initBundle(Bundle arguments) {
        super.initBundle(arguments);
        this.mDialogType = arguments.getInt("dialogType", 0);
        this.mDialogTitle = arguments.getString("dialogTitle");
    }

    @Override
    protected int getLayout() {
        setCancelable(false);
        return R.layout.hw_lay_dialog_version;
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        mTvFirmwareTitle = rootView.findViewById(R.id.tv_version_title);
        mTvProgress = rootView.findViewById(R.id.tv_progress);
        mProgressBar = rootView.findViewById(R.id.progressBar);
    }

    @Override
    protected void initData() {
        super.initData();
        this.mTvFirmwareTitle.setText(mDialogType == 0x00 ? mDialogTitle : getString(R.string.firmware_upgrade_title_hint));
    }

    public void updateProgress(int progress) {
        runUiThread(() -> {
            this.mTvProgress.setText(String.format(Locale.getDefault(), "%d%s", progress, "%"));
            this.mProgressBar.setSecondaryProgress(progress);
        });
    }

    public void cancel() {
        dismiss();
    }
}
