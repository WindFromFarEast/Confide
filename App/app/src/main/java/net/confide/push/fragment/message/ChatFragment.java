package net.confide.push.fragment.message;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import net.confide.common.app.PresenterFragment;
import net.confide.common.widget.PortraitView;
import net.confide.common.widget.adapter.TextWatcherAdapter;
import net.confide.common.widget.recycler.RecyclerAdapter;
import net.confide.factory.model.db.Message;
import net.confide.factory.model.db.User;
import net.confide.factory.persistence.Account;
import net.confide.factory.presenter.message.ChatContract;
import net.confide.push.R;
import net.confide.push.activities.MessageActivity;
import net.qiujuer.genius.ui.compat.UiCompat;
import net.qiujuer.genius.ui.widget.Loading;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 聊天界面Fragment的基类
 * 由于不知道当前界面是群聊还是用户单聊，无法确定InitModel，因此用到了泛型抛出
 */
public abstract class ChatFragment<InitModel> extends PresenterFragment<ChatContract.Presenter>
        implements AppBarLayout.OnOffsetChangedListener, ChatContract.View<InitModel>{

    //接收者id
    protected String mReceiverId;

    protected Adapter mAdapter;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;
    @BindView(R.id.appbar)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.collapsingToolbarLayout)
    CollapsingToolbarLayout mCollapsingLayout;
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
        //初始化RecyclerView
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new Adapter();
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {
        super.initData();
        //Presenter的初始化方法
        mPresenter.start();
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

    /**
     *
     */
    @OnClick(R.id.btn_submit)
    void onSubmitClick() {
        if (mSubmit.isActivated()) {
            //激活状态下可以发送消息
            String content = mContent.getText().toString();
            mContent.setText("");
            mPresenter.pushText(content);
        } else {
            onMoreClick();
        }
    }

    private void onMoreClick() {
        // TODO
    }

    @Override
    public RecyclerAdapter<Message> getRecyclerAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChange() {
        //界面没有占位控件,RecyclerView一直显示,不需要做任何操作
    }

    /**
     * 聊天列表RecyclerView的Adapter
     */
    private class Adapter extends RecyclerAdapter<Message> {

        @Override
        protected int getItemViewType(int position, Message message) {
            //是否是我自己发的消息,是为右边布局类型,否为左边布局类型
            boolean isRight = Objects.equals(message.getSender().getId(), Account.getUserId());
            switch (message.getType()) {
                case Message.TYPE_STR: {
                    //文字消息
                    return isRight ? R.layout.cell_chat_text_right : R.layout.cell_chat_text_left;
                }
                case Message.TYPE_AUDIO: {
                    //语音消息
                    return isRight ? R.layout.cell_chat_audio_right : R.layout.cell_chat_audio_left;
                }
                case Message.TYPE_PIC: {
                    //图片消息
                    return isRight ? R.layout.cell_chat_pic_right : R.layout.cell_chat_pic_left;
                }
                default: {
                    //文件消息
                    return isRight ? R.layout.cell_chat_text_right : R.layout.cell_chat_text_left;
                }
            }
        }

        @Override
        protected ViewHolder<Message> onCreateViewHolder(View root, int viewType) {
            switch (viewType) {
                //文本消息的情况下,左右两种布局都用TextHolder实现数据绑定
                case R.layout.cell_chat_text_left:
                case R.layout.cell_chat_text_right: {
                    return new TextHolder(root);
                }
                case R.layout.cell_chat_audio_left:
                case R.layout.cell_chat_audio_right: {
                    return new AudioHolder(root);
                }
                case R.layout.cell_chat_pic_left:
                case R.layout.cell_chat_pic_right: {
                    return new PicHolder(root);
                }
                default: return new TextHolder(root);
            }
        }
    }

    /**
     * 基础ViewHolder,包含头像和Loading框
     */
    class BaseHolder extends RecyclerAdapter.ViewHolder<Message> {

        @BindView(R.id.im_portrait)
        PortraitView mPortrait;
        @Nullable
        @BindView(R.id.loading)
        Loading mLoading;

        public BaseHolder(View itemView) {
            super(itemView);
        }

        /**
         * 数据绑定
         * @param message
         */
        @Override
        protected void onBind(Message message) {
            //进行头像加载
            User sender = message.getSender();
            sender.load();
            mPortrait.setup(Glide.with(ChatFragment.this), sender);

            if (mLoading != null) {
                //Loading不为空说明应该在右边
                int status = message.getStatus();
                if (status == Message.STATUS_DONE) {
                    //消息发送成功
                    mLoading.setVisibility(View.GONE);
                    mLoading.stop();
                } else if (status == Message.STATUS_CREATED) {
                    //消息正在发送
                    mLoading.setVisibility(View.VISIBLE);
                    mLoading.setProgress(0);
                    mLoading.setForegroundColor(UiCompat.getColor(getResources(), R.color.colorAccent));
                    mLoading.start();
                } else if (status == Message.STATUS_FAILED) {
                    //消息发送失败,允许重新发送
                    mLoading.setVisibility(View.VISIBLE);
                    mLoading.setProgress(1);
                    mLoading.setForegroundColor(UiCompat.getColor(getResources(), R.color.alertImportant));
                    mLoading.stop();
                }
                //消息发送失败时才允许重新发送
                mPortrait.setEnabled(status == Message.STATUS_FAILED);
            }
        }

        /**
         * 点击头像重新发送消息
         */
        @OnClick(R.id.im_portrait)
        void onRePushClick() {
            if (mLoading != null && mPresenter.rePush(mData)) {
                //状态改变重新刷新界面
                updateData(mData);
            }
        }
    }

    /**
     * 文本消息的ViewHolder,继承自基础的BaseHolder
     */
    class TextHolder extends BaseHolder {

        @BindView(R.id.txt_content)
        TextView mContent;

        public TextHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            super.onBind(message);
            //将内容设置到布局上
            mContent.setText(message.getContent());
        }
    }

    /**
     * 语音消息的ViewHolder,继承自基础的BaseHolder
     */
    class AudioHolder extends BaseHolder {

        public AudioHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            super.onBind(message);
            // TODO
        }
    }

    /**
     * 图片消息的ViewHolder,继承自基础的BaseHolder
     */
    class PicHolder extends BaseHolder {

        public PicHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            super.onBind(message);
            //TODO
        }
    }

}
