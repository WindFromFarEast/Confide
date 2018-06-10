package net.confide.push.activities;


import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;

import net.confide.common.app.Activity;
import net.confide.common.app.Fragment;
import net.confide.push.R;
import net.confide.push.fragment.account.AccountTrigger;
import net.confide.push.fragment.account.LoginFragment;
import net.confide.push.fragment.account.RegisterFragment;
import net.qiujuer.genius.ui.compat.UiCompat;

import butterknife.BindView;

/**
 * 用户的登录、注册界面
 */
public class AccountActivity extends Activity implements AccountTrigger {

    private Fragment mCurFragment;
    private Fragment mLoginFragment;
    private Fragment mRegisterFragment;

    @BindView(R.id.im_bg)
    ImageView mBg;

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
        //初始化Fragment
        //默认情况下显示的Fragment是登录Fragment
        mCurFragment = mLoginFragment = new LoginFragment();
        //显示当前Fragment(默认为登录Fragment)
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.lay_container, mCurFragment)
                .commit();
        //初始化背景图片
        Glide.with(this).load(R.drawable.bg_src_tianjin)
                .centerCrop()//居中剪切
                .into(new ViewTarget<ImageView, GlideDrawable>(mBg) {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                //获取Glide的Drawable
                Drawable drawable = resource.getCurrent();
                //使用适配类进行包装
                drawable = DrawableCompat.wrap(drawable);
                drawable.setColorFilter(UiCompat.getColor(getResources(), R.color.colorAccent),
                        PorterDuff.Mode.SCREEN);//设置着色效果和颜色,蒙板模式
                this.view.setImageDrawable(drawable);
            }
        });
    }

    /**
     * 切换当前正在显示的Fragment
     */
    @Override
    public void triggerView() {
        //要切换成的Fragment
        Fragment fragment;
        if (mCurFragment == mLoginFragment) {
            //当前显示的Fragment是登录Fragment
            if (mRegisterFragment == null) {
                //注册Fragment默认情况下为null,需要初始化
                mRegisterFragment = new RegisterFragment();
            }
            fragment = mRegisterFragment;
        } else {
            //当前显示的Fragment不是登录Fragment
            fragment = mLoginFragment;
        }
        //重新赋值当前正在显示的Fragment
        mCurFragment = fragment;
        //切换Fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.lay_container, fragment)
                .commit();
    }
}
