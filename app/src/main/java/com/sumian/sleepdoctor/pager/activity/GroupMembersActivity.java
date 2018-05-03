package com.sumian.sleepdoctor.pager.activity;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.sumian.sleepdoctor.R;
import com.sumian.sleepdoctor.account.bean.UserProfile;
import com.sumian.sleepdoctor.base.BaseActivity;
import com.sumian.sleepdoctor.chat.bean.PinYinUserProfile;
import com.sumian.sleepdoctor.chat.widget.IndexView;
import com.sumian.sleepdoctor.pager.adapter.MemberAdapter;
import com.sumian.sleepdoctor.pager.decoration.MemberDecoration;
import com.sumian.sleepdoctor.widget.TitleBar;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;

/**
 * Created by sm
 * on 2018/2/2.
 * desc:
 */

public class GroupMembersActivity extends BaseActivity implements TitleBar.OnBackListener, IndexView.OnIndexTouchListener {

    public static final String ARGS_MEMBERS = "group_members";

    private static final String SPLIT_HEAD = "~";
    private static final String DEFAULT_CHAR = "#";

    @BindView(R.id.title_bar)
    TitleBar mTitleBar;
    @BindView(R.id.recycler)
    RecyclerView mRecycler;

    @BindView(R.id.index_view)
    IndexView mIndexView;

    @BindView(R.id.tv_index_show)
    TextView mTvIndexShow;

    private List<UserProfile> mMembers;

    private MemberAdapter mMemberAdapter;
    private MemberDecoration mDecoration;

    private static HanyuPinyinOutputFormat mFormat;

    static {
        mFormat = new HanyuPinyinOutputFormat();
        mFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        mFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        mFormat.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);
    }


    @Override
    protected boolean initBundle(Bundle bundle) {
        this.mMembers = bundle.getParcelableArrayList(ARGS_MEMBERS);
        return super.initBundle(bundle);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main_group_members;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        mTitleBar.addOnBackListener(this);
        mRecycler.setLayoutManager(new LinearLayoutManager(root.getContext()));
        mRecycler.setItemAnimator(new DefaultItemAnimator());
        mRecycler.addItemDecoration(mDecoration = new MemberDecoration());
        mRecycler.setAdapter(mMemberAdapter = new MemberAdapter(this));
        mIndexView.setOnIndexTouchListener(this);
    }

    @Override
    protected void initData() {
        super.initData();

        if (mMembers == null || mMembers.isEmpty()) {
            mIndexView.setVisibility(View.GONE);
            return;
        }

        List<PinYinUserProfile> pinyinFormatMembers = new ArrayList<>();

        PinYinUserProfile pinYinUserProfile;
        for (UserProfile member : mMembers) {

            pinYinUserProfile = new PinYinUserProfile();

            String nickname = member.nickname;
            if (TextUtils.isEmpty(nickname)) continue;

            pinYinUserProfile.userProfile = member;
            String convertToPinyin = convertToPinyin(nickname, SPLIT_HEAD);
            pinYinUserProfile.pinyin = convertToPinyin;
            String firstChar = convertToPinyin.substring(1, 2);
            pinYinUserProfile.firstChar = firstChar.matches("[a-zA-Z]") ? firstChar : DEFAULT_CHAR;

            pinyinFormatMembers.add(pinYinUserProfile);
        }

        Collections.sort(pinyinFormatMembers);

       // Collections.sort(pinyinFormatMembers, (o1, o2) -> o2.userProfile.role - o1.userProfile.role);

        mDecoration.addAllItems(pinyinFormatMembers);
        mMemberAdapter.addAll(pinyinFormatMembers);
    }

    /**
     * 字符串转化为拼音
     * 字符串中英文不转换为拼音
     *
     * @param text      可能含有拼音的字符串
     * @param splitHead 每个中文转化为拼音后头部添加的分割符号
     *                  eg: "V好De","~"->"~V~hao~De"
     * @return 转化为拼音后的字符串
     */
    private String convertToPinyin(String text, String splitHead) {
        if (TextUtils.isEmpty(text))
            return "";

        char[] charArray = text.toCharArray();

        StringBuilder sb = new StringBuilder();
        boolean canAdd = true;
        for (char c : charArray) {
            String temp = Character.toString(c);
            if (temp.matches("[\u4E00-\u9FA5]+")) {
                String py;
                try {
                    String[] pys = PinyinHelper.toHanyuPinyinStringArray(c, mFormat);
                    py = pys[0];
                } catch (Exception e) {
                    e.printStackTrace();
                    py = " ";
                }
                sb.append(splitHead);
                sb.append(py);
                canAdd = true;
            } else {
                if (canAdd) {
                    sb.append(splitHead);
                    canAdd = false;
                }
                sb.append(temp);
            }
        }
        return sb.toString().trim();
    }

    @Override
    public void onBack(View v) {
        finish();
    }

    @Override
    public void onIndexTouchMove(char indexLetter) {
        String str = Character.toString(indexLetter).toLowerCase();
        List<PinYinUserProfile> patients = mMemberAdapter.getItems();
        int position = -1;
        int size = patients.size();
        for (int i = 0; i < size; i++) {
            PinYinUserProfile friend = patients.get(i);
           // if (friend.firstChar.startsWith(str) && friend.userProfile.role == 0) {
          //      position = i;
          //      break;
          //  }
        }

        if (position >= 0) {
            RecyclerView.LayoutManager layoutManager = mRecycler.getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                ((LinearLayoutManager) layoutManager).scrollToPositionWithOffset(position, 0);
            } else {
                mRecycler.smoothScrollToPosition(position);
            }

            mTvIndexShow.setText(str);
            mTvIndexShow.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onIndexTouchUp() {
        mTvIndexShow.setVisibility(View.GONE);
    }

}
