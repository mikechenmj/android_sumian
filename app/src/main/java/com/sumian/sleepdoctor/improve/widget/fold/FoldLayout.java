package com.sumian.sleepdoctor.improve.widget.fold;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.improve.widget.textview.FoldTextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sm
 * on 2018/5/30 13:54
 * desc:
 **/
public class FoldLayout extends LinearLayout implements View.OnClickListener, FoldTextView.OnFoldListener {

    @BindView(R.id.tv_summary)
    FoldTextView tvSummary;
    @BindView(R.id.tv_show_more)
    TextView tvShowMore;

    public FoldLayout(Context context) {
        this(context, null);
    }

    public FoldLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FoldLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        ButterKnife.bind(inflate(context, R.layout.lay_fold_text_container, this));

        tvSummary.setShowLineRule(4);
        tvSummary.setOnFoldListener(this);
        if (tvSummary.getLineHeight() > 4) {
            tvShowMore.setOnClickListener(this);
            tvShowMore.setVisibility(VISIBLE);
        } else {
            tvShowMore.setVisibility(GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if (tvShowMore.getTag() == null) {
            tvSummary.foldContent();
            tvShowMore.setTag(true);
        } else {
            tvSummary.unfoldContent();
            tvShowMore.setTag(null);
        }
    }

    @Override
    public void fold() {
        tvShowMore.setTag(true);
    }

    @Override
    public void unfold() {
        tvShowMore.setTag(null);
    }
}
