package net.confide.push.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;

import net.confide.common.app.Activity;
import net.confide.common.widget.PortraitView;
import net.confide.factory.persistence.Account;
import net.confide.push.R;
import net.confide.push.activities.AccountActivity;
import net.confide.push.fragment.assist.PermissionFragment;
import net.confide.push.fragment.main.ActiveFragment;
import net.confide.push.fragment.main.ContactFragment;
import net.confide.push.fragment.main.GroupFragment;
import net.confide.push.helper.NavHelper;
import net.qiujuer.genius.ui.Ui;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 主界面
 */
public class MainActivity extends Activity implements BottomNavigationView.OnNavigationItemSelectedListener
        , NavHelper.OnTabChangedListener<Integer> {

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

    //浮动按钮
    @BindView(R.id.btn_action)
    net.qiujuer.genius.ui.widget.FloatActionButton mAction;

    //Fragment调度和重用的工具类
    private NavHelper<Integer> mNavHelper;

    /**
     * MainActivity的跳转接口
     * @param context
     */
    public static void show(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        if (Account.isCompleted()) {
            //用户信息完整,走正常流程
            return super.initArgs(bundle);
        } else {
            //用户信息不完整
            UserActivity.show(this);//跳转到用户信息界面
            //返回false,销毁当前界面
            return false;
        }
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        //初始化调度Fragment的工具类
        mNavHelper = new NavHelper<>(this, R.id.lay_container, getSupportFragmentManager(),this);
        //
        mNavHelper.add(R.id.action_home, new NavHelper.Tab<>(ActiveFragment.class, R.string.title_home))
                .add(R.id.action_group, new NavHelper.Tab<>(GroupFragment.class, R.string.title_group))
                .add(R.id.action_contact, new NavHelper.Tab<>(ContactFragment.class, R.string.title_contact));
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
        //动态权限判断
        PermissionFragment.haveAll(this, getSupportFragmentManager());
    }

    /**
     * 初始化数据
     */
    @Override
    protected void initData() {
        super.initData();
        //从底部导航栏中获取Menu菜单,初始化第一次点击菜单子项
        Menu menu = mNavigation.getMenu();
        //第一次进入界面时,默认显示Home子项,这个方法将回调onNavigationItemSelected('home对应的MenuItem');
        menu.performIdentifierAction(R.id.action_home, 0);
        //初始化头像加载
        mPortrait.setup(Glide.with(this), Account.getUser());
    }

    /**
     * 点击头像回调
     */
    @OnClick(R.id.im_portrait)
    void onPortraitClick() {
        PersonalActivity.show(this, Account.getUserId());
    }

    /**
     * 点击搜索按钮的回调方法
     */
    @OnClick(R.id.im_search)
    void onSearchMenuClick() {
        int type = Objects.equals(mNavHelper.getCurrentTab().extra, R.string.title_group) ?
                SearchActivity.TYPE_GROUP : SearchActivity.TYPE_USER;
        SearchActivity.show(this, type);
    }

    /**
     * 点击浮动按钮的回调方法
     */
    @OnClick(R.id.btn_action)
    void onActionClick() {
        //首先判断当前界面是群还是联系人界面
        //若是群界面则打开群创建界面，若是其他则打开添加用户的界面
        if (Objects.equals(mNavHelper.getCurrentTab().extra, R.string.title_group)) {
            //TODO 打开群创建界面
        } else {
            SearchActivity.show(this, SearchActivity.TYPE_USER);
        }
    }

    /**
     * 底部导航栏的子项被选中时回调的方法
     * @param item 点击的菜单子项
     * @return 是否触发回调
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //将item的点击事件分发给工具类处理,在这里面将会处理Fragment的切换
        return mNavHelper.performClickMenu(item.getItemId());
    }

    /**
     * NavHelper处理了底部导航栏item的点击事件后回调的方法,在这里处理标题栏文字的改变
     * @param newTab
     * @param oldTab
     */
    @Override
    public void onTabChanged(NavHelper.Tab<Integer> newTab, NavHelper.Tab<Integer> oldTab) {
        //改变标题栏文字
        mTitle.setText(newTab.extra);
        //浮动按钮的动画
        float transY = 0;
        float rotation = 0;
        if (newTab.extra.equals(R.string.title_home)) {
            transY = Ui.dipToPx(getResources(), 76);
        } else {
            if (newTab.extra.equals(R.string.title_group)) {
                mAction.setImageResource(R.drawable.ic_group_add);
                rotation = -360;
            } else {
                mAction.setImageResource(R.drawable.ic_contact_add);
                rotation = 360;
            }
        }
        //播放动画
        mAction.animate().rotation(rotation)
                .translationY(transY)
                .setInterpolator(new AnticipateOvershootInterpolator(1))
                .setDuration(480)
                .start();
    }
}
