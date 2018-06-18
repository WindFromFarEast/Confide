package net.confide.push.activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import net.confide.common.factory.model.Author;
import net.confide.push.R;

/**
 * 用户聊天界面
 */
public class MessageActivity extends AppCompatActivity {

    /**
     * 显示和指定用户的聊天界面
     * @param context 上下文
     * @param author 指定用户
     */
    public static void show(Context context, Author author) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
    }
}
