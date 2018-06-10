package net.confide.push.activities;

import android.content.Intent;

import net.confide.common.app.Activity;
import net.confide.push.R;
import net.confide.push.fragment.user.UpdateInfoFragment;

public class UserActivity extends Activity {

    private UpdateInfoFragment mCurFragment;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_user;
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
