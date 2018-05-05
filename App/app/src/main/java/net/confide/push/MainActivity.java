package net.confide.push;

import android.support.design.widget.BottomNavigationView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;

import net.confide.common.app.Activity;
import net.confide.common.widget.PortraitView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 主界面
 */
public class MainActivity extends Activity {

    @BindView(R.id.appbar)
    View mLayAppbar;

    @BindView(R.id.im_portrait)
    PortraitView mPortrait;

    @BindView(R.id.txt_title)
    TextView mTitle;

    @BindView(R.id.lay_container)
    FrameLayout mContainer;

    @BindView(R.id.navigation)
    BottomNavigationView mNavigation;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        //使用Glide加载导航栏背景图片
        Glide.with(this).load(R.drawable.bg_src_morning).centerCrop().
                into(new ViewTarget<View, GlideDrawable>(mLayAppbar) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        //图片加载完成后将其设置到导航栏
                        this.view.setBackground(resource.getCurrent());
                    }
                });
    }

    /**
     * 点击搜索按钮的回调方法
     */
    @OnClick(R.id.im_search)
    void onSearchMenuClick() {

    }

    /**
     * 点击添加按钮的回调方法
     */
    @OnClick(R.id.btn_action)
    void onActionClick() {

    }
}
