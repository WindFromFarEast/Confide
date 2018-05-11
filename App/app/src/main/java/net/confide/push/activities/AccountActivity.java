package net.confide.push.activities;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.bumptech.glide.Glide;
import com.yalantis.ucrop.UCrop;

import net.confide.common.app.Activity;
import net.confide.push.R;
import net.confide.push.fragment.account.UpdateInfoFragment;

public class AccountActivity extends Activity {

    private UpdateInfoFragment mCurFragment;

    /**
     * 账户Activity显示的接口
     * @param context 从context跳转到AccountActivity
     */
    public static void show(Context context) {
        context.startActivity(new Intent(context, AccountActivity.class));
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_account;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mCurFragment = new UpdateInfoFragment();
        //显示用户信息更新的Fragment
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.lay_container, mCurFragment)
                .commit();
    }

    /**
     * 剪切图片后的回调
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //将图片剪切的回调交给UpdateInfoFragment处理
        mCurFragment.onActivityResult(requestCode, resultCode, data);
    }

}
