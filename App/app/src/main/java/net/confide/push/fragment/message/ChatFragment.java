package net.confide.push.fragment.message;


import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import net.confide.common.app.Fragment;
import net.confide.common.widget.PortraitView;
import net.confide.common.widget.adapter.TextWatcherAdapter;
import net.confide.push.R;
import net.confide.push.activities.MessageActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 聊天界面Fragment的基类
 */
public abstract class ChatFragment extends Fragment implements AppBarLayout.OnOffsetChangedListener{

    //接收者id
    protected String mReceiverId;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    @BindView(R.id.appbar)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.edit_content)
    EditText mContent;
    @BindView(R.id.btn_submit)
    View mSubmit;

    @Override
    protected void initArgs(Bundle bundle) {
        super.initArgs(bundle);
        mReceiverId = bundle.getString(MessageActivity.KEY_RECEIVER_ID);
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        initToolbar();
        initAppbar();
        initEditContent();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    /**
     * 初始化Toolbar
     */
    protected void initToolbar() {
        Toolbar toolbar = mToolbar;
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }

    private void initAppbar() {
        //给AppBar设置监听
        mAppBarLayout.addOnOffsetChangedListener(this);
    }

    /**
     * 初始化内容输入框
     */
    private void initEditContent() {
        mContent.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                String content = s.toString().trim();
                boolean needSendMsg = !TextUtils.isEmpty(content);
                mSubmit.setActivated(needSendMsg);
            }
        });
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

    }

    @OnClick(R.id.btn_face)
    void onFaceClick() {
        // TODO
    }

    @OnClick(R.id.btn_record)
    void onRecordClick() {
        // TODO
    }

    @OnClick(R.id.btn_submit)
    void onSubmitClick() {
        if (mSubmit.isActivated()) {
            //发送消息
        } else {
            onMoreClick();
        }
    }

    private void onMoreClick() {
        // TODO
    }
}
