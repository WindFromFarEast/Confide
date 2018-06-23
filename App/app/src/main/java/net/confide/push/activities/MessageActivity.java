package net.confide.push.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;

import net.confide.common.app.Activity;
import net.confide.common.app.Fragment;
import net.confide.common.factory.model.Author;
import net.confide.factory.model.db.Group;
import net.confide.push.R;
import net.confide.push.fragment.main.GroupFragment;
import net.confide.push.fragment.message.ChatGroupFragment;
import net.confide.push.fragment.message.ChatUserFragment;

/**
 * 聊天界面
 */
public class MessageActivity extends Activity {

    //接收者ID的KEY
    public static final String KEY_RECEIVER_ID = "KEY_RECEIVER_ID";
    //标识是否是群聊的KEY
    public static final String KEY_RECEIVER_IS_GROUP = "KEY_RECEIVER_IS_GROUP";
    //接收者id
    private String mReceiverId;
    //是否为群聊
    private boolean mIsGroup;

    /**
     * 显示和指定用户的聊天界面
     * @param context 上下文
     * @param author 指定用户
     */
    public static void show(Context context, Author author) {
        if (author == null || context == null || TextUtils.isEmpty(author.getId()))
            return;
        Intent intent = new Intent(context, MessageActivity.class);
        intent.putExtra(KEY_RECEIVER_ID, author.getId());
        intent.putExtra(KEY_RECEIVER_IS_GROUP, false);
        context.startActivity(intent);
    }

    /**
     * 显示指定群的聊天界面
     * @param context 上下文
     * @param group 指定群
     */
    public static void show(Context context, Group group) {
        if (group == null || context == null || TextUtils.isEmpty(group.getId()))
            return;
        Intent intent = new Intent(context, MessageActivity.class);
        intent.putExtra(KEY_RECEIVER_ID, group.getId());
        intent.putExtra(KEY_RECEIVER_IS_GROUP, true);
        context.startActivity(intent);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_message;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        mReceiverId = bundle.getString(KEY_RECEIVER_ID);
        mIsGroup = bundle.getBoolean(KEY_RECEIVER_IS_GROUP);
        return !TextUtils.isEmpty(mReceiverId);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setTitle("");
        Fragment fragment;
        //判断当前Activity下的Fragment应该是群聊Fragment还是单聊Fragment
        if (mIsGroup) {
            fragment = new ChatGroupFragment();
        } else {
            fragment = new ChatUserFragment();
        }
        //从Activity传递参数到Fragment中
        Bundle bundle = new Bundle();
        bundle.putString(KEY_RECEIVER_ID, mReceiverId);
        fragment.setArguments(bundle);
        //添加对应的Fragment
        getSupportFragmentManager().beginTransaction()
                .add(R.id.lay_container, fragment)
                .commit();
    }
}
