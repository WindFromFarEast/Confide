package net.confide.push;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;

import net.confide.common.app.Activity;
import net.confide.common.widget.PortraitView;
import net.confide.push.fragment.main.ActiveFragment;
import net.confide.push.fragment.main.ContactFragment;
import net.confide.push.fragment.main.GroupFragment;
import net.confide.push.helper.NavHelper;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 主界面
 */
public class MainActivity extends Activity implements BottomNavigationView.OnNavigationItemSelectedListener {

    //主界面布局中的各个控件
    //状态栏和标题栏的父容器
    @BindView(R.id.appbar)
    View mLayAppbar;

    //用户头像
    @BindView(R.id.im_portrait)
    PortraitView mPortrait;

    //标题栏文字
    @BindView(R.id.txt_title)
    TextView mTitle;

    //内容布局
    @BindView(R.id.lay_container)
    FrameLayout mContainer;

    //底部导航栏
    @BindView(R.id.navigation)
    BottomNavigationView mNavigation;

    //Fragment调度和重用的工具类
    private NavHelper mNavHelper;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        //初始化Fragment的工具类
        mNavHelper = new NavHelper();
        //使用Glide加载导航栏背景图片
        Glide.with(this).load(R.drawable.bg_src_morning).centerCrop().
                into(new ViewTarget<View, GlideDrawable>(mLayAppbar) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        //图片加载完成后将其设置到导航栏
                        this.view.setBackground(resource.getCurrent());
                    }
                });
        //为底部导航栏设置菜单子项监听器
        mNavigation.setOnNavigationItemSelectedListener(this);
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

    /**
     * 底部导航栏的子项被选中时回调的方法
     * @param item 点击的菜单子项
     * @return 是否触发回调
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //将item的点击事件分发给工具类处理
        return mNavHelper.performClickMenu(item.getItemId());
    }
}
